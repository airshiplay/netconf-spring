package com.airlenet.netconf.rest;

import com.airlenet.data.domain.Result;
import com.airlenet.netconf.common.PlayNetconfDevice;
import com.airlenet.netconf.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/device")
@Api(value = "Device Management Rest Api")
public class DeviceRest {
    @Autowired
    DeviceService deviceService;

    @ApiOperation(value = "Device List")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result list() {
        return Result.success().addProperties("content", deviceService.listDevice().stream().flatMap(new Function<PlayNetconfDevice, Stream<DeviceParam>>() {
            @Override
            public Stream<DeviceParam> apply(PlayNetconfDevice playNetconfDevice) {
                return Stream.of(new DeviceParam(playNetconfDevice.getId(), playNetconfDevice.getMgmt_ip(), playNetconfDevice.getMgmt_port(),
                        playNetconfDevice.getRemoteUser(), playNetconfDevice.getPassword()));
            }
        }).distinct()
                .collect(Collectors.toList()).toArray());
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody DeviceParam param) {
        PlayNetconfDevice playNetconfDevice = deviceService.addDevice(param.getIp(), param.getPort(), param.getUser(), param.getPass());
        return Result.success().addProperties("content", new DeviceParam(playNetconfDevice.getId(), playNetconfDevice.getMgmt_ip(), playNetconfDevice.getMgmt_port(),
                playNetconfDevice.getRemoteUser(), playNetconfDevice.getPassword()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result get(@PathVariable("id") Long id) {
        PlayNetconfDevice playNetconfDevice = deviceService.getDevice(id);
        return Result.success().addProperties("content", new DeviceParam(playNetconfDevice.getId(), playNetconfDevice.getMgmt_ip(), playNetconfDevice.getMgmt_port(),
                playNetconfDevice.getRemoteUser(), playNetconfDevice.getPassword()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result del(@PathVariable("id") Long id) {
        deviceService.delDevice(id);
        return Result.success();
    }
}
