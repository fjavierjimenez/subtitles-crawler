package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.model.Season;
import com.babelsubtitles.crawler.model.Serie;
import rx.Observable;

/**
 * Created by Javi on 12/07/2015.
 */
public interface SeasonExtractor {
    Observable<Season> getSeasons(Serie s);
}
