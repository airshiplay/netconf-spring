package com.airlenet.netconf.service;

import com.airlenet.netconf.common.PlayNetconfDevice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DeviceService {
    private static final Map<Long, PlayNetconfDevice> deviceMap = new ConcurrentHashMap<>();
    private AtomicLong idAtomic = new AtomicLong();

    public PlayNetconfDevice addDevice(String ip, int port, String user, String pass) {
        Long id = idAtomic.incrementAndGet();
        deviceMap.put(id, new PlayNetconfDevice(id, user, pass, ip, port));
        return deviceMap.get(id);
    }

    public void delDevice(Long id) {
        deviceMap.remove(id);
    }

    public List<PlayNetconfDevice> listDevice() {
        return new ArrayList<>(deviceMap.values());
    }
}
