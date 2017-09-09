import com.airlenet.netconf.common.PlayNetconfDevice;
import com.airlenet.netconf.common.PlayNetconfSession;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.NodeSet;

import java.io.IOException;

/**
 * @author lig
 * @date 17/9/7
 */
public class Demo {
    public static void main(String args[]) throws JNCException, IOException {
        PlayNetconfDevice netconfDevice = new PlayNetconfDevice(1L, "admin", "admin", "admin", "admin", "172.16.25.147", 2022);


        PlayNetconfSession playNetconfSession = netconfDevice.getDefaultNetconfSession();

        NodeSet config = playNetconfSession.getConfig("");
        System.out.print(config.toXMLString());


    }
}
