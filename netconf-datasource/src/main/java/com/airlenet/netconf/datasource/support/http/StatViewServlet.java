package com.airlenet.netconf.datasource.support.http;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatViewServlet extends ResourceServlet {


    public StatViewServlet() {
        super("support/http/resources");
    }

    @Override
    protected String process(String url) {
        return null;
    }
}
