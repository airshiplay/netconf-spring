package com.airlenet.netconf.datasource.stat;

import com.airlenet.netconf.datasource.support.json.JSONUtils;
import com.airlenet.netconf.datasource.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NetconfStatService implements NetconfStatServiceMBean {
    private static Logger logger = LoggerFactory.getLogger(NetconfStatService.class);
    public final static String MBEAN_NAME = "com.airlenet.netconf:type=NetconfStatService";

    public final static int RESULT_CODE_SUCCESS = 1;
    public final static int RESULT_CODE_ERROR = -1;

    private final static NetconfStatService instance = new NetconfStatService();
    private static NetconfStatManagerFacade statManagerFacade = NetconfStatManagerFacade.getInstance();

    public static NetconfStatService getInstance() {
        return instance;
    }

    public String service(String url) {
        Map<String, String> parameters = getParameters(url);

        if (url.equals("/basic.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, statManagerFacade.returnJSONBasicStat());
        }
        if (url.equals("/datasource.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, statManagerFacade.getDataSourceStatDataList());
        }
        if (url.equals("/activeConnectionStackTrace.json")) {
            return returnJSONResult(RESULT_CODE_SUCCESS, statManagerFacade.getActiveConnStackTraceList());
        }
        if (url.startsWith("/datasource-")) {
            Integer id = StringUtils.subStringToInteger(url, "datasource-", ".");
            Object result = statManagerFacade.getDataSourceStatData(id);
            return returnJSONResult(result == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS, result);
        }
        if (url.startsWith("/connectionInfo-") && url.endsWith(".json")) {
            Integer id = StringUtils.subStringToInteger(url, "connectionInfo-", ".");
            List<?> connectionInfoList = statManagerFacade.getPoolingConnectionInfoByDataSourceId(id);
            return returnJSONResult(connectionInfoList == null ? RESULT_CODE_ERROR : RESULT_CODE_SUCCESS,
                    connectionInfoList);
        }
        if (url.startsWith("/activeConnectionStackTrace-") && url.endsWith(".json")) {
            Integer id = StringUtils.subStringToInteger(url, "activeConnectionStackTrace-", ".");
            return returnJSONActiveConnectionStackTrace(id);
        }

        return returnJSONResult(RESULT_CODE_ERROR, "Do not support this request, please contact with administrator.");
    }

    private String returnJSONActiveConnectionStackTrace(Integer id) {
        List<String> result = statManagerFacade.getActiveConnectionStackTraceByDataSourceId(id);
        if (result == null) {
            return returnJSONResult(RESULT_CODE_ERROR, "require set removeAbandoned=true");
        }
        return returnJSONResult(RESULT_CODE_SUCCESS, result);
    }

    public static String returnJSONResult(int resultCode, Object content) {
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("ResultCode", resultCode);
        dataMap.put("Content", content);
        return JSONUtils.toJSONString(dataMap);
    }

    public static Map<String, String> getParameters(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            return Collections.<String, String>emptyMap();
        }

        String parametersStr = StringUtils.subString(url, "?", null);
        if (parametersStr == null || parametersStr.length() == 0) {
            return Collections.<String, String>emptyMap();
        }

        String[] parametersArray = parametersStr.split("&");
        Map<String, String> parameters = new LinkedHashMap<String, String>();

        for (String parameterStr : parametersArray) {
            int index = parameterStr.indexOf("=");
            if (index <= 0) {
                continue;
            }

            String name = parameterStr.substring(0, index);
            String value = parameterStr.substring(index + 1);
            parameters.put(name, value);
        }
        return parameters;
    }

    public static void registerMBean() {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        try {

            ObjectName objectName = new ObjectName(MBEAN_NAME);
            if (!mbeanServer.isRegistered(objectName)) {
                mbeanServer.registerMBean(instance, objectName);
            }
        } catch (JMException ex) {
            logger.error("register mbean error", ex);
        }
    }

    public static void unregisterMBean() {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

        try {
            mbeanServer.unregisterMBean(new ObjectName(MBEAN_NAME));
        } catch (JMException ex) {
            logger.error("unregister mbean error", ex);
        }
    }
}
