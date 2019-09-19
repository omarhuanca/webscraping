package com.example.webscraping.util.exception.response.custom;

import org.springframework.http.HttpStatus;

import com.example.webscraping.util.exception.response.XErrorResponse;

/**
 * CustomBeanException captures the exception to be customized. Is used to
 * customize the body of the error response
 *
 * @author Elio Arias
 * @since 1.0
 */
public class CustomRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;

    private Integer code;

    private String messageCRE;

    private String details;

    public CustomRuntimeException(HttpStatus httpStatus, Integer code, String messageCRE, String details) {
        super(messageCRE);
        this.httpStatus = httpStatus;
        this.code = code;
        this.messageCRE = messageCRE;
        this.details = details;
    }

    public XErrorResponse getXErrorResponse() {
        return new XErrorResponse(code, messageCRE, details);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessageCRE() {
        return messageCRE;
    }

    public void setMessageCRE(String messageCRE) {
        this.messageCRE = messageCRE;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
