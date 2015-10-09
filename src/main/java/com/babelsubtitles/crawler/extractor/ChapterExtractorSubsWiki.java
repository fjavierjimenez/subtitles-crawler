package com.babelsubtitles.crawler.extractor;

import com.babelsubtitles.crawler.model.Chapter;
import com.babelsubtitles.crawler.model.Serie;
import com.babelsubtitles.crawler.model.Subtitle;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Javi on 12/07/2015.
 */
public class ChapterExtractorSubsWiki implements ChapterExtractor {
    private static final String HOST="www.subswiki.com";
    private static final String CHAPTER_PREFIX = "ajax_loadShow.php?show=";
    private static final String CHAPTER_SUFFIX = "&season=";


    private static final String CHAPTER_RE= "(<table width=\"80%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">.*?<tr>(.*?<a href=\'(http:\\/\\/www\\.subswiki\\.com\\/serie\\/.*?\\/.*?\\/(.*?)\\/.*?)\'.*?>(.*?)<\\/a>.*?)<\\/tr>(.*?)<\\/table>)";
    private static final String VERSION_REGEX = ".*?Versi&oacute;n(.*?)</td>.*?</tr>(.*?)(<td colspan=\"3\" class=\"newsClaro\">|<td colspan=\"7\">&nbsp;<\\/td>)";
    private static final String SUBTITLE_REGEX ="<td width=\"41%\" class=\"language\">(.*?)</td>.*?<a href=\"(.*?)\">";

    private Pattern patternChapter = Pattern.compile(CHAPTER_RE, Pattern.DOTALL + Pattern.MULTILINE);
    private Pattern patternVersion = Pattern.compile(VERSION_REGEX, Pattern.DOTALL + Pattern.MULTILINE);
    private Pattern patternSubtitle = Pattern.compile(SUBTITLE_REGEX, Pattern.DOTALL + Pattern.MULTILINE);

    private HttpClient httpClient;

    private static final Logger logger = LoggerFactory.getLogger(ChapterExtractorSubsWiki.class);

    public ChapterExtractorSubsWiki(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Observable<Chapter> getChapters(Serie serie, Integer seasonId) {
        Observable<Chapter> objectObservable = Observable.create(subscriber -> {
            httpClient.getNow(80, HOST, "/" + CHAPTER_PREFIX + serie.getId() + CHAPTER_SUFFIX + seasonId,
                    response -> response.bodyHandler(buffer -> {
                        List<Chapter> chapters = extractChapter(buffer);
                        for (Chapter chapter : chapters) {
                            subscriber.onNext(chapter);
                        }
                        subscriber.onCompleted();
                    })
            );
        });

        return objectObservable;
    }

    private List<Chapter> extractChapter(Buffer buffer){
        String html = buffer.getString(0, buffer.length());
        Matcher matcher = patternChapter.matcher(html);
        List<Chapter> chapters = new ArrayList<>();
        while(matcher.find()){
            String url = matcher.group(3);
            String id = matcher.group(4);
            String name = matcher.group(5);
            List<Chapter> chapters2 = extractVersions(url, id, name, matcher.group(6));
            chapters.addAll(chapters2);
        }
        return chapters;
    }

    private List<Chapter> extractVersions(String url, String id, String name, String subtitles) {
        Matcher matcher = patternVersion.matcher(subtitles);
        List<Chapter> chapters = new ArrayList<>();
        while(matcher.find()){
            String version = matcher.group(1);
            List<Subtitle> subtitlesList = extractSubtitles(url, id, name, version, matcher.group(2));
            Chapter chapter = new Chapter(id, name, url, subtitlesList);
            chapters.add(chapter);
        }
        return chapters;
    }

    private List<Subtitle> extractSubtitles(String url, String id, String name, String version, String html) {
        Matcher matcher = patternSubtitle.matcher(html);
        List<Subtitle> subtitles = new ArrayList<>();
        while(matcher.find()){
            String language = matcher.group(1);
            String uri = matcher.group(2);
            Subtitle subtitle = new Subtitle(language, uri, version);
            subtitles.add(subtitle);
        }
        return subtitles;
    }
}