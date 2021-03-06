package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.model.Chapter;
import com.babelsubtitles.crawler.model.Serie;
import rx.Observable;

/**
 * Created by Javi on 12/07/2015.
 */
public interface ChapterExtractor {
    Observable<Chapter> getChapters(Serie serie, Integer seasonId);
}
