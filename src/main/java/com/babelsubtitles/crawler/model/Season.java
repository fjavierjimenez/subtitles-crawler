package com.babelsubtitles.crawler.model;

import com.babelsubtitles.crawler.model.Chapter;

import java.util.List;

/**
 * Created by Javi on 10/07/2015.
 */
public class Season {
    private Integer id;
    private List<Chapter> chapters;

    public Season(Integer id) {
        this.id = id;
    }

    public Season(Integer id, List<Chapter> chapters) {
        this.id = id;
        this.chapters = chapters;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Season{" +
                "id=" + id +
                ", chapters=" + chapters +
                '}';
    }
}
