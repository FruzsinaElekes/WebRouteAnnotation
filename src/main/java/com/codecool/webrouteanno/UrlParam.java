package com.codecool.webrouteanno;

import java.util.List;

public class UrlParam {
    private final String[] params;
    private final String recomposed;

    public UrlParam(String recomposed, List<String> params) {
        this.recomposed = recomposed;
        this.params = new String[params.size()];
        params.toArray(this.params);
    }

    public String[] getParams() {
        return params;
    }

    public String getRecomposed() {
        return recomposed;
    }
}
