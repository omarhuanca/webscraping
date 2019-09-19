package com.example.webscraping.util.exception.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;

/**
 * SimpleErrorMessage is used to customize the body of the error response.
 *
 * @author Elio Arias
 * @since 1.0
 */
public class XErrorResponse {

    private Date date;

    private int code;

    private String message;

    private String description;

    private List<String> details;

    public XErrorResponse(int code, String message, String description) {
        this(code, message, description, new ArrayList<String>());
    }

    public XErrorResponse(HttpStatus status, String description) {
        this(status.value(), status.name(), description, new ArrayList<String>());
    }

    public XErrorResponse(HttpStatus status, String description, List<String> details) {
        this(status.value(), status.name(), description, details);
    }

    public XErrorResponse(HttpStatus status, String message, String description) {
        this(status.value(), message, description, new ArrayList<String>());
    }

    public XErrorResponse(HttpStatus status, String message, String description, List<String> details) {
        this(status.value(), message, description, details);
    }

    public XErrorResponse(int code, String message, String description, List<String> details) {
        this.date = new Date();
        this.code = code;
        this.message = message;
        this.description = description;
        this.details = details;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public String toJson() {
        return "{ \"date\": \"" + date + "\", \"code\": " + code + ", \"message\": \"Acceso denegado....\"}";
    }
}
