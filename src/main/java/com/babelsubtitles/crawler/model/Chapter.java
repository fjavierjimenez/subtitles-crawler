package com.babelsubtitles.crawler.model;

import java.util.List;

/**
 * Created by Javi on 12/07/2015.
 */
public class Chapter {
    private final String id;
    private final String name;
    private final String url;
    private final List<Subtitle> subtitles;

    public Chapter(String id, String name, String url, List<Subtitle> subtitles) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.subtitles = subtitles;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", subtitles='" + subtitles + '\'' +
                '}';
    }
}
