package com.topjava.graduation.util.exception;

public class ErrorInfo {
    private String url;
    private ErrorType type;
    private String[] detail;

    public ErrorInfo() {
    }

    public ErrorInfo(CharSequence url, ErrorType type, String... detail) {
        this.url = url.toString();
        this.type = type;
        this.detail = detail;
    }

    public ErrorType getType() {
        return type;
    }

    public void setType(ErrorType type) {
        this.type = type;
    }


}