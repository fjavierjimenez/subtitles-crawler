package com.babelsubtitles.crawler;

import io.vertx.rxjava.core.Vertx;

/**
 * Created by Javi on 02/07/2015.
 */
public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle("com.babelsubtitles.crawler.SubtitlesCrawler");


    }
}
