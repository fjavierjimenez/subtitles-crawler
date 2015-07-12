package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.Season;
import com.babelsubtitles.crawler.Serie;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Javi on 12/07/2015.
 */
public class SeasonExtractorSubsWiki implements SeasonExtractor {
    private Pattern p2 = Pattern.compile("<option>(\\d+)</option>", Pattern.DOTALL + Pattern.MULTILINE);

    private static final String HOST="www.subswiki.com";

    private static final String SEASONS_URI="ajax_getSeasons.php?showID=";

    private HttpClient httpClient;

    public SeasonExtractorSubsWiki(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Observable<Serie> getSeasons(Serie s){
        Observable<Serie> serieInfoObservable = Observable.<Serie>create(subscriber -> {
            httpClient.getNow(80, HOST, "/" + SEASONS_URI + s.getId(), response ->
                            response.bodyHandler(buffer -> subscriber.onNext(Serie.SerieBuilder.create().withSerieInfo(s).withSeasons(extractSeason(buffer)).build()))
            );
        });
        return serieInfoObservable;
    }
    private List<Season> extractSeason(Buffer buffer){
        String html = buffer.getString(0, buffer.length());
        Matcher matcher = p2.matcher(html);
        List<Season> seasons = new ArrayList<>();
        while(matcher.find()){
            seasons.add(new Season(Integer.valueOf(matcher.group(1))));
        }
        return seasons;
    }
}
