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

    PlayNetconfDevice netconfDevice = new PlayNetconfDevice(1L, "admin", "admin", "172.19.102.122", 2022);
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
            return Result.exception().setContent(e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @RequestMapping("/get")
    @ResponseBody
    public Object get() {
        try {
            return Result.success().setContent(netconfService.get(netconfDevice).toXMLString());
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @PostMapping("get/xpath")
    @ResponseBody
    public Object getXpath(@RequestBody String xpath) {
        try {
            return (netconfService.get(netconfDevice, xpath).toXMLString());
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }
    @RequestMapping(value = "/getConfig",method = RequestMethod.GET)
    @ResponseBody
    public Object getConfig() {
        try {
            return netconfService.getConfig(netconfDevice).toXMLString();
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @RequestMapping(value = "/getConfig",method = RequestMethod.POST)
    @ResponseBody
    public Object getConfigPost(@RequestBody String content) {
        try {
            return netconfService.getConfig(netconfDevice, Element.readXml(content)).toXMLString();
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @RequestMapping("/getConfig/xpath")
    @ResponseBody
    public Object getConfigXpath(@RequestBody String xpath) {
        try {
            return netconfService.getConfig(netconfDevice, xpath).toXMLString();
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }

    @RequestMapping("/rpc")
    @ResponseBody
    public Object rpc(@RequestBody String content) {
        try {
            return  (netconfService.callRpc(netconfDevice, Element.readXml(content)).toXMLString());
        } catch (IOException e) {
            return Result.exception().message(e.getMessage());
        } catch (JNCException e) {
            return Result.exception().setContent(e.toString());
        } catch (Exception e) {
            return Result.exception().message(e.getMessage());
        }
    }
}
