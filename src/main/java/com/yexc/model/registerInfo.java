package com.yexc.model;

/**
 * Created by Administrator on 2017/8/18.
 */
public class registerInfo {

    private String loginOutUrl;
    private String localSessionId;

    public registerInfo(String loginOutUrl, String localSessionId) {
        this.loginOutUrl = loginOutUrl;
        this.localSessionId = localSessionId;
    }

    public String getLoginOutUrl() {
        return loginOutUrl;
    }

    public void setLoginOutUrl(String loginOutUrl) {
        this.loginOutUrl = loginOutUrl;
    }

    public String getLocalSessionId() {

        return localSessionId;
    }

    public void setLocalSessionId(String localSessionId) {
        this.localSessionId = localSessionId;
    }

}
