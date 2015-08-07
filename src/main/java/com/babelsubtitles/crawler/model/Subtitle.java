package com.babelsubtitles.crawler.model;

/**
 * Created by Javi on 15/07/2015.
 */
public class Subtitle {
    private String language;
    private String uri;
    private String version;

    public Subtitle(String language, String uri, String version) {
        this.language = language;
        this.uri = uri;
        this.version = version;
    }

    @Override
    public String toString() {
        return "Subtitle{" +
                "language='" + language + '\'' +
                ", uri='" + uri + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
