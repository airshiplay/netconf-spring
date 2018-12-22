package com.airlenet.netconf.rest;

import com.airlenet.netconf.common.PlayNetconfDevice;
import com.airlenet.netconf.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/device")
public class DeviceRest {
    @Autowired
    DeviceService deviceService;


    @RequestMapping("list")
    public Object list() {
        return deviceService.listDevice().stream().flatMap(new Function<PlayNetconfDevice, Stream<DeviceParam>>() {
            @Override
            public Stream<DeviceParam> apply(PlayNetconfDevice playNetconfDevice) {
                return Stream.of(new DeviceParam(playNetconfDevice.getId(), playNetconfDevice.getMgmt_ip(), playNetconfDevice.getMgmt_port(), playNetconfDevice.getRemoteUser(), playNetconfDevice.getPassword()));
            }
        }).distinct()
                .collect(Collectors.toList()).toArray();
    }

    @RequestMapping("add")
    public Object add(@RequestBody DeviceParam param) {
        PlayNetconfDevice playNetconfDevice = deviceService.addDevice(param.getIp(), param.getPort(), param.getUser(), param.getPass());
        return new DeviceParam(playNetconfDevice.getId(), playNetconfDevice.getMgmt_ip(), playNetconfDevice.getMgmt_port(), playNetconfDevice.getRemoteUser(), playNetconfDevice.getPassword());
    }
}
