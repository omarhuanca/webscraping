package com.example.webscraping.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.example.webscraping.util.AElog;
import com.example.webscraping.util.Util;

/**
 * TestService
 *
 * @author Omar Huanca
 * @since 1.0
 */
@Service
@Slf4j
public class TestService {

    private static final Logger logger = LoggerFactory.getLogger(TestService.class);

    @Autowired
    private Util util;

    public Map<String, Long> getLinkOnPage(String url) throws IOException {

        this.requestLog("Jsoup is connecting to : {}" + url);
        Document webPage = Jsoup.connect(url).get();
        List<String> list = this.printLink(webPage);

        return this.convertListToMap(list);
    }

    public Map<String, Long> getImageOnPage(String url) throws IOException {
        this.requestLog("Jsoup is connecting to : {}" + url);
        Document webPage = Jsoup.connect(url).get();
        List<String> list = this.printImage(webPage);

        return this.convertListToMap(list);
    }

    public Map<String, Long> getImportOnPage(String url) throws IOException {
        this.requestLog("Jsoup is connecting to : {}" + url);
        Document webPage = Jsoup.connect(url).get();
        List<String> list = this.printImport(webPage);

        return this.convertListToMap(list);
    }

    private Map<String, Long> convertListToMap(List<String> linksOnPage) {

        this.requestLog("List of HostNames recieved");
        this.requestLog("Removing empty HostNames from list");
        this.requestLog("Grouping identical HostNames and counting");
        this.requestLog("Creating a Map with unique HostNames as key and their frequencies as values");

        Map<String, Long> mapLinkOnPage = linksOnPage.parallelStream().filter(link -> (link != null && !link.isEmpty()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return mapLinkOnPage;
    }

    private List<String> printLink(Document webPage) {

        List<String> listLink = new ArrayList<>();

        Elements listLinkOnPage = webPage.select("a[href]");

        this.requestLog("Link size " + listLinkOnPage.size());

        listLinkOnPage.parallelStream().forEach(linkOnPage -> {
            try {
                URI uri = new URI(linkOnPage.attr("abs:href"));
                String link = uri.getHost();

                this.requestLog("Tag <a href= '{}'>" + uri);
                this.requestLog("HostName = {}" + link);

                listLink.add(link);

            } catch (URISyntaxException e) {
                System.err.println("URISyntaxException : " + "url = " + linkOnPage.attr("abs:href") + "\nMessage = "
                        + e.getMessage());
            }
        });

        return listLink;
    }

    private List<String> printImage(Document webPage) {
        List<String> listMedia = new ArrayList<>();

        Elements listMediaOnPage = webPage.select("[src]");

        this.requestLog("Media size " + listMediaOnPage.size());

        listMediaOnPage.parallelStream().forEach(mediaOnPage -> {
            try {
                if (mediaOnPage.tagName().equals("img")) {

                    this.requestLog("tagName " + mediaOnPage.tagName());
                    this.requestLog("attr " + mediaOnPage.attr("abs:src"));
                    this.requestLog("width " + mediaOnPage.attr("width"));
                    this.requestLog("height " + mediaOnPage.attr("height"));
                    this.requestLog("alt " + mediaOnPage.attr("alt"));

                    listMedia.add(mediaOnPage.attr("abs:src"));
                }
            } catch (Exception e) {
                System.err.println("URISyntaxException : " + "url = " + mediaOnPage.attr("abs:src") + "\nMessage = "
                        + e.getMessage());
            }
        });

        return listMedia;
    }

    private List<String> printImport(Document webPage) {
        List<String> listImport = new ArrayList<>();

        Elements listImportOnPage = webPage.select("link[href]");

        this.requestLog("Media size " + listImportOnPage.size());

        listImportOnPage.parallelStream().forEach(importOnPage -> {
            try {
                this.requestLog("abs:href " + importOnPage.attr("abs:href"));
                this.requestLog("text " + importOnPage.text());

                listImport.add(importOnPage.attr("abs:href"));

                this.requestLog(importOnPage.attr("abs:href"));
            } catch (Exception e) {
                System.err.println("URISyntaxException : " + "url = " + importOnPage.attr("abs:src") + "\nMessage = "
                        + e.getMessage());
            }
        });

        return listImport;
    }

    public void tryLogin(String url, String userAgent, String username, String password) {
        /*
         * String url = "https://www.dgr.gub.uy/sr/loginStart.jsf"; 
         * String userAgent =
         * "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36"
         * String username = "6551990"; String password = "VNZANU";
         */
        try {
            util.login(url, userAgent, username, password);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public Document getHtmlDocument(String baseUrl, String urlLogin) {
        Document response = null;
        try {
            response = util.getHtmlDocument(baseUrl, urlLogin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private synchronized void requestLog(String string) {
        AElog.info1(logger, string);
    }
}