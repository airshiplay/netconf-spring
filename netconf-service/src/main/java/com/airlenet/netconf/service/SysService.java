package com.airlenet.netconf.service;

import com.airlenet.netconf.common.PlayNetconfDevice;
import com.airlenet.netconf.common.PlayNetconfSession;
import com.tailf.jnc.Element;
import com.tailf.jnc.NodeSet;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by airlenet on 17/8/24.
 */
@Service
public class SysService {

    public Optional<Element> getVersion(PlayNetconfDevice playNetconfDevice) throws Exception {
        PlayNetconfSession netconfSession = playNetconfDevice.getDefaultNetconfSession();
        NodeSet nodeSet = netconfSession.get("sys-info");
        if (nodeSet != null && !nodeSet.isEmpty()) {
            return Optional.of(nodeSet.get(0));
        }
        return Optional.empty();
    }
}
