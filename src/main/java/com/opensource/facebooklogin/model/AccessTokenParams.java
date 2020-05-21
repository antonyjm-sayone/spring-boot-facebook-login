package com.opensource.facebooklogin.model;

public class AccessTokenParams {
    private String grant_type;
    private String code;
    private String redirect_uri;
    private String state;


    public AccessTokenParams() {
    }

    public AccessTokenParams(String grant_type, String code, String redirect_uri, String state) {
        this.grant_type = grant_type;
        this.code = code;
        this.redirect_uri = redirect_uri;
        this.state = state;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
