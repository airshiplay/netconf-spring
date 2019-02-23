package com.airlenet.netconf.datasource.support.jconsole;

import com.sun.tools.jconsole.JConsoleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.swing.*;

public class NetconfPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(NetconfPanel.class);

    protected Object doInBackground(JConsoleContext context) throws Exception {
        doInBackground(context.getMBeanServerConnection());

        return null;
    }

    protected void doInBackground(MBeanServerConnection conn) {
        if (conn == null) {
            logger.warn("MBeanServerConnection is null");
            return;
        }
        try {
//            this.conn = conn;
//            addOrRefreshTable(url);
        } catch (Exception e) {
            logger.warn("", e);
        }
    }
}
