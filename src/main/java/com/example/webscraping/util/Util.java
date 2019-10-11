package com.example.webscraping.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Util
 *
 * @author Omar Huanca
 * @since 1.0
 */
@Component
@Slf4j
public class Util {

    private static final DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public void login(String url, String userAgent, String username, String password) throws IOException {
        try {
            Connection.Response loginPageResponse = Jsoup.connect(url).referrer(url).userAgent(userAgent)
                    .timeout(10 * 1000).followRedirects(true).execute();

            System.out.println("Fetched login page");

            Map<String, String> mapLoginPageCookie = loginPageResponse.cookies();

            Map<String, String> mapParams = new HashMap<String, String>();
            mapParams.put("FormName", "existing");
            mapParams.put("seclogin", "on");
            mapParams.put("login", username);
            mapParams.put("passwd", password);
            mapParams.put("remember", "1");
            mapParams.put("proceed", "Go");

            Connection.Response responsePostLogin = Jsoup.connect(url).referrer(url).userAgent(userAgent)
                    .timeout(10 * 1000).data(mapParams).cookies(mapLoginPageCookie).followRedirects(true).execute();

            System.out.println("HTTP Status code " + responsePostLogin);

            Document document = responsePostLogin.parse();
            System.out.println("Document " + document);

            Map<String, String> mapLoggedInCookies = responsePostLogin.cookies();
            System.out.println("Cookies " + mapLoggedInCookies);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Document getHtmlDocument(String baseUrl, String urlLogin) {
        Document page = null;
        Connection.Response response;

        try {
            Map<String, String> header = getHeader();
            Connection connection = Jsoup.connect(baseUrl).headers(header).method(Connection.Method.GET);

            response = connection.execute();

            System.out.println("response " + response);

            Map<String, String> cookies = response.cookies();

            header.put("Cookie", "JSESSIONID=" + cookies.get("JSESSIONID"));
            header.put("Origin", baseUrl);
            header.put("Referer", urlLogin);

            response = Jsoup.connect(urlLogin).data("j_username", "6551990").data("j_password", "VNZANU")
                    .data("input[type=submit]", "ingresar").cookies(cookies)
                    .userAgent(
                            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36")
                    .method(Connection.Method.POST).execute();

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
            Connection connection = Jsoup.connect("http://sistema.emporiodelosfiltros.com/").headers(header)
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
                    .data("usuario[email]", "Alsur-repuestos@adinet.com.uy").data("usuario[password]", "alsurrepuestos")
                    .data("commit", "Ingresar").cookies(cookies)
                    .userAgent(
                            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36")
                    .method(Connection.Method.POST).execute();

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
            Connection connection = Jsoup.connect(defaultUrl).headers(header).validateTLSCertificates(false)
                    .userAgent(userAgent).method(Connection.Method.GET);

            Connection.Response response = connection.execute();
            document = Jsoup.parse(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public Map<String, String> getDocumentThree(String defaultUrl) {
        Map<String, String> cookies = new HashMap<String, String>();

        try {
            Connection.Response connection = null;
            connection = Jsoup.connect(defaultUrl).validateTLSCertificates(false).execute();
            cookies = connection.cookies();

        } catch (Exception e) {
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

            response = Jsoup.connect("https://www.dgr.gub.uy/sr/j_security_check").data("j_username", "6551990")
                    .data("j_password", "VNZANU")
                    .cookies(this.getRequestHeaderFour(cookies.get("JSESSIONID"), userAgentChromium))
                    .userAgent(userAgentChromium).method(Connection.Method.POST).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public Map<String, String> getDocumentFive(String defaultUrl, String host) {
        Map<String, String> cookies = new HashMap<>();
        try {
            Connection.Response response = null;

            Connection connection = Jsoup.connect(defaultUrl).headers(this.getRequestHeaderBefore())
                    .method(Connection.Method.GET);
            response = connection.execute();

            cookies = response.cookies();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cookies;
    }

    public List<String> getDocumentSix() {
        Document doc = null;
        List<String> listaResult = new ArrayList<>();
        Connection.Response res = null;
        Map<String, String> cookies = new HashMap<>();

        try {
            Map<String, String> headers = new HashMap<>();

            headers = this.getRequestHeaderBefore();
            Connection connection = Jsoup.connect("https://www.dgr.gub.uy/sr/loginStart.jsf").headers(headers)
                    .method(Connection.Method.GET);

            res = connection.execute();
            cookies = res.cookies();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Connection connectionTwo = Jsoup.connect("https://www.dgr.gub.uy/sr/j_security_check")
                    .data("j_username", "6551990").data("j_password", "VNZANU").cookies(cookies)
                    .method(Connection.Method.POST);

            res = connectionTwo.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Connection connectionThree = null;

            connectionThree = Jsoup.connect("http://www.dgr.gub.uy/sr/principal.jsf").cookies(cookies)
                    .data("_USER", "6551990").data("_PASSWORD", "VNZANU").data("_EventName", "E'LOGIN")
                    .method(Connection.Method.GET).followRedirects(true);

            res = connectionThree.execute();
            doc = res.parse();
            String[] listaSplit = doc.body().html().split("\n");
            listaResult = Arrays.asList(listaSplit);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaResult;
    }

    public List<String> getDocumentSeven(String username, String password) {
        List<String> result = new ArrayList<String>();

        if (null != username && null != password) {
            Connection.Response response = null;
            Map<String, String> cookies = new HashMap<>();
            Connection connection = null;
            Document document = null;

            try {
                Map<String, String> headers = new HashMap<>();

                headers = this.getRequestHeaderBefore();
                connection = Jsoup.connect("https://www.dgr.gub.uy/sr/loginStart.jsf").headers(headers)
                        .method(Connection.Method.GET);

                response = connection.execute();
                cookies = response.cookies();

                System.out.println("cookies " + response.cookies());

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Map<String, String> headersTwo = new HashMap<>();

                headersTwo = this.getRequestHeaderAfter(cookies.get("JSESSIONID"));

                connection = Jsoup.connect("https://www.dgr.gub.uy/sr/j_security_check")
                        .headers(headersTwo)
                        .data("j_username", username)
                        .data("j_password", password)
                        .cookies(cookies)
                        .method(Connection.Method.POST);

                response = connection.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                connection = Jsoup.connect("https://www.dgr.gub.uy/etimbreapp/servlet/hlogin")
                        .cookies(cookies)
                        .data("_USER", username)
                        .data("_PASSWORD", password)
                        .data("_EventName", "E'LOGIN'.")
                        .method(Connection.Method.POST);

                response = connection.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                connection = Jsoup.connect("https://www.dgr.gub.uy/etimbreapp/servlet/hinicio")
                        .cookies(cookies)
                        .method(Connection.Method.GET);

                response = connection.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                connection = Jsoup.connect("http://www.dgr.gub.uy/sr/principal.jsf")
                        .cookies(cookies)
                        .method(Connection.Method.GET);

                response = connection.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
            
            /*
            try {
                connection = Jsoup.connect("http://www.dgr.gub.uy/sr/principal.jsf")
                        .cookies(cookies)
                        .data("j_id15", "j_id15")
                        .data("j_id15:mpOpenedState", "")
                        .data("javax.faces.ViewState", "j_id1")
                        .data("j_id15:j_id44:hidden", "j_id15:j_id44")
                        .method(Connection.Method.GET);

                response = connection.execute();
              
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
            
            try {
                connection = Jsoup.connect("http://www.dgr.gub.uy/etimbreapp/servlet/hsolicitudform?1")
                        .cookies(cookies)
                        .data("_EventName", "E'AGREGARFIS'.")
                        .data("_EventGridId", "367")
                        .data("_EventRowId", "1")
                        .data("CTLCODIGOUS", "6551990")
                        .data("CTLCODIGOCLI", "PUBLICO")
                        .data("_FCHING", "11/10/2019")
                        .data("CTLFCHEMSOL", "11/10/2019")
                        .data("_NROSOLIC", "0")
                        .data("CTLTIPOSOL", "C")
                        .data("_NROFISICAS", "0")
                        .data("_NROJURIDICAS", "0")
                        .data("_NROAUTOMOT", "0")
                        .data("_NROINM", "0")
                        .data("_NROAERONAVES", "0")
                        .data("_NROTESTIMONIOS", "0")
                        .data("_CITEMP", "")
                        .data("CTLDVFIS", "")
                        .data("CTLAPE1FIS", "ZAA")
                        .data("CTLAPE2FIS", "") 
                        .data("CTLNOM1FIS", "JORGE")
                        .data("CTLNOM2FIS", "")
                        .data("CTLNOM3FIS", "")
                        .data("CTLINTERFIS","1")
                        .data("CTLINTERVALFIS", "I")
                        .data("CTLFALL_ANOFIS", "0")
                        .data("CTLCES_HASFIS", "0")
                        .data("CTLNG_DESFIS", "0")
                        .data("CTLNG_HASFIS", "0")
                        .data("CTLDCM_DESFIS", "0")
                        .data("CTLDCM_HASFIS", "0")
                        .data("CTLDD_PFIS", "0")
                        .data("CTLMM_PFIS", "0")
                        .data("CTLAA_PFIS", "0")
                        .data("CTLPOD_DESFIS", "0")
                        .data("CTLPOD_HASFIS", "0")
                        .data("CTLDEPPFIS", "X")
                        .data("BUTTON5", "Agregar")
                        .data("_DEPSELECC", "#")
                        .data("_PRIMERSTART", "1")
                        .data("_BORREFIS", "0")
                        .data("_INDEXFISICAS", "0")
                        .data("_ORD", "1")
                        .data("CTLREINDFIS", "1")
                        .data("_RUCTEMP", "")
                        .data("CTLBPSJUR", "0")
                        .data("CTLNOMBREJUR", "")
                        .data("CTLDD_PJUR", "0")
                        .data("CTLMM_PJUR", "0")
                        .data("CTLAA_PJUR", "0")
                        .data("CTLPOD_DESJUR", "0")
                        .data("CTLPOD_HASJUR", "0")
                        .data("CTLSOC_DESJUR", "0")
                        .data("CTLSOC_HASJUR", "0")
                        .data("CTLDEPPJUR", "X")
                        .data("_ENUM", "0")
                        .data("_BORREJUR", "0")
                        .data("_ESTADOFJ", "0")
                        .data("_INDEXJURIDICAS", "0")
                        .data("CTLPADRONAUT2", "0")
                        .data("_DEPAUT", "")
                        .data("CTLLOCAUT2", "")
                        .data("_PADRONAUX", "0")
                        .data("_MARCASAUT", "0")
                        .data("CTLIDENTRG_MODELOS_AUTOMOTOREDIT", "0")
                        .data("CTLIDENTRG_TIPOS_AUTOMOTOREDIT", "0")
                        .data("CTLPLACAMUNICIPALAUTEDIT", "")
                        .data("CTLANOAUTEDIT", "0")
                        .data("CTLANOAUT2", "0")
                        .data("CTLPADRONAUT3", "0")
                        .data("_DEPAUT2","")
                        .data("CTLLOCAUT3", "")
                        .data("CTLPLACAMUNICIPALAUT2", "")
                        .data("_PADRONAUX2", "0")
                        .data("CTLIDENTRG_MARCAS_AUTOMOTOR_A2", "0")
                        .data("CTLIDENTRG_MODELOS_AUTOMOTOR_2A", "0")
                        .data("CTLIDENTRG_TIPOS_AUTOMOTOR_A2", "0")
                        .data("_NROAUTOMOTAUX", "0")
                        .data("CTLANOAUT3", "0")
                        .data("CTLPADRONAUT4", "0")
                        .data("_DEPAUT3", "")
                        .data("CTLLOCAUT4", "")
                        .data("CTLPLACAMUNICIPALAUT3", "")
                        .data("_PADRONAUX3", "0")
                        .data("CTLIDENTRG_MARCAS_AUTOMOTOR_A3", "0")
                        .data("CTLIDENTRG_MODELOS_AUTOMOTOR_3A", "0")
                        .data("CTLIDENTRG_TIPOS_AUTOMOTOR_A3", "0")
                        .data("CTLIDENTRG_MARCAS_AUTOMOTOREDIT", "0")
                        .data("_DEPINM", "D")
                        .data("CTLLOCINM2", "C03")
                        .data("CTLPADRONINM2", "0")
                        .data("_CARTELINM", "0")
                        .data("_CARTELAUT", "0")
                        .data("CTLBLOCKINM2", "")
                        .data("CTLMANZANAINM", "")
                        .data("CTLNIVELINM2", "")
                        .data("CTLSOLARINM", "")
                        .data("CTLUNIDADINM1", "")
                        .data("CTLFRACCIONINM", "")
                        .data("CTLSJINM", "0")
                        .data("GRID2_ROW", "0")
                        .data("CTLPAISAERO", "CX")
                        .data("CTLORDAERO2", "0")
                        .data("CTLMATRICULAAERO", "")
                        .data("CTLMARCAAERO", "")
                        .data("CTLMODELOAERO", "")
                        .data("CTLSERIEAERO", "")
                        .data("GRID3_ROW", "0")
                        .data("CTLREGTES2", "")
                        .data("CTLDEPTES2", "")
                        .data("CTLANOTES2", "0")
                        .data("CTLNROTES2", "0")
                        .data("CTLNUMEROTES2", "0")
                        .data("_NROAUX", "0")
                        .data("CTLFOLIOTES2", "0")
                        .data("CTLLIBROTES2", "0")
                        .data("CTLSEDESOL", "")
                        .data("Solic", "eJx9VV1rwjAUfZ6/YvguaQcOHFlhEwXHFgV1D76I2uACthlNO/z5S/N5k4jSh3vuOfm4PaeI1/zCTqztysdrdanF69A1qFh1R1lzMSwGD/jES3bm0wsrVtv3z8V0iZFvSZ40XC4tMoxMJXvz08+iPqu2/I3Us8myF/Vg5Gmp3bBftWyKkS31DrOqr5+yfDLKs1Geq3W6qQVbce8EzUrlTLSHUu37jZEHkpmqObaieB6P88lErnMdcwWnjq8Bt5Fzz5lgp4Mwb8EizX10DSsB67HmF3XV0eOFWt5jzb91La94yxungB2pWR77UZGa9cpr2hwsXNOS2nrZlMYnU5ndacPrw5/f22HNb6hoWcVrxq0CdqQmWIACFN0TRTgYFAVoBV4hgmAVvD0UwuhmKMJfvKTFgqwxUhXM9n6HfJb3O59mWcM899TdRGu9CbLZ1cY2WOzT7I5Qib1zguVhps0RNrfgRK9ITgVUEF83uMdRhJ0CdqIYOw3sJFF2qrDn4mwm8YHuF8iPL2j4iBu5jrbe29VR0P3JoJOE3anCHjCX9AIPAlcUB2FsihJEndALEnlBEi9I4gVJvCCJF+SGF+SGFwR4oXhXh7boUSH2PhDgA0l8IIkP5IYP5IYPUoeR+8MqBv9JdjIB")
                        .data("Solicfis", "eJx1lkFv2zAMhc/rrxj6B7Iu1yxAsKxbgJULNmCHXgw31gIDthXEabGfP0UixUeuvfHxqSJjfQ/o6lcc+kN/ee6a7+ElDMvdJYzv/47DNH+6rV6Y989PqY7z7frm3erHubvv5/WH1YKr1Nucwl0qF1x/lJrieAc19pdSf95Jtf1de2HehrOo+3YYms0UeSzKcrbZhplNUOx9a9Fjlbx97HTEdtvs+ZSUqfvwULtSXn/gpnalLNfBGqDY0zVAJW83XXSNLF7aQfTP0E9d3fEwQgnDQLGnw0BdFzmHqcuqXBNOp/rJ4xjOhz6K/jJf2qcmdeEAfueIM68KvnPEmRSOzdd2aqdD3w6hzqYj3KaiOHqXiut7pCdbcNH/6UOXX0bqymbzqHSmWvlsHpFQVsyoKustVWVSuc6sSl9oZQ2AllVswzBbDhhtuFW/amBXlinIlrMqlODiqFCKi6PCkFxMow3N6uNyQjQvB0xzR6iW7TPXKsxwow3d6psvUwmXCwvj8lRKOXeQcz1kXyfaHZRv9XGH/4kXnI7mXpTIfXXxUoGW8ntVAchmRwXgK86yOgXl3JeyQp27Uhq8iVms0sFOHnZysJODnRzs5GAnAzsxbTBdYCckn5B8QvIJyScknxz55MgnRz458smQn11QLgZq1wbGItugNCPEyGPfbGy0jQu5uJCLS/lFqCE95Y+rcEkiTgE0fLLyEdexUSMXNXJRIxc1eiNqBfbX+iaBZBNINoFkE5jc1eK1f5bWN/8A9Wzh1w==")
                        .data("Solicjur", "eJxllc1uwkAMhM/lKSpegB56pEgUKkFV3KjcuESQbKWV8oMSgvr43WS96/Fy88wER843EstjW9nC3oYy/zJ3U73ub6Z+/qurpn+bx8z02XBxc9vPV7On5XdXfg7d6mW54Ml51NaXzjixGNXPUITx/drzwzw5L2vLfGuCD4qz3RkzVmPWmaac1LR6a67XMLsHTReDbZ7xgjA693CIbhidu15HN4zO3Tc3WTeJ+7mK1xnblEFs2tp0hW1Bw22gOJPbQLnso785I2w5tsXG3m2V7cCBvaA4k72gxqvdh1nwYH+tKaf7wxxp5ifh6WYkmp+AKQvP0v8ozoqsz5RWdCWPWhHmFzFjVoFyCD1cv0qEsPaJCOHtExHAnJcD9fAFmDtLIC+Oul5pxV9yvD52gPepFoin3qG06oLk+A7GSGMWZyFKQJQSopQQpYQoJUQpIepz1AB4CkUg7LA3KCBPSJ6QPCF5QvKE5EmRnyJQSQ0kjgbWwn9TUUlHiJGDoTtDSWco6QwlnSHdmSlG+dAg4kIoS3eKkk5R0ilKOuXyUT/+iaxm/xq/BYg=")
                        .data("Solicaut", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ5yvknhCrEid21ZMV3DCv7DGZ17ttoRwVfPRGtHZfGdXpPB14CNWTggy2y4dISWyOkYpWVrLN4iZe4SgbGcG98Q5bU6YCu/ZWbuVARVeZkHbXppikcBBHQPYY7Xlscyx057BihggOIMUJwB2mYwT23ZO2TQHhm0QwbtkEFrMsj8UgPDkpqjwt278g/zffo2ffe+eeOlydYfZnb4Bz5pyQw=")
                        .data("Solicautpadronanterior", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ5yvknhCrEid21ZMV3DCv7DGZ17ttoRwVfPRGtHZfGdXpPB14CNWTggy2y4dISWyOkYpWVrLN4iZe4SgbGcG98Q5bU6YCu/ZWbuVARVeZkHbXppikcBBHQPYY7Xlscyx057BihggOIMUJwB2mYwT23ZO2TQHhm0QwbtkEFrMsj8UgPDkpqjwt278g/zffo2ffe+eeOlydYfZnb4Bz5pyQw=")
                        .data("Solicautpadronanterior2", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ5yvknhCrEid21ZMV3DCv7DGZ17ttoRwVfPRGtHZfGdXpPB14CNWTggy2y4dISWyOkYpWVrLN4iZe4SgbGcG98Q5bU6YCu/ZWbuVARVeZkHbXppikcBBHQPYY7Xlscyx057BihggOIMUJwB2mYwT23ZO2TQHhm0QwbtkEFrMsj8UgPDkpqjwt278g/zffo2ffe+eeOlydYfZnb4Bz5pyQw=")
                        .data("Solicinm", "eJxlkk1OwzAQRtfkFKgXKGKdZgEIqaiklSo22UTGNpVF4kFJixCnxxnP2OOy+978OPOk1EcYnHbni+l39tsO99uzHW9/xsHPm1Xq2flweQ8Z5lVT3dT7yWz92NzVa0qh9gjGneDJfvWhsF4qO9AcD8pM4GknQ+g8DKA/eax14QKGN++MMkzHF9qOIVQ68Iq7r8r/qozhbjUxPE9Ka4cfjLNgLAf34axZXk052fVd9gv52rDvhCNBEou7EoUpDbMrYbIlRs34DsfsTDPZmpfIm1CYl+e2y7Mpy9uwI0geij1BxdXYlJgc2uzQSgesZyh9sFmw1ItPZrpyxXZZqHD+34/eVH+q0vIs")
                        .data("Solicaero", "eJxtkL0KwjAURmf7FNIXiItbDDgKioVuXUJMrnAhNZCf4uObSHON6HbOd4ccwkdnUWNMRp5hAbs/RZi3z9k+wqGnG4Qh3TK70Ituw6/eHME7seOsYl4HhaEwK3JR0aNOVjWL1405A9aRjuARvo4V8I5gykvEnwA5NQlZmgg5/WbQtoZUp5R1oJi3c/bvi0T3AjoIZVI=")
                        .data("Solictes", "eJxtkssKwkAMRdf2K6Q/MK5cjQVBBKFGUVfdFGqjDPQhfYifb+pMMxlwd3LvNJxA9bWtzN0MY5mn+MZqfRiwXn7qquk3MXfYn8eCuO3jJFroU1fesE9WWjmi7IJPIjXhDl8zbpvWvXREGXRz5oiy1BScMlO+J4M5Z552jDX6NTxQc2xLVA7Mw2A5vWBm+Tzz+sR8QJ6JE9xg1e0XzHyIzZnFMbYRkzjIdmKSR7mVYvR+MFXMXvWXM3trENYgrEFYQ2ANgTUE1hBYQ2gNoTW1Wv37vZLoC/YgyUs=")
                        .data("nRC_Grid1", "0")
                        .data("nRC_Grid2", "0")
                        .data("nRC_Grid3", "0")
                        .data("nRC_Grid4", "0")
                        .data("sCallerURL", "http://www.dgr.gub.uy/sr/principal.jsf")
                        .method(Connection.Method.POST);

                response = connection.execute();

                document = response.parse();
                
                String[] listaSplit = document.body().html().split("\n");
                result = Arrays.asList(listaSplit);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        // 6551990
        // VNZANU

        return result;
    }

    public Map<String, Object> getDocumentEight(String username, String password) {
        Map<String, Object> result = new HashMap<>();

        if (null != username && null != password) {
            Connection.Response response = null;
            Map<String, String> cookies = new HashMap<>();
            Connection connection = null;
            Document document = null;

            try {
                Map<String, String> headers = new HashMap<>();

                headers = this.getRequestHeaderBefore();
                connection = Jsoup.connect("https://www.dgr.gub.uy/sr/loginStart.jsf").headers(headers)
                        .method(Connection.Method.GET);

                response = connection.execute();
                cookies = response.cookies();

                result.put("cookies", response.cookies());
                System.out.println("cookies " + response.cookies());

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Map<String, String> headersTwo = new HashMap<>();

                headersTwo = this.getRequestHeaderAfter(cookies.get("JSESSIONID"));

                connection = Jsoup.connect("https://www.dgr.gub.uy/sr/j_security_check")
                        .headers(headersTwo)
                        .data("j_username", username)
                        .data("j_password", password)
                        .cookies(cookies)
                        .method(Connection.Method.POST);

                response = connection.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                connection = Jsoup.connect("https://www.dgr.gub.uy/etimbreapp/servlet/hlogin")
                        .cookies(cookies)
                        .data("_USER", username)
                        .data("_PASSWORD", password)
                        .data("_EventName", "E'LOGIN'.")
                        .method(Connection.Method.POST);

                response = connection.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                connection = Jsoup.connect("https://www.dgr.gub.uy/etimbreapp/servlet/hinicio")
                        .cookies(cookies)
                        .method(Connection.Method.GET);

                response = connection.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                connection = Jsoup.connect("http://www.dgr.gub.uy/sr/principal.jsf")
                        .cookies(cookies)
                        .method(Connection.Method.GET);

                response = connection.execute();
                result.put("response", response);

                document = response.parse();
                result.put("document", document);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        // 87654321
        // notariosuycyt

        return result;
    }

    public List<String> getPhysicalPerson(Map<String, String> cookies, Connection connection) {
        List<String> result = new ArrayList<String>();

        if (null != cookies) {
            Connection.Response response;
            try {
                Document doc = null;
                connection = Jsoup.connect("http://www.dgr.gub.uy/sr/hPersonasFisicas.jsf").cookies(cookies)
                        .method(Connection.Method.GET).followRedirects(true);

                response = connection.execute();

                doc = response.parse();
                String[] listaSplit = doc.body().html().split("\n");
                result = Arrays.asList(listaSplit);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<String> getPhysicalPersonTwo(Map<String, String> cookies, String username) {
        List<String> result = new ArrayList<String>();

        if (null != cookies) {
            Document document = null;
            Connection.Response response = null;
            Connection connection = null;
            Date date = new Date();

            /*
            try {
                connection = Jsoup.connect("http://www.dgr.gub.uy/sr/principal.jsf")
                        .cookies(cookies)
                        .data("j_id15", "j_id15")
                        .data("j_id15:mpOpenedState", "")
                        .data("javax.faces.ViewState", "j_id1")
                        .data("j_id15:j_id44:hidden", "j_id15:j_id44")
                        .method(Connection.Method.GET);

                response = connection.execute();
                

            } catch (Exception e) {
                e.printStackTrace();
            }
            */
            
            try {
                connection = Jsoup.connect("http://www.dgr.gub.uy/etimbreapp/servlet/hsolicitudform?1")
                        .cookies(cookies)
                        .data("_EventName", "E'AGREGARFIS'.")
                        .data("_EventGridId", "367")
                        .data("_EventRowId", "1")
                        .data("CTLCODIGOUS", "6551990")
                        .data("CTLCODIGOCLI", "PUBLICO")
                        .data("_FCHING", "11/10/2019")
                        .data("CTLFCHEMSOL", "11/10/2019")
                        .data("_NROSOLIC", "0")
                        .data("CTLTIPOSOL", "C")
                        .data("_NROFISICAS", "0")
                        .data("_NROJURIDICAS", "0")
                        .data("_NROAUTOMOT", "0")
                        .data("_NROINM", "0")
                        .data("_NROAERONAVES", "0")
                        .data("_NROTESTIMONIOS", "0")
                        .data("_CITEMP", "0")
                        .data("CTLDVFIS", "")
                        .data("CTLAPE1FIS", "ROJAS")
                        .data("CTLAPE2FIS", "") 
                        .data("CTLNOM1FIS", "JAIME")
                        .data("CTLNOM2FIS", "")
                        .data("CTLNOM3FIS", "")
                        .data("CTLINTERVALFIS", "I")
                        .data("CTLFALL_ANOFIS", "0")
                        .data("CTLCES_HASFIS", "0")
                        .data("CTLNG_DESFIS", "0")
                        .data("CTLNG_HASFIS", "0")
                        .data("CTLDCM_DESFIS", "0")
                        .data("CTLDCM_HASFIS", "0")
                        .data("CTLDD_PFIS", "0")
                        .data("CTLMM_PFIS", "0")
                        .data("CTLAA_PFIS", "0")
                        .data("CTLPOD_DESFIS", "0")
                        .data("CTLPOD_HASFIS", "0")
                        .data("CTLDEPPFIS", "X")
                        .data("BUTTON5", "Agregar")
                        .data("_DEPSELECC", "#")
                        .data("_PRIMERSTART", "1")
                        .data("_BORREFIS", "0")
                        .data("_INDEXFISICAS", "0")
                        .data("_ORD", "1")
                        .data("CTLREINDFIS", "1")
                        .data("_RUCTEMP", "")
                        .data("CTLBPSJUR", "0")
                        .data("CTLNOMBREJUR", "")
                        .data("CTLDD_PJUR", "0")
                        .data("CTLMM_PJUR", "0")
                        .data("CTLAA_PJUR", "0")
                        .data("CTLPOD_DESJUR", "0")
                        .data("CTLPOD_HASJUR", "0")
                        .data("CTLSOC_DESJUR", "0")
                        .data("CTLSOC_HASJUR", "0")
                        .data("CTLDEPPJUR", "X")
                        .data("_ENUM", "0")
                        .data("_BORREJUR", "0")
                        .data("_ESTADOFJ", "0")
                        .data("_INDEXJURIDICAS", "0")
                        .data("CTLPADRONAUT2", "0")
                        .data("_DEPAUT", "")
                        .data("CTLLOCAUT2", "")
                        .data("_PADRONAUX", "0")
                        .data("_MARCASAUT", "0")
                        .data("CTLIDENTRG_MODELOS_AUTOMOTOREDIT", "0")
                        .data("CTLIDENTRG_TIPOS_AUTOMOTOREDIT", "0")
                        .data("CTLPLACAMUNICIPALAUTEDIT", "")
                        .data("CTLANOAUTEDIT", "0")
                        .data("CTLANOAUT2", "0")
                        .data("CTLPADRONAUT3", "0")
                        .data("_DEPAUT2","")
                        .data("CTLLOCAUT3", "")
                        .data("CTLPLACAMUNICIPALAUT2", "")
                        .data("_PADRONAUX2", "0")
                        .data("CTLIDENTRG_MARCAS_AUTOMOTOR_A2", "0")
                        .data("CTLIDENTRG_MODELOS_AUTOMOTOR_2A", "0")
                        .data("CTLIDENTRG_TIPOS_AUTOMOTOR_A2", "0")
                        .data("_NROAUTOMOTAUX", "0")
                        .data("CTLANOAUT3", "0")
                        .data("CTLPADRONAUT4", "0")
                        .data("_DEPAUT3", "")
                        .data("CTLLOCAUT4", "")
                        .data("CTLPLACAMUNICIPALAUT3", "")
                        .data("_PADRONAUX3", "0")
                        .data("CTLIDENTRG_MARCAS_AUTOMOTOR_A3", "0")
                        .data("CTLIDENTRG_MODELOS_AUTOMOTOR_3A", "0")
                        .data("CTLIDENTRG_TIPOS_AUTOMOTOR_A3", "0")
                        .data("CTLIDENTRG_MARCAS_AUTOMOTOREDIT", "0")
                        .data("_DEPINM", "D")
                        .data("CTLLOCINM2", "C03")
                        .data("CTLPADRONINM2", "0")
                        .data("_CARTELINM", "0")
                        .data("_CARTELAUT", "0")
                        .data("CTLBLOCKINM2", "")
                        .data("CTLMANZANAINM", "")
                        .data("CTLNIVELINM2", "")
                        .data("CTLSOLARINM", "")
                        .data("CTLUNIDADINM1", "")
                        .data("CTLFRACCIONINM", "")
                        .data("CTLSJINM", "0")
                        .data("GRID2_ROW", "0")
                        .data("CTLPAISAERO", "CX")
                        .data("CTLORDAERO2", "0")
                        .data("CTLMATRICULAAERO", "")
                        .data("CTLMARCAAERO", "")
                        .data("CTLMODELOAERO", "")
                        .data("CTLSERIEAERO", "")
                        .data("GRID3_ROW", "0")
                        .data("CTLREGTES2", "")
                        .data("CTLDEPTES2", "")
                        .data("CTLANOTES2", "0")
                        .data("CTLNROTES2", "0")
                        .data("CTLNUMEROTES2", "0")
                        .data("_NROAUX", "0")
                        .data("CTLFOLIOTES2", "0")
                        .data("CTLLIBROTES2", "0")
                        .data("CTLSEDESOL", "")
                        .data("Solic", "eJx9VV1rwjAUfZ6/YvguaQcOHFlhEwXHFgV1D76I2uACthlNO/z5S/N5k4jSh3vuOfm4PaeI1/zCTqztysdrdanF69A1qFh1R1lzMSwGD/jES3bm0wsrVtv3z8V0iZFvSZ40XC4tMoxMJXvz08+iPqu2/I3Us8myF/Vg5Gmp3bBftWyKkS31DrOqr5+yfDLKs1Geq3W6qQVbce8EzUrlTLSHUu37jZEHkpmqObaieB6P88lErnMdcwWnjq8Bt5Fzz5lgp4Mwb8EizX10DSsB67HmF3XV0eOFWt5jzb91La94yxungB2pWR77UZGa9cpr2hwsXNOS2nrZlMYnU5ndacPrw5/f22HNb6hoWcVrxq0CdqQmWIACFN0TRTgYFAVoBV4hgmAVvD0UwuhmKMJfvKTFgqwxUhXM9n6HfJb3O59mWcM899TdRGu9CbLZ1cY2WOzT7I5Qib1zguVhps0RNrfgRK9ITgVUEF83uMdRhJ0CdqIYOw3sJFF2qrDn4mwm8YHuF8iPL2j4iBu5jrbe29VR0P3JoJOE3anCHjCX9AIPAlcUB2FsihJEndALEnlBEi9I4gVJvCCJF+SGF+SGFwR4oXhXh7boUSH2PhDgA0l8IIkP5IYP5IYPUoeR+8MqBv9JdjIB")
                        .data("Solicfis", "eJx1lkFv2zAMhc/rrxj6B7Iu1yxAsKxbgJULNmCHXgw31gIDthXEabGfP0UixUeuvfHxqSJjfQ/o6lcc+kN/ee6a7+ElDMvdJYzv/47DNH+6rV6Y989PqY7z7frm3erHubvv5/WH1YKr1Nucwl0qF1x/lJrieAc19pdSf95Jtf1de2HehrOo+3YYms0UeSzKcrbZhplNUOx9a9Fjlbx97HTEdtvs+ZSUqfvwULtSXn/gpnalLNfBGqDY0zVAJW83XXSNLF7aQfTP0E9d3fEwQgnDQLGnw0BdFzmHqcuqXBNOp/rJ4xjOhz6K/jJf2qcmdeEAfueIM68KvnPEmRSOzdd2aqdD3w6hzqYj3KaiOHqXiut7pCdbcNH/6UOXX0bqymbzqHSmWvlsHpFQVsyoKustVWVSuc6sSl9oZQ2AllVswzBbDhhtuFW/amBXlinIlrMqlODiqFCKi6PCkFxMow3N6uNyQjQvB0xzR6iW7TPXKsxwow3d6psvUwmXCwvj8lRKOXeQcz1kXyfaHZRv9XGH/4kXnI7mXpTIfXXxUoGW8ntVAchmRwXgK86yOgXl3JeyQp27Uhq8iVms0sFOHnZysJODnRzs5GAnAzsxbTBdYCckn5B8QvIJyScknxz55MgnRz458smQn11QLgZq1wbGItugNCPEyGPfbGy0jQu5uJCLS/lFqCE95Y+rcEkiTgE0fLLyEdexUSMXNXJRIxc1eiNqBfbX+iaBZBNINoFkE5jc1eK1f5bWN/8A9Wzh1w==")
                        .data("Solicjur", "eJxllc1uwkAMhM/lKSpegB56pEgUKkFV3KjcuESQbKWV8oMSgvr43WS96/Fy88wER843EstjW9nC3oYy/zJ3U73ub6Z+/qurpn+bx8z02XBxc9vPV7On5XdXfg7d6mW54Ml51NaXzjixGNXPUITx/drzwzw5L2vLfGuCD4qz3RkzVmPWmaac1LR6a67XMLsHTReDbZ7xgjA693CIbhidu15HN4zO3Tc3WTeJ+7mK1xnblEFs2tp0hW1Bw22gOJPbQLnso785I2w5tsXG3m2V7cCBvaA4k72gxqvdh1nwYH+tKaf7wxxp5ifh6WYkmp+AKQvP0v8ozoqsz5RWdCWPWhHmFzFjVoFyCD1cv0qEsPaJCOHtExHAnJcD9fAFmDtLIC+Oul5pxV9yvD52gPepFoin3qG06oLk+A7GSGMWZyFKQJQSopQQpYQoJUQpIepz1AB4CkUg7LA3KCBPSJ6QPCF5QvKE5EmRnyJQSQ0kjgbWwn9TUUlHiJGDoTtDSWco6QwlnSHdmSlG+dAg4kIoS3eKkk5R0ilKOuXyUT/+iaxm/xq/BYg=")
                        .data("Solicaut", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ5yvknhCrEid21ZMV3DCv7DGZ17ttoRwVfPRGtHZfGdXpPB14CNWTggy2y4dISWyOkYpWVrLN4iZe4SgbGcG98Q5bU6YCu/ZWbuVARVeZkHbXppikcBBHQPYY7Xlscyx057BihggOIMUJwB2mYwT23ZO2TQHhm0QwbtkEFrMsj8UgPDkpqjwt278g/zffo2ffe+eeOlydYfZnb4Bz5pyQw=")
                        .data("Solicautpadronanterior", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ5yvknhCrEid21ZMV3DCv7DGZ17ttoRwVfPRGtHZfGdXpPB14CNWTggy2y4dISWyOkYpWVrLN4iZe4SgbGcG98Q5bU6YCu/ZWbuVARVeZkHbXppikcBBHQPYY7Xlscyx057BihggOIMUJwB2mYwT23ZO2TQHhm0QwbtkEFrMsj8UgPDkpqjwt278g/zffo2ffe+eeOlydYfZnb4Bz5pyQw=")
                        .data("Solicautpadronanterior2", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ5yvknhCrEid21ZMV3DCv7DGZ17ttoRwVfPRGtHZfGdXpPB14CNWTggy2y4dISWyOkYpWVrLN4iZe4SgbGcG98Q5bU6YCu/ZWbuVARVeZkHbXppikcBBHQPYY7Xlscyx057BihggOIMUJwB2mYwT23ZO2TQHhm0QwbtkEFrMsj8UgPDkpqjwt278g/zffo2ffe+eeOlydYfZnb4Bz5pyQw=")
                        .data("Solicinm", "eJxlkk1OwzAQRtfkFKgXKGKdZgEIqaiklSo22UTGNpVF4kFJixCnxxnP2OOy+978OPOk1EcYnHbni+l39tsO99uzHW9/xsHPm1Xq2flweQ8Z5lVT3dT7yWz92NzVa0qh9gjGneDJfvWhsF4qO9AcD8pM4GknQ+g8DKA/eax14QKGN++MMkzHF9qOIVQ68Iq7r8r/qozhbjUxPE9Ka4cfjLNgLAf34axZXk052fVd9gv52rDvhCNBEou7EoUpDbMrYbIlRs34DsfsTDPZmpfIm1CYl+e2y7Mpy9uwI0geij1BxdXYlJgc2uzQSgesZyh9sFmw1ItPZrpyxXZZqHD+34/eVH+q0vIs")
                        .data("Solicaero", "eJxtkL0KwjAURmf7FNIXiItbDDgKioVuXUJMrnAhNZCf4uObSHON6HbOd4ccwkdnUWNMRp5hAbs/RZi3z9k+wqGnG4Qh3TK70Ituw6/eHME7seOsYl4HhaEwK3JR0aNOVjWL1405A9aRjuARvo4V8I5gykvEnwA5NQlZmgg5/WbQtoZUp5R1oJi3c/bvi0T3AjoIZVI=")
                        .data("Solictes", "eJxtkssKwkAMRdf2K6Q/MK5cjQVBBKFGUVfdFGqjDPQhfYifb+pMMxlwd3LvNJxA9bWtzN0MY5mn+MZqfRiwXn7qquk3MXfYn8eCuO3jJFroU1fesE9WWjmi7IJPIjXhDl8zbpvWvXREGXRz5oiy1BScMlO+J4M5Z552jDX6NTxQc2xLVA7Mw2A5vWBm+Tzz+sR8QJ6JE9xg1e0XzHyIzZnFMbYRkzjIdmKSR7mVYvR+MFXMXvWXM3trENYgrEFYQ2ANgTUE1hBYQ2gNoTW1Wv37vZLoC/YgyUs=")
                        .data("nRC_Grid1", "0")
                        .data("nRC_Grid2", "0")
                        .data("nRC_Grid3", "0")
                        .data("nRC_Grid4", "0")
                        .data("sCallerURL", "http://www.dgr.gub.uy/sr/principal.jsf")
                        .method(Connection.Method.POST);

                response = connection.execute();
                document = response.parse();
                
                String[] listaSplit = document.body().html().split("\n");
                result = Arrays.asList(listaSplit);

            } catch (Exception e) {
                e.printStackTrace();
            }

            /*
            try {
                connection = Jsoup.connect("http://www.dgr.gub.uy/etimbreapp/servlet/hsolicitudform?1")
                        .cookies(cookies)
                        .data(this.sendDataPhysicalPerson(username))
                        .method(Connection.Method.POST);

                response = connection.execute();
                document = response.parse();

            } catch (Exception e) {
                e.printStackTrace();
            }
            */

            /*
            try {
                connection = Jsoup.connect("http://www.dgr.gub.uy/sr/verTramite.jsf?numeroSolicitud")
                        .cookies(cookies)
                        .method(Connection.Method.GET);

                response = connection.execute();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            */

        }

        return result;
    }

    private Map<String, String> addDataPhysicalPerson(String username) {
        Map<String, String> data = new HashMap<>();
        Date date = new Date();

        data.put("_EventName", "E'AGREGARFIS'.");
        data.put("_EventGridId", "367");
        data.put("_EventRowId", "1");
        data.put("CTLCODIGOUS", username);
        data.put("CTLCODIGOCLI", "PUBLICO");
        data.put("_FCHING", sdf.format(date));
        data.put("CTLFCHEMSOL", sdf.format(date));
        data.put("_NROSOLIC", "0");
        data.put("CTLTIPOSOL", "C");
        data.put("_NROFISICAS", "0");
        data.put("_NROJURIDICAS", "0");
        data.put("_NROAUTOMOT", "0");
        data.put("_NROINM", "0");
        data.put("_NROAERONAVES", "0");
        data.put("_NROTESTIMONIOS", "0");
        data.put("_CITEMP", "0");
        data.put("CTLDVFIS", "");
        data.put("CTLAPE1FIS", "FLORES");
        data.put("CTLAPE2FIS", "");      
        data.put("CTLNOM1FIS", "JUAN");
        data.put("CTLNOM2FIS", ""); 
        data.put("CTLNOM3FIS", "");
        data.put("CTLINTERVALFIS", "I");
        data.put("CTLFALL_ANOFIS", "0");
        data.put("CTLCES_HASFIS", "0");
        data.put("CTLNG_DESFIS", "0");
        data.put("CTLNG_HASFIS", "0");
        data.put("CTLDCM_DESFIS", "0");
        data.put("CTLDCM_HASFIS", "0");
        data.put("CTLDD_PFIS", "0");
        data.put("CTLMM_PFIS", "0");
        data.put("CTLAA_PFIS", "0");
        data.put("CTLPOD_DESFIS", "0");
        data.put("CTLPOD_HASFIS", "0");
        data.put("CTLDEPPFIS", "0");
        data.put("BUTTON5", "Agregar");
        data.put("_DEPSELECC", "#");
        data.put("_PRIMERSTART", "1");
        data.put("_BORREFIS", "0");
        data.put("_INDEXFISICAS", "0");
        data.put("_ORD", "1");
        data.put("CTLREINDFIS", "1");
        data.put("_RUCTEMP", "");
        data.put("CTLBPSJUR", "0");
        data.put("CTLNOMBREJUR", "");
        data.put("CTLDD_PJUR", "0");
        data.put("CTLMM_PJUR", "0");
        data.put("CTLAA_PJUR", "0");
        data.put("CTLPOD_DESJUR", "0");
        data.put("CTLPOD_HASJUR", "0");
        data.put("CTLSOC_DESJUR", "0");
        data.put("CTLSOC_HASJUR", "0");
        data.put("CTLDEPPJUR", "X");
        data.put("_ENUM", "0");
        data.put("_BORREJUR", "0");
        data.put("_ESTADOFJ", "0");
        data.put("_INDEXJURIDICAS", "0");
        data.put("CTLPADRONAUT2", "0");
        data.put("_DEPAUT", "");
        data.put("CTLLOCAUT2", "");
        data.put("_PADRONAUX", "0");
        data.put("_MARCASAUT", "0");
        data.put("CTLIDENTRG_MODELOS_AUTOMOTOREDIT", "0");
        data.put("CTLIDENTRG_TIPOS_AUTOMOTOREDIT", "0");
        data.put("CTLPLACAMUNICIPALAUTEDIT", "");
        data.put("CTLANOAUTEDIT", "0");
        data.put("CTLANOAUT2", "0");
        data.put("CTLPADRONAUT3", "0");
        data.put("_DEPAUT2","");
        data.put("CTLLOCAUT3", "");
        data.put("CTLPLACAMUNICIPALAUT2", "");
        data.put("_PADRONAUX2", "0");
        data.put("CTLIDENTRG_MARCAS_AUTOMOTOR_A2", "0");
        data.put("CTLIDENTRG_MODELOS_AUTOMOTOR_2A", "0");
        data.put("CTLIDENTRG_TIPOS_AUTOMOTOR_A2", "0");
        data.put("_NROAUTOMOTAUX", "0");
        data.put("CTLANOAUT3", "0");
        data.put("CTLPADRONAUT4", "0");
        data.put("_DEPAUT3", "");
        data.put("CTLLOCAUT4", "");
        data.put("CTLPLACAMUNICIPALAUT3", "");
        data.put("_PADRONAUX3", "0");
        data.put("CTLIDENTRG_MARCAS_AUTOMOTOR_A3", "0");
        data.put("CTLIDENTRG_MODELOS_AUTOMOTOR_3A", "0");
        data.put("CTLIDENTRG_TIPOS_AUTOMOTOR_A3", "0");
        data.put("CTLIDENTRG_MARCAS_AUTOMOTOREDIT", "0");
        data.put("_DEPINM", "D");
        data.put("CTLLOCINM2", "C03");
        data.put("CTLPADRONINM2", "0");
        data.put("_CARTELINM", "0");
        data.put("_CARTELAUT", "0");
        data.put("CTLBLOCKINM2", "");
        data.put("CTLMANZANAINM", "");
        data.put("CTLNIVELINM2", "");
        data.put("CTLSOLARINM", "");
        data.put("CTLUNIDADINM1", "");
        data.put("CTLFRACCIONINM", "");
        data.put("CTLSJINM", "0");
        data.put("GRID2_ROW", "0");
        data.put("CTLPAISAERO", "CX");
        data.put("CTLORDAERO2", "0");
        data.put("CTLMATRICULAAERO", "");
        data.put("CTLMARCAAERO", "");
        data.put("CTLMODELOAERO", "");
        data.put("CTLSERIEAERO", "");
        data.put("GRID3_ROW", "0");
        data.put("CTLREGTES2", "");
        data.put("CTLDEPTES2", "");
        data.put("CTLANOTES2", "0");
        data.put("CTLNROTES2", "0");
        data.put("CTLNUMEROTES2", "0");
        data.put("_NROAUX", "0");
        data.put("CTLFOLIOTES2", "0");
        data.put("CTLLIBROTES2", "0");
        data.put("CTLSEDESOL", "");
        data.put("Solic", "eJx9VV1rwjAUfZ6/Yvgu0YGDjqywiULHFgV1D76I2uACthlNO/z5S/N5k4jiwz33nNz09hwRr/mFnVjblY/X6lKL16FrULHqjrLmYpgPHvCJl+zMZxeWr7bvn8VsiZFvSZ40XB7NxxiZSvYWp5+iPqu2/IzUdzMev6gvRp6W2g37VcdmGNlST5hXff00nmSjiRyQqXO6qQVbce8GzUrlXLSHUs39xsgDyczUHluRP0+nkyyT51zHPIJTx48Bx8i9F0yw00GYt2CR5j66hpWA9VjzRV119HihlvdY829dyyve8sYpYEdqlsd+VaR2vfKaNgcL17Sktl42pfHJVGY6bXh9+POzHdb8hoqWVbxm3CpgR2qCAyhA0XOiCAeLogCtwCtEEKyCt4dCGD0ZivAXL2lekDVGqoLZ3u+Qz/J+59Msa5jnnrqbaK03QTZTbWyDwz7N7gqV2Ds3WB5m2lxhcwtu9IrkVkAF8XWLexxF2ClgJ4qx08BOEmWnCnsuzmYTH+j+wET+gGHDR9zIdbT1bFdHQfc3g04SdqcKe8Bc0gs8CFxRHISxKUoQdUIvSOQFSbwgiRck8YIkXpAbXpAbXhDgheJdHdqiV4XY+0CADyTxgSQ+kBs+kBs+SB1G7g8rH/wDm0syDw==");
        data.put("Solicfis", "eJx1lkFv2zAMhc/rrxj6B7Iu1yxAsKxbgJULNmCHXgw31gIDthXEabGfP0UixUeuvfHxqSJjfQ/o6lcc+kN/ee6a7+ElDMvdJYzv/47DNH+6rV6Y989PqY7z7frm3erHubvv5/WH1YKr1Nucwl0qF1x/lJrieAc19pdSf95Jtf1de2HehrOo+3YYms0UeSzKcrbZhplNUOx9a9Fjlbx97HTEdtvs+ZSUqfvwULtSXn/gpnalLNfBGqDY0zVAJW83XXSNLF7aQfTP0E9d3fEwQgnDQLGnw0BdFzmHqcuqXBNOp/rJ4xjOhz6K/jJf2qcmdeEAfueIM68KvnPEmRSOzdd2aqdD3w6hzqYj3KaiOHqXiut7pCdbcNH/6UOXX0bqymbzqHSmWvlsHpFQVsyoKustVWVSuc6sSl9oZQ2AllVswzBbDhhtuFW/amBXlinIlrMqlODiqFCKi6PCkFxMow3N6uNyQjQvB0xzR6iW7TPXKsxwow3d6psvUwmXCwvj8lRKOXeQcz1kXyfaHZRv9XGH/4kXnI7mXpTIfXXxUoGW8ntVAchmRwXgK86yOgXl3JeyQp27Uhq8iVms0sFOHnZysJODnRzs5GAnAzsxbTBdYCckn5B8QvIJyScknxz55MgnRz458smQn11QLgZq1wbGItugNCPEyGPfbGy0jQu5uJCLS/lFqCE95Y+rcEkiTgE0fLLyEdexUSMXNXJRIxc1eiNqBfbX+iaBZBNINoFkE5jc1eK1f5bWN/8A9Wzh1w==)");
        data.put("Solicjur", "eJxllc1uwkAMhM/lKSpegB56pEgUKkFV3KjcuESQbKWV8oMSgvr43WS96/Fy88wER843EstjW9nC3oYy/zJ3U73ub6Z+/qurpn+bx8z02XBxc9vPV7On5XdXfg7d6mW54Ml51NaXzjixGNXPUITx/drzwzw5L2vLfGuCD4qz3RkzVmPWmaac1LR6a67XMLsHTReDbZ7xgjA693CIbhidu15HN4zO3Tc3WTeJ+7mK1xnblEFs2tp0hW1Bw22gOJPbQLnso785I2w5tsXG3m2V7cCBvaA4k72gxqvdh1nwYH+tKaf7wxxp5ifh6WYkmp+AKQvP0v8ozoqsz5RWdCWPWhHmFzFjVoFyCD1cv0qEsPaJCOHtExHAnJcD9fAFmDtLIC+Oul5pxV9yvD52gPepFoin3qG06oLk+A7GSGMWZyFKQJQSopQQpYQoJUQpIepz1AB4CkUg7LA3KCBPSJ6QPCF5QvKE5EmRnyJQSQ0kjgbWwn9TUUlHiJGDoTtDSWco6QwlnSHdmSlG+dAg4kIoS3eKkk5R0ilKOuXyUT/+iaxm/xq/BYg=)");
        data.put("Solicaut", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ5yvknhCrEid21ZMV3DCv7DGZ17ttoRwVfPRGtHZfGdXpPB14CNWTggy2y4dISWyOkYpWVrLN4iZe4SgbGcG98Q5bU6YCu/ZWbuVARVeZkHbXppikcBBHQPYY7Xlscyx057BihggOIMUJwB2mYwT23ZO2TQHhm0QwbtkEFrMsj8UgPDkpqjwt278g/zffo2ffe+eeOlydYfZnb4Bz5pyQw=)");
        data.put("Solicautpadronanterior", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ5yvknhCrEid21ZMV3DCv7DGZ17ttoRwVfPRGtHZfGdXpPB14CNWTggy2y4dISWyOkYpWVrLN4iZe4SgbGcG98Q5bU6YCu/ZWbuVARVeZkHbXppikcBBHQPYY7Xlscyx057BihggOIMUJwB2mYwT23ZO2TQHhm0QwbtkEFrMsj8UgPDkpqjwt278g/zffo2ffe+eeOlydYfZnb4Bz5pyQw=)");
        data.put("Solicautpadronanterior2", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ5yvknhCrEid21ZMV3DCv7DGZ17ttoRwVfPRGtHZfGdXpPB14CNWTggy2y4dISWyOkYpWVrLN4iZe4SgbGcG98Q5bU6YCu/ZWbuVARVeZkHbXppikcBBHQPYY7Xlscyx057BihggOIMUJwB2mYwT23ZO2TQHhm0QwbtkEFrMsj8UgPDkpqjwt278g/zffo2ffe+eeOlydYfZnb4Bz5pyQw=)");
        data.put("Solicinm", "eJxlkk1OwzAQRtfkFKgXKGKdZgEIqaiklSo22UTGNpVF4kFJixCnxxnP2OOy+978OPOk1EcYnHbni+l39tsO99uzHW9/xsHPm1Xq2flweQ8Z5lVT3dT7yWz92NzVa0qh9gjGneDJfvWhsF4qO9AcD8pM4GknQ+g8DKA/eax14QKGN++MMkzHF9qOIVQ68Iq7r8r/qozhbjUxPE9Ka4cfjLNgLAf34axZXk052fVd9gv52rDvhCNBEou7EoUpDbMrYbIlRs34DsfsTDPZmpfIm1CYl+e2y7Mpy9uwI0geij1BxdXYlJgc2uzQSgesZyh9sFmw1ItPZrpyxXZZqHD+34/eVH+q0vIs)");
        data.put("Solicaero", "eJxtkL0KwjAURmf7FNIXiItbDDgKioVuXUJMrnAhNZCf4uObSHON6HbOd4ccwkdnUWNMRp5hAbs/RZi3z9k+wqGnG4Qh3TK70Ituw6/eHME7seOsYl4HhaEwK3JR0aNOVjWL1405A9aRjuARvo4V8I5gykvEnwA5NQlZmgg5/WbQtoZUp5R1oJi3c/bvi0T3AjoIZVI=)");
        data.put("Solictes", "eJxtkssKwkAMRdf2K6Q/MK5cjQVBBKFGUVfdFGqjDPQhfYifb+pMMxlwd3LvNJxA9bWtzN0MY5mn+MZqfRiwXn7qquk3MXfYn8eCuO3jJFroU1fesE9WWjmi7IJPIjXhDl8zbpvWvXREGXRz5oiy1BScMlO+J4M5Z552jDX6NTxQc2xLVA7Mw2A5vWBm+Tzz+sR8QJ6JE9xg1e0XzHyIzZnFMbYRkzjIdmKSR7mVYvR+MFXMXvWXM3trENYgrEFYQ2ANgTUE1hBYQ2gNoTW1Wv37vZLoC/YgyUs=)");
        data.put("nRC_Grid1", "0");
        data.put("nRC_Grid2", "0");
        data.put("nRC_Grid3", "0");
        data.put("nRC_Grid4", "0");
        data.put("sCallerURL", "http://www.dgr.gub.uy/sr/principal.jsf");
        
        return data;
    }
    
    
    private Map<String, String> sendDataPhysicalPerson(String username) {
        Map<String, String> response = new HashMap<>();
        Date date = new Date();
        System.out.println("date " + sdf.format(date));

        response.put("_EventName", "E'AGREGARFIS'.");
        response.put("_EventGridId", "367");
        response.put("_EventRowId", "1");
        response.put("CTLCODIGOUS", username);
        response.put("CTLCODIGOCLI", "PUBLICO");
        response.put("_FCHING", sdf.format(date));
        response.put("CTLFCHEMSOL", sdf.format(date));
        response.put("_NROSOLIC", "0");
        response.put("CTLTIPOSOL", "C");
        response.put("_NROFISICAS", "1");
        response.put("_NROJURIDICAS", "0");
        response.put("_NROAUTOMOT", "0");
        response.put("_NROINM", "0");
        response.put("_NROAERONAVES", "0");
        response.put("_NROTESTIMONIOS", "0");
        response.put("_CITEMP", "");
        response.put("CTLDVFIS", "");
        response.put("CTLAPE1FIS", "");
        response.put("CTLAPE2FIS", "");
        response.put("CTLNOM1FIS", "");
        response.put("CTLNOM2FIS", "");
        response.put("CTLNOM3FIS", "");
        response.put("CTLINTERFIS", "1");
        response.put("CTLFALL_ANOFIS", "0");
        response.put("CTLCES_HASFIS", "0");
        response.put("CTLNG_DESFIS", "0");
        response.put("CTLNG_HASFIS", "0");
        response.put("CTLDCM_DESFIS", "0");
        response.put("CTLDCM_HASFIS", "0");
        response.put("CTLDD_PFIS", "0");
        response.put("CTLMM_PFIS", "0");
        response.put("CTLAA_PFIS", "0");
        response.put("CTLPOD_DESFIS", "0");
        response.put("CTLPOD_HASFIS", "0");
        response.put("CTLDEPPFIS", "X");
        response.put("_DEPSELECC", "#");
        response.put("_PRIMERSTART", "1");
        response.put("_BORREFIS", "0");
        response.put("_INDEXFISICAS", "2");
        response.put("_ORD", "2");
        response.put("_RUCTEMP", "");
        response.put("CTLBPSJUR", "0");
        response.put("CTLNOMBREJUR", "");
        response.put("CTLDD_PJUR", "0");
        response.put("CTLMM_PJUR", "0");
        response.put("CTLAA_PJUR", "0");
        response.put("CTLPOD_DESJUR", "0");
        response.put("CTLPOD_HASJUR", "0");
        response.put("CTLSOC_DESJUR", "0");
        response.put("CTLSOC_HASJUR", "0");
        response.put("CTLDEPPJUR", "X");
        response.put("_ENUM", "1");
        response.put("_BORREJUR", "0");
        response.put("_ESTADOFJ", "1");
        response.put("_INDEXJURIDICAS", "0");
        response.put("CTLPADRONAUT2", "0");
        response.put("_DEPAUT", "");
        response.put("_PADRONAUX", "0");
        response.put("_MARCASAUT", "0");
        response.put("CTLIDENTRG_MODELOS_AUTOMOTOREDIT", "0");
        response.put("CTLIDENTRG_TIPOS_AUTOMOTOREDIT", "");
        response.put("CTLPLACAMUNICIPALAUTEDIT", "");
        response.put("CTLANOAUTEDIT", "0");
        response.put("CTLANOAUT2", "0");
        response.put("CTLPADRONAUT3", "0");
        response.put("_DEPAUT2", "");
        response.put("CTLPLACAMUNICIPALAUT2", "");
        response.put("_PADRONAUX2", "0");
        response.put("CTLIDENTRG_MARCAS_AUTOMOTOR_A2", "0");
        response.put("CTLIDENTRG_MODELOS_AUTOMOTOR_2A", "0");
        response.put("CTLIDENTRG_TIPOS_AUTOMOTOR_A2", "0");
        response.put("_NROAUTOMOTAUX", "0");
        response.put("CTLANOAUT3", "0");
        response.put("CTLPADRONAUT4", "0");
        response.put("_DEPAUT3", "");
        response.put("CTLPLACAMUNICIPALAUT3", "");
        response.put("_PADRONAUX3", "0");
        response.put("CTLIDENTRG_MARCAS_AUTOMOTOR_A3", "0");
        response.put("CTLIDENTRG_MODELOS_AUTOMOTOR_3A", "0");
        response.put("CTLIDENTRG_TIPOS_AUTOMOTOR_A3", "0");
        response.put("CTLIDENTRG_MARCAS_AUTOMOTOREDIT", "0");
        response.put("_DEPINM", "D");
        response.put("CTLLOCINM2", "C03");
        response.put("CTLPADRONINM2", "0");
        response.put("_CARTELINM", "0");
        response.put("_CARTELAUT", "0");
        response.put("CTLBLOCKINM2", "");
        response.put("CTLMANZANAINM", "");
        response.put("CTLNIVELINM2", "");
        response.put("CTLSOLARINM", "");
        response.put("CTLUNIDADINM1", "");
        response.put("CTLFRACCIONINM", "");
        response.put("CTLSJINM", "0");
        response.put("GRID2_ROW", "0");
        response.put("CTLPAISAERO", "CX");
        response.put("CTLORDAERO2", "0");
        response.put("CTLMATRICULAAERO", "");
        response.put("CTLMARCAAERO", "");
        response.put("CTLMODELOAERO", "");
        response.put("CTLSERIEAERO", "");
        response.put("GRID3_ROW", "0");
        response.put("CTLREGTES2", "");
        response.put("CTLDEPTES2", "");
        response.put("CTLANOTES2", "0");
        response.put("CTLNROTES2", "0");
        response.put("CTLNUMEROTES2", "0");
        response.put("_NROAUX", "0");
        response.put("CTLFOLIOTES2", "0");
        response.put("CTLLIBROTES2", "0");
        response.put("CTLSEDESOL", "X");
        response.put("BUTTON1", "Enviar");
        response.put("Solic", "eJx9V99v2zYQfl7/iqHvnewGHZDBM+A5SechvRhLUgx5MRSLdQVIYiDZQf/8kTzyfpBuAj/cd9+RvNPxU3SLe9u1+/Z4an790XfD9Od7cphpe3p2tp3eL9/9stjbpj3Yddcut49/3W7Wd4uKXY6H0bqly9miipbz3ey/b4ZDcLu/D+H3MJv9EX6LimkX+9C+hGXrRZVM3OG69/bH2fzyw3zmfmEdOjHgcXrrBGRd5PV0rJuw79dFxcAx61DH47T8/dOn+eWlW0eemAJF52nIbVzdN+3U7uspPoWEkPvnNLaNYBkjvxn6k3nuTOIZI786HW1vj3akCOlxMXfPvtQq1PrDDmasE7w3jUn23djEPkUr7m5GO9SvvDdh5B/MdGx7O7Q2RUiPi1ELKoWyPKsMq0Irhbb8CF0V6Wb+dmteTXexOZreE6EmF7ich5q8FbyrFzP3YAUP108rWLmDoyfRHx2oEIHtY/Dn61tXX4SJ05EXjNYbtq++Cr+ZrszotwB3nQgE7qbuut1q8PfDP0sJ09rdlZkiLRCxf9eSjSiwW9vQsWRjdle7bVyUzOD/8oX8ycTnsyJ/MtMJIjuBiOXsBArsZjhiRve+0UeRXUCvdecdm0hGGPh/TTs0cSXZWNe+j/VGK3lFlgIRy1kKhDWMZmgQ+6fICNealxcP/nMLo4l9sb0Z961NPRcw8P5d8bxz7higcNpB9t3qnD0Wfbc6ZzCH3ed6qId9W3cm5X7Gi9EHcRCDxPExDPCuuBu13IDrQbCSr/3WmsbLj2why91TfNmgLaW5e6qUFglH+Umc8xcSBxUSCjpkLomPPEJwmJx2ZBrEEIUzHXIEYaVFTgx1h/EMpAyRYyCliByDTI5IK5xJkiN0okmKlKiQH/mS6riaIDcJVSIKZ6rjiOyJkdZ4W9QYt5N1RT4pJRmoO2jzfFhDHKHzKfXDV/CgdpdQy4h4vXW68hB6SkBd98AxUFc/cReCQxkEJplCEMGfzEwaEG8wwUIokAsFCqFAJhQohAKZUCATCsT7qbJIQgGpGtCqAaka0KoBqRooVAOZaqBQDWSqgUw1gReokBAHkENLKgQIJPUFUSqaUdkrnEsNMqlBITWsT2KlPNyAQKFCiNoRjlKVISjz5DKFTKZQyBQymcJPZYryOOfP1AtavZCrF7R6A7+ofvIt6h7jDX/0b9UnfqVh9vlcZbj4T8sDGL6C4sAV3y7JlkOXp94cuzA+Tltx1zRbqcU8ctERYax644TEy8ErHpGGK3EiRxSnCkrNWFQ442zOogjpyWYtipGeYt6iKO2jmStWwlOXX+A+h5SD57AYjvMXfR7JGml84pOFp5jIKEr7RHPDLWagukLSbJhVTx5f/dqjewFZL6DoBRS9gKIXUPQCzvQCzvQCRC8CT7ZuC5YqMfcBRB+g6AMUfYAzfYAzfXBx4n2xfPc/PkdOyQ==");
        response.put("Solicfis", "eJx1lkFv2zAMhc/rrxj6B7Iu1yxAsKxbgJULNmCHXgw31gIDthXEabGfP0UixUeuvfHxqSJjfQ/o6lcc+kN/ee6a7+ElDMvdJYzv/47DNH+6rV6Y989PqY7z7frm3erHubvv5/WH1YKr1Nucwl0qF1x/lJrieAc19pdSf95Jtf1de2HehrOo+3YYms0UeSzKcrbZhplNUOx9a9Fjlbx97HTEdtvs+ZSUqfvwULtSXn/gpnalLNfBGqDY0zVAJW83XXSNLF7aQfTP0E9d3fEwQgnDQLGnw0BdFzmHqcuqXBNOp/rJ4xjOhz6K/jJf2qcmdeEAfueIM68KvnPEmRSOzdd2aqdD3w6hzqYj3KaiOHqXiut7pCdbcNH/6UOXX0bqymbzqHSmWvlsHpFQVsyoKustVWVSuc6sSl9oZQ2AllVswzBbDhhtuFW/amBXlinIlrMqlODiqFCKi6PCkFxMow3N6uNyQjQvB0xzR6iW7TPXKsxwow3d6psvUwmXCwvj8lRKOXeQcz1kXyfaHZRv9XGH/4kXnI7mXpTIfXXxUoGW8ntVAchmRwXgK86yOgXl3JeyQp27Uhq8iVms0sFOHnZysJODnRzs5GAnAzsxbTBdYCckn5B8QvIJyScknxz55MgnRz458smQn11QLgZq1wbGItugNCPEyGPfbGy0jQu5uJCLS/lFqCE95Y+rcEkiTgE0fLLyEdexUSMXNXJRIxc1eiNqBfbX+iaBZBNINoFkE5jc1eK1f5bWN/8A9Wzh1w==");
        response.put("Solicjur", "eJxllc1uwjAQhM/lKSpeID30mCJRqARV2UblUnGJIDGSpfyg/KA+fjfx2rtrbjM7xlZ2Pon02Fa2sMNY5l/mbqrX/WDq57+6avq3ZchMn40X1G2/XC2e0u+u/By71UuakMIZtPWlM2iSyf2MhZfvt54Ok8JZ1pb51vi5cJTtzjIjN2WdaUrnADN2mG3N7TbJ3zTx0t1mOn/e6+n0Ns/oCS9xejiEqZc4Xa/D1Euc7psh3By0n9/PVViFsU1J54LG+aatTVfYliJpXSo2JBxlvCHhMPvoBxzQnWwwObbFxt5tle0o1QN3QrwpHGX8pnDT1nCvCQl7taac9+d14CU/MTGoJTP5SVBDxtHifhS0Ysdlyit+OA9eMUQPES3kPCQ+dHC4q9gwKy5hw7y4hI1ghi4XqPgNECBkBRQ8UV+vvGKDc/n1AQm6T0HAM/WG8ooFzuUbVCNMWdDcKIhGIWoUokYhahSiRiFq1OXSi4LnkI0s29/rnWgeZPMgmwfZPMjmQTYPqvk5Ei7CgOMwkFi4nbKLGAGqXAw0MxAxAxEzEDEDmpk5lvaBICAg1EgzBRFTEDEFEVOYT/7xb2q1+AdtqSjO");
        response.put("Solicaut", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ4yQ/PL3VKnhavHitx1Z8V0KSv4D2d0bt1qxwdfPSGtHaPFd3rNCV8DUmbh8Cyz4dLxWiKnY8yWrbF4i5u5SwTNcm58Q5Te6oCt/JaguVMRVOVlHrTppSkeBRDQPYQ5Xlseyxw77RmggAGKM0BxBmibwTy1Ze+QQXtk0A4ZtEMGrckg80sNDEtqjgp378o/zPfp2/Td++aNlyZbf5/Z4R8h584t");
        response.put("Solicautpadronanterior", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ4yQ/PL3VKnhavHitx1Z8V0KSv4D2d0bt1qxwdfPSGtHaPFd3rNCV8DUmbh8Cyz4dLxWiKnY8yWrbF4i5u5SwTNcm58Q5Te6oCt/JaguVMRVOVlHrTppSkeBRDQPYQ5Xlseyxw77RmggAGKM0BxBmibwTy1Ze+QQXtk0A4ZtEMGrckg80sNDEtqjgp378o/zPfp2/Td++aNlyZbf5/Z4R8h584t");
        response.put("Solicautpadronanterior2", "eJyFlE1ugzAUhNfNKapcgHZPkVDLIlJwULvLxnKxiywZHjKm6vFrwH8hxOzGM37Gni9K+gWC11yNFJ/ZLxOvJ8Xa579WdMPb0WVsqMZvrWE4Zoen9CJpPqrsJU2M0l5JlPGM0t47UN7AB+uxNpLJOUNtZUWohM7M+IVO8g6MbZT2OGWdkg1WvIcBk1FBCwokJtO2x+H0HUFqUo6dfklPhP36aRr5bHAJlAk9lN+eGIvDaSJr8nj4Pl3enXeKSQ4yQ/PL3VKnhavHitx1Z8V0KSv4D2d0bt1qxwdfPSGtHaPFd3rNCV8DUmbh8Cyz4dLxWiKnY8yWrbF4i5u5SwTNcm58Q5Te6oCt/JaguVMRVOVlHrTppSkeBRDQPYQ5Xlseyxw77RmggAGKM0BxBmibwTy1Ze+QQXtk0A4ZtEMGrckg80sNDEtqjgp378o/zPfp2/Td++aNlyZbf5/Z4R8h584t");
        response.put("Solicinm", "eJxlks1qwzAMx8/LU4y+QMp6TXNYx6CjzQpll1yCZ3vDLLFH0pbSp58iy7bc3f4fkqMfpDq63khzOqtupy+6f9qe9PB4HXo7rRex09Ph/AnaTYu6eKjeR7W1Q72sSlKQbZwy3+5F/3YQlHOyc3LuNstVVZKG9CDU6CytJwPNc+/kT1huDBwTzIc1Sqjgjm+07QUkrbMitHthbyJZQBBjMK+jkNLgB/2sUzoI82W0ml+NOoJ2bUIFfQ/btQyXTATzu9wyUhoOrGQjLXnE9O8EmZhpJlGHJeImy8jzc5v52aj5bdgwxw/Fjrnsaiy5jQxNYmg4A+bJ5DxYZp7j+SeTu2PFOg8KnP/3z9fFHwXr9Y4=");
        response.put("Solicaero", "eJxtkEEKwjAQRdf2FNILxI27GBBXgtJCN9JNiMkIA6mBpBGPb1qbMaK7//5fzGN45yxqHKORJ3iA3R5HGNbPwd7DrqYNQhuvKbtQi2rFG2/24J3YcJZjaluFYc6HC2cEaTir0aOOVk0FezdeF+QMWEfYgUf4GnPAG4KZrlL+yMi+0ElQCMme/WhQt4hkJpWlIJmZOfv3LlG9AG6RaXs=");
        response.put("Solictes", "eJxtkssKwkAMRdf2K6Q/MK5cjQVBBKFGUVfdFGqjDPQhfYifb+pMMxlwd3LvNJxA9bWtzN0MY5mn+MZqfRiwXn7qquk3MXfYn8eCuO3jJFroU1fesE9WWjmi7IJPIjXhDl8zbpvWvXREGXRz5oiy1BScMlO+J4M5Z552jDX6NTxQc2xLVA7Mw2A5vWBm+Tzz+sR8QJ6JE9xg1e0XzHyIzZnFMbYRkzjIdmKSR7mVYvR+MFXMXvWXM3trENYgrEFYQ2ANgTUE1hBYQ2gNoTW1Wv37vZLoC/YgyUs=");
        response.put("nRC_Grid1", "0");
        response.put("nRC_Grid2", "0");
        response.put("nRC_Grid3", "0");
        response.put("nRC_Grid4", "0");
        response.put("sCallerURL", "http://www.dgr.gub.uy/sr/principal.jsf");
        
        return response;
    }

    private Map<String, String> getRequestHeaderBefore() {
        Map<String, String> header = new HashMap<>();

        header.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
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

        header.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
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
        header.put("User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/75.0.3770.90 Chrome/75.0.3770.90 Safari/537.36");

        return header;
    }

    private Map<String, String> getRequestHeaderFour(String cookie, String userAgent) {
        Map<java.lang.String, java.lang.String> response = new HashMap<>();

        response.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
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

    public boolean esCedulaValida(String ni) {
        if (ni == null || ni.equalsIgnoreCase("") || ni.trim().length() <= 5 || ni.trim().length() > 8) {
            return false;
        } else {
            int lengthCedula = ni.length();
            String digitoVerificador = ni.substring(lengthCedula - 1, lengthCedula);
            String cedulaSinDigito = ni.substring(0, lengthCedula - 1);
            String cedulaAuxiliar = "0000000" + cedulaSinDigito;
            String cedulaCompleta = cedulaAuxiliar.substring(cedulaAuxiliar.length() - 7, cedulaAuxiliar.length());
            String millones = cedulaCompleta.substring(0, 1);
            String centenasMiles = cedulaCompleta.substring(1, 2);
            String decenasMiles = cedulaCompleta.substring(2, 3);
            String miles = cedulaCompleta.substring(3, 4);
            String centenas = cedulaCompleta.substring(4, 5);
            String decenas = cedulaCompleta.substring(5, 6);
            String unidades = cedulaCompleta.substring(6, 7);
            try {
                int digitoMillones = (Integer.parseInt(millones)) * 2;
                int digitoCentenasMiles = (Integer.parseInt(centenasMiles)) * 9;
                int digitoDecenasMiles = (Integer.parseInt(decenasMiles)) * 8;
                int digitoMiles = (Integer.parseInt(miles)) * 7;
                int digitoCentenas = (Integer.parseInt(centenas)) * 6;
                int digitoDecenas = (Integer.parseInt(decenas)) * 3;
                int digitoUnidades = (Integer.parseInt(unidades)) * 4;
                int total = digitoMillones + digitoCentenasMiles + digitoDecenasMiles + digitoMiles + digitoCentenas
                        + digitoDecenas + digitoUnidades;
                String digitoMilesTotal = total + "";
                int digitoMilesTotalAux;
                if (total > 99) {
                    digitoMilesTotalAux = Integer.parseInt(digitoMilesTotal.substring(2));
                } else if (total < 10) {
                    digitoMilesTotalAux = total;
                } else {
                    digitoMilesTotalAux = Integer.parseInt(digitoMilesTotal.substring(1));
                }
                int digitoTemp = (10 - digitoMilesTotalAux);
                if (digitoTemp == Integer.parseInt(digitoVerificador)
                        || (digitoTemp - 10) == Integer.parseInt(digitoVerificador)) {
                    return true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Cedula con caracteres invalidos = " + ni);
                return false;
            }
        }

        return false;
    }

}
