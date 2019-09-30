package com.example.webscraping.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.example.webscraping.service.TestService;
import com.example.webscraping.util.AElog;
import com.example.webscraping.util.AEutil;

/**
 * TestResource
 *
 * @author Omar Huanca
 * @since 1.0
 */
@RestController
@Slf4j
public class TestResource {
    private static final Logger logger = LoggerFactory.getLogger(TestResource.class);

    @Autowired
    private AEutil util;

    @Autowired
    private TestService testService;

    @GetMapping("/getlinks")
    public Map<String, Long> getLinks(@Valid @RequestParam(value = "url") String url) throws IOException {
        System.out.println("Restful Request to the WebScrapingService endpoint with parameter: url = {}" + url);
        return testService.getLinkOnPage(url);
    }

    @GetMapping("/getimages")
    public Map<String, Long> getImages(@NotEmpty @RequestParam(value = "url") String url) throws IOException {
        System.out.println("Restful Request to the WebScrapingService endpoint with parameter: url = {}" + url);
        return testService.getImageOnPage(url);
    }

    @GetMapping("/getimports")
    public Map<String, Long> getImports(@NotNull @RequestParam(value = "url") String url) throws IOException {
        System.out.println("Restful Request to the WebScrapingService endpoint with parameter: url = {}" + url);
        return testService.getImportOnPage(url);
    }

    @PostMapping("/trylogin")
    public ResponseEntity<Object> tryLogin(@RequestParam(value = "url") String url,
            @RequestParam(value = "userAgent") String userAgent, @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password, HttpServletRequest request) {

        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        testService.tryLogin(url, userAgent, username, password);

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/gethtmldocument")
    public ResponseEntity<Object> getHtmlDocument(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        String baseUrl = "https://portal.dgr.gub.uy";
        String urlLogin = "https://www.dgr.gub.uy/sr/loginStart.jsf";
        Document object = testService.getHtmlDocument(baseUrl, urlLogin);

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(object, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/getauthvks")
    public ResponseEntity<Object> getAuthVk(HttpServletRequest request) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        String baseUrl = "https://m.vk.com";
        String email = "oma378501@gmail.com";
        String pass = "Internetes3!";
        Map<String, String> object = testService.getCookie(baseUrl, email, pass);

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(object, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/getcookies")
    public ResponseEntity<Object> getCookie(@RequestParam(value = "url") String url, @RequestParam(value = "email") String email,
                                            @RequestParam(value = "password") String password, HttpServletRequest request) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        Map<String, String> object = testService.getCookie(url, email, password);

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(object, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/getauthfiltros")
    public ResponseEntity<Object> getAuthEmporioDeLosFiltros(HttpServletRequest request) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        Document object = testService.getHtmlDynamicDocument();

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(object, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/getdocuments")
    public ResponseEntity<Object> getDocument(HttpServletRequest request) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        Document object = testService.getDocument();

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(object, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/getdocumentthrees")
    public ResponseEntity<Object> getDocumentThree(@RequestParam(value = "url") String url, HttpServletRequest request) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        Map<String, String> object = testService.getDocumentThree(url);

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(object, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/getdocumentfours")
    public ResponseEntity<Object> getDocumentFour(@RequestParam(value = "url") String url, HttpServletRequest request) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        Connection.Response object = testService.getDocumentFour(url);

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(object, responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/getdocumentfives")
    public ResponseEntity<Object> getDocumentFive(@RequestParam(value = "url") String url, @RequestParam(value = "host") String host,
                                                  HttpServletRequest request) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        Map<String, String> object = testService.getDocumentFive(url, host);

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(object, responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/getdocumentsixs")
    public ResponseEntity<Object> getDocumentSix(HttpServletRequest request) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        requestLog(request);

        Document object = testService.getDocumentSix();

        responseHeaders.set("Custom-Message", "HTTP/1.1 200 OK");
        return new ResponseEntity<Object>(object, responseHeaders, HttpStatus.OK);
    }

    private synchronized void requestLog(HttpServletRequest request) {
        AElog.info1(logger,
                util.getInetAddressPort() + " <= " + request.getRemoteHost() + " {method:" + request.getMethod()
                        + ", URI:" + request.getRequestURI() + ", query:" + request.getQueryString() + "}");
    }
}
