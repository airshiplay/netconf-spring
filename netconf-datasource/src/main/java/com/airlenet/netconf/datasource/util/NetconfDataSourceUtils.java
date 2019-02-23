package com.airlenet.netconf.datasource.util;

import com.airlenet.netconf.datasource.NetconfDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ObjectName;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class NetconfDataSourceUtils {
    private final static Logger LOG = LoggerFactory.getLogger(NetconfDataSourceUtils.class);

    public static String getUrl(Object netconfDataSource) {
        if (netconfDataSource.getClass() == NetconfDataSource.class) {
            return ((NetconfDataSource) netconfDataSource).getUrl();
        }

        try {
            Method method = netconfDataSource.getClass().getMethod("getUrl");
            Object obj = method.invoke(netconfDataSource);
            return (String) obj;
        } catch (Exception e) {
            LOG.error("getUrl error", e);
            return null;
        }
    }

    public static long getID(Object netconfDataSource) {
        if (netconfDataSource.getClass() == NetconfDataSource.class) {
            return ((NetconfDataSource) netconfDataSource).getID();
        }

        try {
            Method method = netconfDataSource.getClass().getMethod("getID");
            Object obj = method.invoke(netconfDataSource);
            return (Long) obj;
        } catch (Exception e) {
            LOG.error("getID error", e);
            return -1;
        }
    }

    public static String getName(Object netconfDataSource) {
        if (netconfDataSource.getClass() == NetconfDataSource.class) {
            return ((NetconfDataSource) netconfDataSource).getName();
        }

        try {
            Method method = netconfDataSource.getClass().getMethod("getName");
            Object obj = method.invoke(netconfDataSource);
            return (String) obj;
        } catch (Exception e) {
            LOG.error("getUrl error", e);
            return null;
        }
    }

    public static ObjectName getObjectName(Object netconfDataSource) {
        if (netconfDataSource.getClass() == NetconfDataSource.class) {
            return ((NetconfDataSource) netconfDataSource).getObjectName();
        }

        try {
            Method method = netconfDataSource.getClass().getMethod("getObjectName");
            Object obj = method.invoke(netconfDataSource);
            return (ObjectName) obj;
        } catch (Exception e) {
            LOG.error("getObjectName error", e);
            return null;
        }
    }
    public static Map<String, Object> getStatData(Object netconfDataSource) {
        if (netconfDataSource.getClass() == NetconfDataSource.class) {
            return ((NetconfDataSource) netconfDataSource).getStatData();
        }

        try {
            Method method = netconfDataSource.getClass().getMethod("getStatData");
            Object obj = method.invoke(netconfDataSource);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getStatData error", e);
            return null;
        }
    }
}
