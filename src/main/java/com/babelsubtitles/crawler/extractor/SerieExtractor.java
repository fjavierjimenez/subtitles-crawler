package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.model.Chapter;
import com.babelsubtitles.crawler.model.Serie;
import io.vertx.rxjava.core.buffer.Buffer;
import rx.Observable;

/**
 * Created by Javi on 02/08/2015.
 */
public interface SerieExtractor {
    Observable<Serie> getSeries(Buffer b);
}
