package com.airlenet.netconf.rest;

import com.airlenet.data.domain.Result;
import com.airlenet.netconf.service.DeviceService;
import com.airlenet.netconf.service.NetconfService;
import com.airlenet.netconf.service.SysService;
import com.tailf.jnc.Element;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.SessionClosedException;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by airshiplay on 17/8/31.
 *
 * @author airshiplay
 */
@Controller
@RequestMapping("/netconf")
public class NetconfRest {
    @Autowired
    SysService sysService;
    @Autowired
    NetconfService netconfService;

    @Autowired
    DeviceService deviceService;

    @GetMapping(path = {"/{id}/version"}, produces = {"application/xml;charset=UTF-8", "application/json;charset=UTF-8"})
    @ResponseBody
    public Result getVersion(@ApiParam(value = "Device Id") @PathVariable Long id) {
        try {
            Optional<Element> version = sysService.getVersion(deviceService.getDevice(id));
            if (version.isPresent())
                return Result.success().addProperties("content", version.get());
            return Result.failure();
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().addProperties("content", e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @PostMapping(path = "/{id}/subtree", consumes = {"application/xml"})
    @ResponseBody
    public Object get(@ApiParam(value = "Device Id") @PathVariable Long id, @RequestBody String content) {
        try {
            return Result.success()
                    .addProperties("content", netconfService.get(deviceService.getDevice(id), Element.readXml(content)));
        } catch (SessionClosedException e) {
            return Result.exception().message(e.getMessage());
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().addProperties("content", e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @PostMapping("/{id}/xpath")
    @ResponseBody
    public Result getXpath(@ApiParam(value = "Device Id") @PathVariable Long id, @RequestBody String xpath) {
        try {
            return Result.success()
                    .addProperties("content", netconfService.get(deviceService.getDevice(id), xpath));
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().addProperties("content", e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @GetMapping(value = "/{id}/getConfig")
    @ResponseBody
    public Result getConfig(@ApiParam(value = "Device Id") @PathVariable Long id) {
        try {
            return Result.success()
                    .addProperties("content", netconfService.getConfig(deviceService.getDevice(id)));
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().addProperties("content", e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @PostMapping(value = "/{id}/getConfig", produces = {"application/xml;charset=UTF-8", "application/json;charset=UTF-8"}, consumes = {"application/xml"})
    @ResponseBody
    public Result getConfigPost(@ApiParam(value = "Device Id") @PathVariable Long id, @RequestBody String content) {
        try {
            return Result.success()
                    .addProperties("content", netconfService.getConfig(deviceService.getDevice(id), Element.readXml(content)));
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().addProperties("content", e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @PostMapping(value = "/{id}/getConfig/xpath", produces = {"application/xml;charset=UTF-8", "application/json;charset=UTF-8"})
    @ResponseBody
    public Result getConfigXpath(@ApiParam(value = "Device Id") @PathVariable Long id, @RequestBody String xpath) {
        try {
            return Result.success()
                    .addProperties("content", netconfService.getConfig(deviceService.getDevice(id), xpath));
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().addProperties("content", e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @PostMapping(path = "/{id}/rpc", produces = {"application/xml;charset=UTF-8", "application/json;charset=UTF-8"}, consumes = {"application/xml;charset=UTF-8"})
    @ResponseBody
    public Result rpc(@ApiParam(value = "Device Id") @PathVariable Long id, @RequestBody String content) {
        try {
            return Result.success()
                    .addProperties("content", netconfService.callRpc(deviceService.getDevice(id), Element.readXml(content)));
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().addProperties("content", e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }
}
