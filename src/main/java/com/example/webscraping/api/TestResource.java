package com.example.webscraping.api;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.example.webscraping.service.TestService;
import com.example.webscraping.util.AElog;

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
    private TestService testService;

    @RequestMapping(value = { "/getlinks" }, method = { RequestMethod.GET, RequestMethod.POST})
    public Map<String, Long> getLinks(@RequestParam(value = "url") String url) throws IOException {
        this.requestLog("Restful Request to the WebScrapingService endpoint with parameter: url = {}" + url);
        return testService.getLinkOnPage(url);
    }

    @RequestMapping(value = {"/getimages"}, method = { RequestMethod.GET, RequestMethod.POST })
    public Map<String, Long> getImages(@RequestParam(value = "url") String url) throws IOException {
        this.requestLog("Restful Request to the WebScrapingService endpoint with parameter: url = {}" + url);
        return testService.getImageOnPage(url);
    }

    @RequestMapping(value = {"/getimports"}, method = { RequestMethod.GET, RequestMethod.POST })
    public Map<String, Long> getImports(@RequestParam(value = "url") String url) throws IOException {
        this.requestLog("Restful Request to the WebScrapingService endpoint with parameter: url = {}" + url);
        return testService.getImportOnPage(url);
    }

    private synchronized void requestLog(String string) {
        AElog.info1(logger, string);
    }
}
