package com.airlenet.netconf.datasource.support.http;

import com.airlenet.netconf.datasource.stat.NetconfStatService;

public class StatViewServlet extends ResourceServlet {
    private NetconfStatService statService = NetconfStatService.getInstance();


    public StatViewServlet() {
        super("support/http/resources");
    }

    @Override
    protected String process(String url) {
        return statService.service(url);
    }
}
