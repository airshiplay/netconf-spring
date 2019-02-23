package com.airlenet.netconf.datasource.support.http;

import com.airlenet.netconf.datasource.stat.NetconfStatService;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatViewServlet extends ResourceServlet {
    private NetconfStatService statService             = NetconfStatService.getInstance();


    public StatViewServlet() {
        super("support/http/resources");
    }

    @Override
    protected String process(String url) {
        return statService.service(url);
    }
}
