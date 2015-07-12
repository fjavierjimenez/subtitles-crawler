package com.babelsubtitles.crawler;

import java.util.List;

/**
 * Created by Javi on 02/07/2015.
 */
public class Serie {
    private String id;
    private String title;
    private List<Season> seasons;

    public Serie(String id, String title) {
        this.id = id;
        this.title = title;
    }
    private Serie(){

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

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public static class SerieBuilder{
        private Serie serie = new Serie();
        private SerieBuilder (){

        }
        public static SerieBuilder create(){
            return new SerieBuilder();
        }
        public SerieBuilder withSerieInfo(Serie serie){
            this.serie.id = serie.getId();
            this.serie.title = serie.getTitle();
            this.serie.seasons = serie.getSeasons();
            return this;
        }
        public SerieBuilder withSeasons(List<Season> seasons){
            this.serie.seasons = seasons;
            return this;
        }
        public Serie build(){
            return serie;
        }
    }

    @Override
    public String toString() {
        return "Serie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", seasons=" + seasons +
                '}';
    }
}
