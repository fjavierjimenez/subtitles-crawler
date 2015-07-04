package com.babelsubtitles.crawler;

import java.util.List;

/**
 * Created by Javi on 02/07/2015.
 */
public class SerieInfo {
    private String id;
    private String title;
    private List<String> seasons;

    public SerieInfo(String id, String title) {
        this.id = id;
        this.title = title;
    }
    private SerieInfo(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<String> seasons) {
        this.seasons = seasons;
    }

    public static class SerieBuilder{
        private SerieInfo serie = new SerieInfo();
        private SerieBuilder (){

        }
        public static SerieBuilder create(){
            return new SerieBuilder();
        }
        public SerieBuilder withSerieInfo(SerieInfo serie){
            this.serie.id = serie.getId();
            this.serie.title = serie.getTitle();
            this.serie.seasons = serie.getSeasons();
            return this;
        }
        public SerieBuilder withSeasons(List<String> seasons){
            this.serie.seasons = seasons;
            return this;
        }
        public SerieInfo build(){
            return serie;
        }
    }

    @Override
    public String toString() {
        return "SerieInfo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", seasons=" + seasons +
                '}';
    }
}
