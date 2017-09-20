package com.airlenet.netconf.rest;

import com.airlenet.netconf.service.SysService;
import com.airlenet.repo.domain.Result;
import com.airlenet.netconf.common.PlayNetconfDevice;
import com.airlenet.netconf.service.NetconfService;
import com.tailf.jnc.Element;
import com.tailf.jnc.JNCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    PlayNetconfDevice netconfDevice = new PlayNetconfDevice(1L, "admin", "admin", "172.16.129.181", 2022);
    @Autowired
    NetconfService netconfService;

    @RequestMapping(path = {"","/"})
    @ResponseBody
    public Object getVersion() {
        try {
            return sysService.getVersion(netconfDevice);
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.getRpcErrors());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @RequestMapping("/get")
    @ResponseBody
    public Object get() {
        try {
            return Result.success().setContent(netconfService.get(netconfDevice));
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.getRpcErrors());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @RequestMapping("/get/{xpath}")
    @ResponseBody
    public Object getXpath(@PathVariable("xpath") String xpath) {
        try {
            return Result.success().setContent(netconfService.get(netconfDevice, xpath));
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.getRpcErrors());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }
    @RequestMapping(value = "/getConfig",method = RequestMethod.GET)
    @ResponseBody
    public Object getConfig() {
        try {
            return netconfService.getConfig(netconfDevice);
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.getRpcErrors());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @RequestMapping(value = "/getConfig",method = RequestMethod.POST)
    @ResponseBody
    public Object getConfigPost(@RequestBody Element element) {
        try {
            return netconfService.getConfig(netconfDevice,element);
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.getRpcErrors());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @RequestMapping("/getConfig/{xpath}")
    @ResponseBody
    public Object getConfigXpath(@PathVariable("xpath") String xpath) {
        try {
            return netconfService.getConfig(netconfDevice, xpath);
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.getRpcErrors());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }
}
