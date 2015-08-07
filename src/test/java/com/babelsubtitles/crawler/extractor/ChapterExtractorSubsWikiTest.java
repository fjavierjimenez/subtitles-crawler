package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.model.Season;
import com.babelsubtitles.crawler.model.Serie;
import io.vertx.ext.unit.junit.RunTestOnContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

/**
 * Created by Javi on 13/07/2015.
 */
@RunWith(io.vertx.ext.unit.junit.VertxUnitRunner.class)
public class ChapterExtractorSubsWikiTest {

    private Serie serie;

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setUp(){
        Season season = new Season(1);
        serie = Serie.SerieBuilder.create().withId("403").withSeasons(Arrays.asList(season)).build();
    }
    @Test
    public void testGetChapters() throws Exception {
//        Vertx vertx = rule.vertx();
//        HttpClient httpClient = vertx.createHttpClient(new HttpClientOptions());
//        io.vertx.rxjava.core.http.HttpClient httpClientRxified = new io.vertx.rxjava.core.http.HttpClient(httpClient);
//        SeasonExtractorSubsWiki seasonExtractorSubsWiki = new SeasonExtractorSubsWiki(httpClientRxified);
//        Observable<Serie> seasons = seasonExtractorSubsWiki.getSeasons(serie);
//        seasons.subscribe(serie->
//                System.out.println(serie.toString()));

    }
}