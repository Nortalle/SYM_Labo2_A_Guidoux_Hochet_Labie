package com.heigvd.sym.symlabo2.Service;

import java.util.Map;

public class ServerResponse {

    private Integer code;

    private String response;

    private Map<String, String> headers;

    public ServerResponse(Integer code, String response, Map<String, String> headers) {
        this.code       = code;
        this.response   = response;
        this.headers    = headers;
    }

    public Integer getCode() {
        return code;
    }

    public String getResponse() {
        return response;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
