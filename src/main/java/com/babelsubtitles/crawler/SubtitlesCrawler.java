package com.babelsubtitles.crawler;


import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
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
    private static final String SEASONS_URI="ajax_getSeasons.php?showID=";
    private static final String CHAPTER_PREFIX = "ajax_getEpisodes.php?showID=";
    private static final String CHAPTER_SUFFIX = "&season=";
    private Pattern p = Pattern.compile("option value=\"(\\d+?)\" >(.*?)</option>", Pattern.DOTALL + Pattern.MULTILINE);
    private Pattern p2 = Pattern.compile("<option>(\\d+)</option>", Pattern.DOTALL + Pattern.MULTILINE);

    private HttpClient httpClient;



    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        this.httpClient = vertx.createHttpClient(new HttpClientOptions());

        HttpClientRequest request = this.httpClient.request(HttpMethod.GET, 80, HOST, "");
        request
                .toObservable()
                .flatMap(success -> success.toObservable().reduce(Buffer.buffer(), (Buffer b1, Buffer b2) -> b1.appendBuffer(b2)))
                .flatMap(b -> extractSeries(b))
                .flatMap(s -> getSeasons(s))
                .flatMap(s -> getChapters(s))
                .forEach(r -> logger.debug(r.toString()));

        request.end();

    }

    public void run() {

    }

    private Observable<SerieInfo> extractSeries(Buffer buffer){
        String html = buffer.getString(0, buffer.length());
        Matcher matcher = p.matcher(html);
        List<SerieInfo> series = new ArrayList<>();
        while(matcher.find()){
            SerieInfo serieInfo = new SerieInfo(matcher.group(1), matcher.group(2));
            series.add(serieInfo);
        }
        return Observable.<SerieInfo>from(series);
    }
    private Observable<SerieInfo> getSeasons(SerieInfo s){
        Observable<SerieInfo> serieInfoObservable = Observable.<SerieInfo>create(subscriber -> {
            httpClient.getNow(80, HOST, "/" + SEASONS_URI + s.getId(), response ->
                            response.bodyHandler(buffer -> subscriber.onNext(SerieInfo.SerieBuilder.create().withSerieInfo(s).withSeasons(extractSeason(buffer)).build()))
            );
        });
        return serieInfoObservable;
    }
    private List<String> extractSeason(Buffer buffer){
        String html = buffer.getString(0, buffer.length());
        Matcher matcher = p2.matcher(html);
        List<String> seasons = new ArrayList<>();
        while(matcher.find()){
            seasons.add(matcher.group(1));
        }
        return seasons;
    }

    private Observable<SerieInfo> getChapters(SerieInfo serie) {
        serie.getSeasons().stream()
                .forEach(
                        (s) -> { httpClient.getNow(80, HOST, "/" + CHAPTER_PREFIX + serie.getId() + CHAPTER_SUFFIX + s,
                                response -> response.bodyHandler(
                                        buffer -> logger.debug(buffer.getString(0,buffer.length()))));
                });
        return Observable.just(serie);
    }
}