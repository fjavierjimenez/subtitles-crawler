package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.Chapter;
import com.babelsubtitles.crawler.Serie;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.List;

/**
 * Created by Javi on 12/07/2015.
 */
public class ChapterExtractorSubsWiki implements ChapterExtractor {
    private static final String HOST="www.subswiki.com";
    private static final String CHAPTER_PREFIX = "ajax_loadShow.php?show=";
    private static final String CHAPTER_SUFFIX = "&season=";

    private HttpClient httpClient;

    private static final Logger logger = LoggerFactory.getLogger(ChapterExtractorSubsWiki.class);

    public ChapterExtractorSubsWiki(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Observable<Serie> getChapters(Serie serie) {
        serie.getSeasons().stream()
                .forEach((s) ->  httpClient.getNow(80, HOST, "/" + CHAPTER_PREFIX + serie.getId() + CHAPTER_SUFFIX + s.getId(),
                        response -> response.bodyHandler(buffer -> extractChapter(buffer))));
        return Observable.just(serie);
    }

    private List<Chapter> extractChapter(Buffer buffer){

        String html = buffer.getString(0, buffer.length());
        logger.debug("init extract");
        logger.debug(html);
        logger.debug("end extract");


        return null;

    }
}
