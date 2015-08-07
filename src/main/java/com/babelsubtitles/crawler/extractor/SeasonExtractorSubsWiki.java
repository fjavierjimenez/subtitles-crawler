package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.model.Chapter;
import com.babelsubtitles.crawler.model.Season;
import com.babelsubtitles.crawler.model.Serie;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Javi on 12/07/2015.
 */
public class SeasonExtractorSubsWiki implements SeasonExtractor {
    private static final Logger logger = LoggerFactory.getLogger(SeasonExtractorSubsWiki.class);
    private Pattern p2 = Pattern.compile("<option>(\\d+)</option>", Pattern.DOTALL + Pattern.MULTILINE);

    private static final String HOST="www.subswiki.com";

    private static final String SEASONS_URI="ajax_getSeasons.php?showID=";

    private HttpClient httpClient;
    private ChapterExtractor chapterExtractor;

    public SeasonExtractorSubsWiki(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.chapterExtractor = new ChapterExtractorSubsWiki(httpClient);
    }

    @Override
    public Observable<Season> getSeasons(Serie s){
        Observable<Season> serieInfoObservable = Observable.<Season>create(subscriber -> {
            httpClient.getNow(80, HOST, "/" + SEASONS_URI + s.getId(), response ->
                            response.bodyHandler(buffer -> {
                                List<Integer> seasonsIds = extractSeason(buffer);
                                for (Integer seasonId : seasonsIds) {
                                    Observable<List<Chapter>> chapters = chapterExtractor.getChapters(s, seasonId).toList();
                                    chapters.subscribe(chaptersList -> subscriber.onNext(new Season(seasonId, chaptersList)),
                                            e -> subscriber.onError(e),
                                            () -> subscriber.onCompleted());

                                }
                            })
            );
        });
        return serieInfoObservable;
    }
    private List<Integer> extractSeason(Buffer buffer){
        String html = buffer.getString(0, buffer.length());
        Matcher matcher = p2.matcher(html);
        List<Integer> seasonsIds = new ArrayList<>();
        while(matcher.find()){
            seasonsIds.add(Integer.valueOf(matcher.group(1)));
        }
        return seasonsIds;
    }
}