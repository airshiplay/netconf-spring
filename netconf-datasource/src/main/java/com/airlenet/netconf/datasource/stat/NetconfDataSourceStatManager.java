package com.airlenet.netconf.datasource.stat;

import com.airlenet.netconf.datasource.NetconfDataSource;
import com.airlenet.netconf.datasource.util.NetconfDataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NetconfDataSourceStatManager implements NetconfDataSourceStatManagerMBean{
    private static Logger logger = LoggerFactory.getLogger(NetconfDataSourceStatManager.class);
    private final static Lock staticLock = new ReentrantLock();

    public final static String SYS_PROP_INSTANCES = "netconf.dataSources";
    public final static String SYS_PROP_REGISTER_SYS_PROPERTY = "netconf.registerToSysProperty";
    private final static String MBEAN_NAME = "com.airlenet.netconf:type=NetconfDataSourceStat";


    private final static NetconfDataSourceStatManager instance = new NetconfDataSourceStatManager();


    // global instances
    private static volatile Map dataSources;

    public static NetconfDataSourceStatManager getInstance() {
        return instance;
    }

    public static void clear() {
        staticLock.lock();
        try {
            Map<Object, ObjectName> dataSources = getInstances();

            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            for (Object item : dataSources.entrySet()) {
                Map.Entry entry = (Map.Entry) item;
                ObjectName objectName = (ObjectName) entry.getValue();

                if (objectName == null) {
                    continue;
                }

                try {
                    mbeanServer.unregisterMBean(objectName);
                } catch (JMException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } finally {
            staticLock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<Object, ObjectName> getInstances() {
        Map<Object, ObjectName> tmp = dataSources;
        if (tmp == null) {
            staticLock.lock();
            try {
                if (isRegisterToSystemProperty()) {
                    dataSources = getInstances0();
                } else {
                    tmp = dataSources;
                    if (null == tmp) {
                        dataSources = tmp = Collections.synchronizedMap(new IdentityHashMap<Object, ObjectName>());
                    }
                }
            } finally {
                staticLock.unlock();
            }
        }

        return dataSources;
    }

    public static boolean isRegisterToSystemProperty() {
        String value = System.getProperty(SYS_PROP_REGISTER_SYS_PROPERTY);
        return "true".equals(value);
    }

    @SuppressWarnings("unchecked")
    static Map<Object, ObjectName> getInstances0() {
        Properties properties = System.getProperties();
        Map<Object, ObjectName> instances = (Map<Object, ObjectName>) properties.get(SYS_PROP_INSTANCES);

        if (instances == null) {
            synchronized (properties) {
                instances = (IdentityHashMap<Object, ObjectName>) properties.get(SYS_PROP_INSTANCES);

                if (instances == null) {
                    instances = Collections.synchronizedMap(new IdentityHashMap<Object, ObjectName>());
                    properties.put(SYS_PROP_INSTANCES, instances);
                }
            }
        }

        return instances;
    }

    public synchronized static ObjectName addDataSource(Object dataSource, String name) {
        final Map<Object, ObjectName> instances = getInstances();

        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        synchronized (instances) {
            if (instances.size() == 0) {
                try {
                    ObjectName objectName = new ObjectName(MBEAN_NAME);
                    if (!mbeanServer.isRegistered(objectName)) {
                        mbeanServer.registerMBean(instance, objectName);
                    }
                } catch (JMException ex) {
                    logger.error("register mbean error", ex);
                }

                NetconfStatService.registerMBean();
            }
        }

        ObjectName objectName = null;
        if (name != null) {
            try {
                objectName = new ObjectName("com.alibaba.druid:type=DruidDataSource,id=" + name);
                mbeanServer.registerMBean(dataSource, objectName);
            } catch (Throwable ex) {
                logger.error("register mbean error", ex);
                objectName = null;
            }
        }

        if (objectName == null) {
            try {
                int id = System.identityHashCode(dataSource);
                objectName = new ObjectName("com.alibaba.druid:type=DruidDataSource,id=" + id);
                mbeanServer.registerMBean(dataSource, objectName);
            } catch (Throwable ex) {
                logger.error("register mbean error", ex);
                objectName = null;
            }
        }

        instances.put(dataSource, objectName);
        return objectName;
    }

    public synchronized static void removeDataSource(Object dataSource) {
        Map<Object, ObjectName> instances = getInstances();

        ObjectName objectName = (ObjectName) instances.remove(dataSource);

        if (objectName == null) {
            objectName = NetconfDataSourceUtils.getObjectName(dataSource);
        }

        if (objectName == null) {
            logger.error("unregister mbean failed. url " + NetconfDataSourceUtils.getUrl(dataSource));
            return;
        }

        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

        try {
            mbeanServer.unregisterMBean(objectName);
        } catch (Throwable ex) {
            logger.error("unregister mbean error", ex);
        }

        if (instances.size() == 0) {
            try {
                mbeanServer.unregisterMBean(new ObjectName(MBEAN_NAME));
            } catch (Throwable ex) {
                logger.error("unregister mbean error", ex);
            }

            NetconfStatService.unregisterMBean();
        }
    }

    @SuppressWarnings("unchecked")
    public static Set<NetconfDataSource> getDruidDataSourceInstances() {
        getInstances();
        return dataSources.keySet();
    }


    public void logAndResetDataSource() {
        Map<Object, ObjectName> dataSources = getInstances();

        for (Object item : dataSources.keySet()) {
            try {
                Method method = item.getClass().getMethod("logStats");
                method.invoke(item);
            } catch (Exception e) {
                logger.error("resetStat error", e);
            }
        }

    }
}
