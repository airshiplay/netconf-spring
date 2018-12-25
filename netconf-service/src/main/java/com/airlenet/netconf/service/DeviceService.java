package com.airlenet.netconf.service;

import com.airlenet.data.jpa.EntityService;
import com.airlenet.netconf.common.PlayNetconfDevice;
import com.airlenet.netconf.model.DeviceModel;
import com.airlenet.netconf.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DeviceService extends EntityService<DeviceModel, Long> {

    private static final Map<Long, PlayNetconfDevice> deviceMap = new ConcurrentHashMap<>();

    @Autowired
    DeviceRepository deviceRepository;

    public PlayNetconfDevice addDevice(String ip, int port, String user, String pass) {
        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setIp(ip);
        deviceModel.setPort(port);
        deviceModel.setUser(user);
        deviceModel.setPass(pass);
        deviceModel = save(deviceModel);
        deviceMap.put(deviceModel.getId(), new PlayNetconfDevice(deviceModel.getId(), user, pass, ip, port));
        return deviceMap.get(deviceModel.getId());
    }

    public void delDevice(Long id) {
        delete(id);
        deviceMap.remove(id);
    }

    public List<PlayNetconfDevice> listDevice() {
        return findAll().stream().flatMap(new Function<DeviceModel, Stream<PlayNetconfDevice>>() {
            @Override
            public Stream<PlayNetconfDevice> apply(DeviceModel deviceModel) {
                PlayNetconfDevice netconfDevice = new PlayNetconfDevice(deviceModel.getId(), deviceModel.getUser(), deviceModel.getPass(), deviceModel.getIp(), deviceModel.getPort());
                return Stream.of(netconfDevice);
            }
        }).collect(Collectors.toList());
    }

    public PlayNetconfDevice getDevice(Long id) {
        DeviceModel deviceModel = findOne(id);
        if (deviceModel == null) {
            return null;
        }
        PlayNetconfDevice playNetconfDevice = deviceMap.get(id);
        if (playNetconfDevice == null) {
            playNetconfDevice = new PlayNetconfDevice(deviceModel.getId(), deviceModel.getUser(), deviceModel.getPass(),
                    deviceModel.getIp(), deviceModel.getPort());
            deviceMap.put(id, playNetconfDevice);
        }
        return playNetconfDevice;
    }
}
