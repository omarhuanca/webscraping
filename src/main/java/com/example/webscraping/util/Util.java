package com.example.webscraping.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

/**
 * Util
 *
 * @author Omar Huanca
 * @since 1.0
 */
@Component
@Slf4j
public class Util {

    public void login(String url, String userAgent, String username, String password) throws IOException {
        try {
            Connection.Response loginPageResponse = Jsoup.connect(url)
                    .referrer(url)
                    .userAgent(userAgent)
                    .timeout(10 * 1000)
                    .followRedirects(true)
                    .execute();

            System.out.println("Fetched login page");

            Map<String, String> mapLoginPageCookie = loginPageResponse.cookies();

            Map<String, String> mapParams = new HashMap<String, String>();
            mapParams.put("FormName", "existing");
            mapParams.put("seclogin", "on");
            mapParams.put("login", username);
            mapParams.put("passwd", password);
            mapParams.put("remember", "1");
            mapParams.put("proceed", "Go");

            Connection.Response responsePostLogin = Jsoup.connect(url)
                    .referrer(url)
                    .userAgent(userAgent)
                    .timeout(10 * 1000)
                    .data(mapParams)
                    .cookies(mapLoginPageCookie)
                    .followRedirects(true)
                    .execute();

            System.out.println("HTTP Status code " + responsePostLogin);

            Document document = responsePostLogin.parse();
            System.out.println("Document " + document);

            Map<String, String> mapLoggedInCookies = responsePostLogin.cookies();
            System.out.println("Cookies " + mapLoggedInCookies);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Document getHtmlDocument(String baseUrl, String urlLogin) {
        Document page = null;
        Connection.Response response;

        try {
            Map<String, String> header = getHeader();
            Connection connection = Jsoup.connect(baseUrl)
                                        .headers(header)
                                        .method(Connection.Method.GET);

            response = connection.execute();

            System.out.println("response " + response);

            Map<String, String> cookies = response.cookies();

            header.put("Cookie", "JSESSIONID=" + cookies.get("JSESSIONID"));
            header.put("Origin", baseUrl);
            header.put("Referer", urlLogin);

            response = Jsoup.connect(urlLogin)
                            .data("j_username", "6551990")
                            .data("j_password", "VNZANU")
                            .data("input[type=submit]", "ingresar")
                            .cookies(cookies)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36")
                            .method(Connection.Method.POST)
                            .execute();

            page = response.parse();

        } catch (IOException io) {
            io.printStackTrace();
        }

        return page;
    }

    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();

        header.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept-Language", "en-US,en;q=0.9");
        header.put("Cache-Control", "max-age=0");
        header.put("Connection", "keep-alive");
        header.put("Content-Length", "173");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Host", "www.dgr.gub.uy");
        header.put("Origin", "https://www.dgr.gub.uy");
        header.put("Referer", "https://www.dgr.gub.uy/sr/loginStart.jsf");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", "Mozilla/5.0");

        return header;
    }

    public Map<String, String> getDynamicHeader() {
        Map<String, String> header = new HashMap<String, String>();

        header.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept-Language", "en-US,en;q=0.9");
        header.put("Cache-Control", "max-age=0");
        header.put("Connection", "keep-alive");
        header.put("Content-Length", "173");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Host", "sistema.emporiodelosfiltros.com");
        header.put("Origin", "http://sistema.emporiodelosfiltros.com");
        header.put("Referer", "http://sistema.emporiodelosfiltros.com/");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", "Mozilla/5.0");

        return header;
    }

    public Document getHtmlDynamicDocument() {
        Document page = null;
        Connection.Response response;

        try {
            Map<String, String> header = this.getDynamicHeader();
            Connection connection = Jsoup.connect("http://sistema.emporiodelosfiltros.com/")
                    .headers(header)
                    .method(Connection.Method.GET);

            response = connection.execute();
            Document document = connection.get();

            Element authenticityToken = document.select("input[name=authenticity_token]").first();
            if (null == authenticityToken) {
                System.out.println("Error of token");
                return null;
            }

            String authenticityTokenVal = authenticityToken.attr("value");

            Map<String, String> cookies = response.cookies();

            header.put("Cookie", "_emp_filtros_session=" + cookies.get("_emp_filtros_session"));
            header.put("Origin", "http://sistema.emporiodelosfiltros.com");
            header.put("Referer", "http://sistema.emporiodelosfiltros.com/usuarios/sign_in?unauthenticated=true");

            response = Jsoup.connect("http://sistema.emporiodelosfiltros.com/usuarios/sign_in")
                    .data("authenticity_token", authenticityTokenVal)
                    .data("usuario[email]", "Alsur-repuestos@adinet.com.uy")
                    .data("usuario[password]", "alsurrepuestos")
                    .data("commit", "Ingresar")
                    .cookies(cookies)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36")
                    .method(Connection.Method.POST)
                    .execute();

            String body = response.body();
            page = Jsoup.parseBodyFragment(body.substring(body.indexOf("<table>"), body.indexOf("</table>")));

        } catch (IOException io) {
            io.printStackTrace();
        }

        return page;
    }

    public Map<String, String> getCookie(String defaultUrl, String email, String pass) throws Exception {
        Map<String, String> cookies = new HashMap<String, String>();
        String url = "";

        Connection.Response connection = null;
        connection = Jsoup.connect(defaultUrl).execute();
        Document doc = connection.parse();
        Element form = doc.getElementsByTag("form").get(0);

        url = form.attr("action");

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", email);
        data.put("pass", pass);
        connection = Jsoup.connect(url).data(data).execute();

        try {
            cookies = connection.cookies();
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad login or password");
        }

        return cookies;
    }

    public Document getDocument(String defaultUrl, String userAgent) {
        Document document = null;
        try {
            Map<String, String> header = this.getDynamicHeader();
            Connection connection = Jsoup.connect(defaultUrl)
                    .headers(header)
                    .validateTLSCertificates(false)
                    .userAgent(userAgent)
                    .method(Connection.Method.GET);

            Connection.Response response = connection.execute();
            document = Jsoup.parse(response.body());

        } catch(Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public Map<String, String> getDocumentThree(String defaultUrl) {
        Map<String, String> cookies = new HashMap<String, String>();

        try {
            Connection.Response connection = null;
            connection = Jsoup.connect(defaultUrl)
                        .validateTLSCertificates(false)
                        .execute();
            cookies = connection.cookies();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return cookies;
    }

    public Connection.Response getDocumentFour(String defaultUrl) {
        Connection.Response response = null;

        try {
            Connection connection = Jsoup.connect(defaultUrl);
            response = connection.execute();

            Document document = connection.get();

            Map<String, String> cookies = response.cookies();

            System.out.println("cookie: " + cookies.get("JSESSIONID"));

            String userAgentChromium = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36";

            // document.select("input[name=authenticity_token]").first()
            System.out.println("name=j_username " + document.select("input[name=j_username]"));
            System.out.println("type=submit " + document.select("input[type=submit]"));


            response = Jsoup.connect("https://www.dgr.gub.uy/sr/j_security_check")
                            .data("j_username", "6551990")
                            .data("j_password", "VNZANU")
                            .cookies(this.getRequestHeaderFour(cookies.get("JSESSIONID"), userAgentChromium))
                            .userAgent(userAgentChromium)
                            .method(Connection.Method.POST)
                            .execute();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Map<String, String> getDocumentFive(String defaultUrl, String host) {
        Map<String, String> cookies = new HashMap<>();
        try {
            Connection.Response response = null;

            Connection connection = Jsoup.connect(defaultUrl)
                                        .headers(this.getRequestHeaderBefore())
                                        .method(Connection.Method.GET);
            response = connection.execute();

            cookies = response.cookies();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return cookies;
    }

    public Document getDocumentSix() {
        Connection.Response res = null;
        Document doc = null;
        Map<String, String> cookies = new HashMap<>();
        Map<String, String> headers = new HashMap<>();

        try {
            headers = this.getRequestHeaderBefore();
            Connection connection = Jsoup.connect("https://www.dgr.gub.uy/sr/loginStart.jsf")
                    .headers(headers)
                    .method(Connection.Method.GET);

            res = connection.execute();
            cookies = res.cookies();

        } catch(Exception e) {
            e.printStackTrace();
        }
        Connection.Response responseTwo = null;

        try {
            Map<String, String> headersTwo = new HashMap<>();

            headersTwo = this.getRequestHeaderAfter(cookies.get("JSESSIONID"));

            Connection connectionTwo = Jsoup.connect("https://www.dgr.gub.uy/sr/j_security_check")
                    .headers(headersTwo)
                    .data("j_username", "6551990")
                    .data("j_password", "VNZANU")
                    .data("input[type=submit]", "ingresar")
                    .timeout(2*1000) 
                    .ignoreHttpErrors(true)
                    .cookies(cookies)
                    .method(Connection.Method.POST);

            responseTwo = connectionTwo.execute();

            } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Map<String, String> headersThree = new HashMap<>();
            Connection.Response  responseThree = null;
            Connection connectionThree = null;

            headersThree = this.getRequestHeaderAfterLogin(cookies.get("JSESSIONID"));
            connectionThree = Jsoup.connect("http://www.dgr.gub.uy/sr/principal.jsf")
                            .headers(headersThree)
                            .cookies(cookies)
                            .method(Connection.Method.GET)
                            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36")
                            .followRedirects(true);

            responseThree = connectionThree.execute();

            doc = responseThree.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }

    private Map<String, String> getRequestHeaderBefore() {
        Map<String, String> header = new HashMap<>();

        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept-Language", "en-US,en;q=0.9");
        header.put("Cache-Control", "max-age=0");
        header.put("Connection", "keep-alive");
        header.put("Host", "www.dgr.gub.uy");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", "Mozilla/5.0");

        return header;
    }

    private Map<String, String> getRequestHeaderAfter(String cookie) {
        Map<String, String> header = new HashMap<>();

        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("Accept-Language", "en-US,en;q=0.9");
        header.put("Cache-Control", "max-age=0");
        header.put("Connection", "keep-alive");
        header.put("Content-Length", "36");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Cookie", "JSESSIONID=" + cookie);
        header.put("Host", "www.dgr.gub.uy");
        header.put("Origin", "https://www.dgr.gub.uy");
        header.put("Referer", "https://www.dgr.gub.uy/sr/loginStart.jsf");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36");

        return header;
    }

    private Map<String, String> getRequestHeaderAfterLogin(String cookie) {
        Map<String, String> header = new HashMap<>();

        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept-Language", "en-US,en;q=0.9");
        header.put("Cache-Control", "max-age=0");
        header.put("Connection", "keep-alive");
        header.put("Cookie", "JSESSIONID=" + cookie);
        header.put("Host", "www.dgr.gub.uy");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36");

        return header;
    }
    


    private Map<String, String> getRequestHeaderFour(String cookie, String userAgent) {
        Map<java.lang.String, java.lang.String> response = new HashMap<>();

        response.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        response.put("Accept-Encoding", "gzip, deflate, br");
        response.put("Accept-Language", "en-US,en;q=0.9");
        response.put("Cache-Control", "max-age=0");
        response.put("Connection", "keep-alive");
        response.put("Cookie", "JSESSIONID=" + cookie);
        response.put("Host", "www.dgr.gub.uy");
        response.put("Referer", "https://www.dgr.gub.uy/sr/loginStart.jsf");
        response.put("Upgrade-Insecure-Requests", userAgent);

        return response;
    }

    public Map<String, String> getHeaderFive(String cookie, String userAgent) {
        Map<String, String> header = new HashMap<String, String>();

        header.put("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept-Language", "en-US,en;q=0.9");
        header.put("Cache-Control", "max-age=0");
        header.put("Connection", "keep-alive");
        header.put("Cookie", "_emp_filtros_session=" + cookie);
        header.put("Content-Length", "173");
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Host", "sistema.emporiodelosfiltros.com");
        header.put("Origin", "http://sistema.emporiodelosfiltros.com");
        header.put("Referer", "http://sistema.emporiodelosfiltros.com/");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", userAgent);

        return header;
    }

}
