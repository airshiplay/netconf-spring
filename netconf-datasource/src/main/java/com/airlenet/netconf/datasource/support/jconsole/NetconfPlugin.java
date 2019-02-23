package com.airlenet.netconf.datasource.support.jconsole;

import com.sun.tools.jconsole.JConsolePlugin;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class NetconfPlugin extends JConsolePlugin {
    private final Map<String, JPanel> tabs = new LinkedHashMap<String, JPanel>();

    public NetconfPlugin() {
        tabs.put("Netconf-DataSource", new NetconfDataSourcePanel());
    }

    @Override
    public Map<String, JPanel> getTabs() {
        return tabs;
    }

    @Override
    public SwingWorker<?, ?> newSwingWorker() {
        SwingWorker<?, ?> worer = new SwingWorker<Object, Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                return NetconfPlugin.this.doInBackground();
            }
        };
        return worer;
    }

    protected Object doInBackground() throws Exception {
        for (JPanel panel : tabs.values()) {
            ((NetconfPanel) panel).doInBackground(this.getContext());
        }

        return null;
    }
}
