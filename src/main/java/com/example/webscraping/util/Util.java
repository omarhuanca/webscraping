package com.example.webscraping.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
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

        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
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
}
