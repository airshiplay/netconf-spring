package com.airlenet.netconf.datasource.stat;

import com.airlenet.netconf.datasource.util.NetconfDataSourceUtils;
import com.airlenet.netconf.datasource.util.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class NetconfStatManagerFacade {
    private final static NetconfStatManagerFacade instance = new NetconfStatManagerFacade();
    private boolean resetEnable = true;
    private final AtomicLong resetCount = new AtomicLong();

    public static NetconfStatManagerFacade getInstance() {
        return instance;
    }

    public Map<String, Object> returnJSONBasicStat() {
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
//        dataMap.put("Version", VERSION.getVersionNumber());
//        dataMap.put("Drivers", getDriversData());
//        dataMap.put("ResetEnable", isResetEnable());
//        dataMap.put("ResetCount", getResetCount());
        dataMap.put("JavaVMName", System.getProperty("java.vm.name"));
        dataMap.put("JavaVersion", System.getProperty("java.version"));
        dataMap.put("JavaClassPath", System.getProperty("java.class.path"));
        dataMap.put("StartTime", Utils.getStartTime());
        return dataMap;
    }

    private Set<Object> getNetconfDataSourceInstances() {
        return NetconfDataSourceStatManager.getInstances().keySet();
    }

    public List<Map<String, Object>> getDataSourceStatDataList() {
        List<Map<String, Object>> datasourceList = new ArrayList<Map<String, Object>>();
        for (Object dataSource : getNetconfDataSourceInstances()) {
            datasourceList.add(dataSourceToMapData(dataSource));
        }
        return datasourceList;
    }

    private Map<String, Object> dataSourceToMapData(Object dataSource) {
        Map<String, Object> map = NetconfDataSourceUtils.getStatData(dataSource);
        return map;
    }
}
