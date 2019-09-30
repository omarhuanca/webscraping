package com.example.webscraping.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

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

    @Autowired
    private Util util;

    public Map<String, Long> getLinkOnPage(String url) throws IOException {
        Document webPage = Jsoup.connect(url).get();
        List<String> list = this.printLink(webPage);

        return this.convertListToMap(list);
    }

    public Map<String, Long> getImageOnPage(String url) throws IOException {
        Document webPage = Jsoup.connect(url).get();
        List<String> list = this.printImage(webPage);

        return this.convertListToMap(list);
    }

    public Map<String, Long> getImportOnPage(String url) throws IOException {
        Document webPage = Jsoup.connect(url).get();
        List<String> list = this.printImport(webPage);

        return this.convertListToMap(list);
    }

    private Map<String, Long> convertListToMap(List<String> linksOnPage) {

        System.out.println("List of HostNames recieved");
        System.out.println("Removing empty HostNames from list");
        System.out.println("Grouping identical HostNames and counting");
        System.out.println("Creating a Map with unique HostNames as key and their frequencies as values");

        Map<String, Long> mapLinkOnPage = linksOnPage.parallelStream().filter(link -> (link != null && !link.isEmpty()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return mapLinkOnPage;
    }

    private List<String> printLink(Document webPage) {

        List<String> listLink = new ArrayList<>();

        Elements listLinkOnPage = webPage.select("a[href]");

        System.out.println("Link size " + listLinkOnPage.size());

        listLinkOnPage.parallelStream().forEach(linkOnPage -> {
            try {
                URI uri = new URI(linkOnPage.attr("abs:href"));
                String link = uri.getHost();

                System.out.println("Tag <a href= '{}'>" + uri);
                System.out.println("HostName = {}" + link);

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

        System.out.println("Media size " + listMediaOnPage.size());

        listMediaOnPage.parallelStream().forEach(mediaOnPage -> {
            try {
                if (mediaOnPage.tagName().equals("img")) {

                    System.out.println("tagName " + mediaOnPage.tagName());
                    System.out.println("attr " + mediaOnPage.attr("abs:src"));
                    System.out.println("width " + mediaOnPage.attr("width"));
                    System.out.println("height " + mediaOnPage.attr("height"));
                    System.out.println("alt " + mediaOnPage.attr("alt"));

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

        System.out.println("Media size " + listImportOnPage.size());

        listImportOnPage.parallelStream().forEach(importOnPage -> {
            try {
                System.out.println("abs:href " + importOnPage.attr("abs:href"));
                System.out.println("text " + importOnPage.text());

                listImport.add(importOnPage.attr("abs:href"));

                System.out.println(importOnPage.attr("abs:href"));
            } catch (Exception e) {
                System.err.println("URISyntaxException : " + "url = " + importOnPage.attr("abs:src") + "\nMessage = "
                        + e.getMessage());
            }
        });

        return listImport;
    }

    public void tryLogin(String url, String userAgent, String username, String password) {
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

    public Map<String, String> getCookie(String defaultUrl, String email, String pass) throws Exception {
        Map<String, String> response = new HashMap<String, String>();
        try {
            response = util.getCookie(defaultUrl, email, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Document getHtmlDynamicDocument() {
        Document response = null;
        try {
            response = util.getHtmlDynamicDocument();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Document getDocument() {
        Document response = null;
        try {
            //String defaultUrl = "https://www.dgr.gub.uy/sr/loginStart.jsf";
            String defaultUrl = "http://sistema.emporiodelosfiltros.com/usuarios/sign_in";
         //String userAgentChromium = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36";
           //String userAgentChrome = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36";
            String userAgentFirefox = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0"; 
            response = util.getDocument(defaultUrl, userAgentFirefox);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Map<String, String> getDocumentThree(String defaultUrl) {
        Map<String, String> response = null;
        try {
            response = util.getDocumentThree(defaultUrl);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Connection.Response getDocumentFour(String defaultUrl) {
        Connection.Response response = null;
        try {
            response = util.getDocumentFour(defaultUrl);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Map<String, String> getDocumentFive(String defaultUrl, String host) {
        Map<String, String> response = null;
        try {
            response = util.getDocumentFive(defaultUrl, host);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Document getDocumentSix() {
        Document response = null;
        try {
            String defaultUrl = "https://www.dgr.gub.uy/sr/loginStart.jsf";
            String host = "www.dgr.gub.uy";
            response = util.getDocumentSix(defaultUrl, host);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}