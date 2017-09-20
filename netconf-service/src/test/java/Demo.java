import com.airlenet.netconf.common.PlayNetconfDevice;
import com.airlenet.netconf.common.PlayNetconfSession;
import com.tailf.jnc.Element;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.NodeSet;

import java.io.IOException;

/**
 * @author airlenet
 * @date 17/9/7
 */
public class Demo {
    public static void main(String args[]) throws JNCException, IOException {
        PlayNetconfDevice netconfDevice = new PlayNetconfDevice(1L, "admin", "admin",   "172.16.129.194", 2022);

        PlayNetconfSession playNetconfSession = netconfDevice.getDefaultNetconfSession();

        String xml ="<interfaces xmlns=\"urn:ietf:params:xml:ns:yang:ietf-interfaces\"><interface><name>10gei-1/1/0</name><ipv4><address><ip-address>172.16.129.190</ip-address><ip-mask>24</ip-mask></address></ipv4></interface></interfaces>";

        playNetconfSession.editConfig(Element.readXml(xml));


    }
}
