package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.Serie;
import rx.Observable;

/**
 * Created by Javi on 12/07/2015.
 */
public interface SeasonExtractor {
    Observable<Serie> getSeasons(Serie s);
}
