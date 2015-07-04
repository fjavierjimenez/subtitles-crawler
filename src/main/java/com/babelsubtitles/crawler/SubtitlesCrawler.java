package com.babelsubtitles.crawler;

import io.vertx.core.Handler;
import io.vertx.core.VoidHandler;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import io.vertx.rxjava.core.http.HttpClientResponse;
import rx.Observable;
import rx.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Javi on 02/07/2015.
 */
public class SubtitlesCrawler {
    private static final String HOST="www.subswiki.com";
    private static final String SEASONS_URI="ajax_getSeasons.php?showID=";
    private static final String CHAPTER_PREFIX = "ajax_getEpisodes.php?showID=";
    private static final String CHAPTER_SUFFIX = "&season=";
    private Pattern p = Pattern.compile("option value=\"(\\d+?)\" >(.*?)</option>", Pattern.DOTALL + Pattern.MULTILINE);
    private Pattern p2 = Pattern.compile("<option>(\\d+)</option>", Pattern.DOTALL + Pattern.MULTILINE);
    private HttpClient httpClient = Vertx.vertx().createHttpClient(new HttpClientOptions());

    public void run() {
        HttpClientRequest request = httpClient.request(HttpMethod.GET, 80, HOST, "");
        request
                .toObservable()
                .flatMap(success -> success.toObservable().reduce(Buffer.buffer(), (Buffer b1, Buffer b2) -> b1.appendBuffer(b2)))
                .flatMap(b -> extractSeries(b))
                .flatMap(s -> getSesons(s))
                .flatMap(s -> getChapters(s))
                .forEach(r -> System.out.println(r));

        request.end();
        Vertx.vertx().close();
    }

    private Observable<SerieInfo> getChapters(SerieInfo s) {
        return Observable.just(s);

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
    private Observable<SerieInfo> getSesons(SerieInfo s){
        Observable<SerieInfo> serieInfoObservable = Observable.<SerieInfo>create(subscriber -> {
            httpClient.getNow(80, HOST, "/" + SEASONS_URI + s.getId(), response ->
                            response.bodyHandler(buffer -> subscriber.onNext(SerieInfo.SerieBuilder.create().withSerieInfo(s).withSeasons(extractSeason(buffer)).build()))
            );
//
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
}
