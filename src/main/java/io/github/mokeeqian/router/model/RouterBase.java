package io.github.mokeeqian.router.model;

import io.github.mokeeqian.router.context.RouterContext;

public class RouterBase {

    private String tbIdx;

    public String getTbIdx() {
        return RouterContext.getTableKey();
    }
}