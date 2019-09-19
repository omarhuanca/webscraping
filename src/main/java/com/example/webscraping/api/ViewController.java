package com.example.webscraping.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

import com.example.webscraping.service.TestService;
import com.example.webscraping.util.AElog;

@Controller
@Slf4j
public class ViewController {

    private static final Logger logger = LoggerFactory.getLogger(TestResource.class);

    @Autowired
    private TestService testService;

    @RequestMapping(value = {"index"}, method = RequestMethod.POST)
    public String postIndexPage(@RequestParam(value = "url") String url, Model model) throws IOException {

        this.requestLog("POST Request to Index.html with url : {}" + url);

        model.addAttribute("pageSummary", testService.getLinkOnPage(url));

        this.requestLog("Rendering Page");

        return "index";
    }

    private synchronized void requestLog(String string) {
        AElog.info1(logger, string);
    }
}
