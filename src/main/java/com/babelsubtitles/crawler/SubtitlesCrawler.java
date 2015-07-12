package com.babelsubtitles.crawler;


import com.babelsubtitles.crawler.extractor.ChapterExtractor;
import com.babelsubtitles.crawler.extractor.ChapterExtractorSubsWiki;
import com.babelsubtitles.crawler.extractor.SeasonExtractor;
import com.babelsubtitles.crawler.extractor.SeasonExtractorSubsWiki;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Javi on 02/07/2015.
 */
public class SubtitlesCrawler extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(SubtitlesCrawler.class);

    private static final String HOST="www.subswiki.com";

    private Pattern p = Pattern.compile("option value=\"(\\d+?)\" >(.*?)</option>", Pattern.DOTALL + Pattern.MULTILINE);

    private HttpClient httpClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        this.httpClient = vertx.createHttpClient(new HttpClientOptions());
        ChapterExtractor chapterExtractor = new ChapterExtractorSubsWiki(httpClient);
        SeasonExtractor seasonExtractor = new SeasonExtractorSubsWiki(httpClient);

        HttpClientRequest request = this.httpClient.request(HttpMethod.GET, 80, HOST, "");
        request
                .toObservable()
                .flatMap(success -> success.toObservable().reduce(Buffer.buffer(), (Buffer b1, Buffer b2) -> b1.appendBuffer(b2)))
                .flatMap(b -> extractSeries(b))
                .flatMap(s -> seasonExtractor.getSeasons(s))
                .flatMap(s -> chapterExtractor.getChapters(s))
                .forEach(r -> logger.debug(r.toString()));
        request.end();

    }

    private Observable<Serie> extractSeries(Buffer buffer){
        String html = buffer.getString(0, buffer.length());
        Matcher matcher = p.matcher(html);
        List<Serie> series = new ArrayList<>();
        while(matcher.find()){
            Serie serieInfo = new Serie(matcher.group(1), matcher.group(2));
            series.add(serieInfo);
        }
        return Observable.<Serie>from(series);
    }

}