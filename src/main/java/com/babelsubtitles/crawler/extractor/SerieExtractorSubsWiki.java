package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.model.Season;
import com.babelsubtitles.crawler.model.Serie;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Javi on 02/08/2015.
 */
public class SerieExtractorSubsWiki implements SerieExtractor{
    private static final Logger logger = LoggerFactory.getLogger(SerieExtractorSubsWiki.class);
    private final HttpClient httpClient;
    private final SeasonExtractor seasonExtractor;

    private static final String HOST="www.subswiki.com";
    private Pattern p = Pattern.compile("option value=\"(\\d+?)\" >(.*?)</option>", Pattern.DOTALL + Pattern.MULTILINE);

    public SerieExtractorSubsWiki(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.seasonExtractor = new SeasonExtractorSubsWiki(httpClient);
    }

    @Override
    public Observable<Serie> getSeries(Buffer b) {
        Observable<Serie> serieObservable = extractSeriesIds(b);
        return serieObservable
                .flatMap(serie-> {
                    Observable<Serie> observable = Observable.<Serie>create(subscriber -> {
                        Observable<Season> seasonObservable = extractSeasons(serie);
                        seasonObservable.toList().subscribe(
                                seasons -> subscriber.onNext(new Serie(serie.getId(), serie.getTitle(), seasons)),
                                error-> subscriber.onError(error));
                    });
                    return observable;
                });
    }

    private Observable<Season> extractSeasons(Serie serie){
        Observable<Season> seasonObservable = Observable.<Season>create(subscriber -> {
            Observable<Season> seasons = seasonExtractor.getSeasons(serie);
            seasons.subscribe(  season -> subscriber.onNext(season),
                    throwable -> subscriber.onError(throwable),
                    () -> subscriber.onCompleted());
        });
        return seasonObservable;
    }

    private Observable<Serie> extractSeriesIds(Buffer buffer){
        String html = buffer.getString(0, buffer.length());
        Matcher matcher = p.matcher(html);
        List<Serie> series = new ArrayList<>();
        while(matcher.find()){
            Serie serieInfo = new Serie(matcher.group(1), matcher.group(2));
            series.add(serieInfo);
        }
        return Observable.from(series);
    }
}
