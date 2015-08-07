package com.babelsubtitles.crawler;

import com.babelsubtitles.crawler.extractor.SerieExtractor;
import com.babelsubtitles.crawler.extractor.SerieExtractorSubsWiki;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Javi on 02/07/2015.
 */
public class SubtitlesCrawler extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(SubtitlesCrawler.class);
    private static final String HOST = "www.subswiki.com";
    private HttpClient httpClient;
    private SerieExtractor serieExtractor;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        this.httpClient = vertx.createHttpClient(new HttpClientOptions());
        serieExtractor = new SerieExtractorSubsWiki(httpClient);
        HttpClientRequest request = this.httpClient.request(HttpMethod.GET, 80, HOST, "");
        Observable<Buffer> bufferObservable = request
                .toObservable()
                .flatMap(success -> success.toObservable().reduce(Buffer.buffer(), (Buffer b1, Buffer b2) -> b1.appendBuffer(b2))).cache();
        Subscription subscribe = bufferObservable.subscribe();
        bufferObservable.flatMap(b -> serieExtractor.getSeries(b))
                .forEach(serie -> logger.debug(serie.toString()),
                        error -> logger.error(error.toString()),
                        () -> logger.info("ALL SERIES COMPLETED"));
        request.end();
    }
}