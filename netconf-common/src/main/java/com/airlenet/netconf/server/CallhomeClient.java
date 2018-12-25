package com.airlenet.netconf.server;

import ch.ethz.ssh2.KnownHosts;
import ch.ethz.ssh2.ServerHostKeyVerifier;
import com.airlenet.netconf.common.PlayNetconfDevice;
import com.airlenet.netconf.common.PlayNetconfListener;
import com.tailf.jnc.Device;
import com.tailf.jnc.DeviceUser;
import com.tailf.jnc.JNCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class CallhomeClient implements Runnable,PlayNetconfListener {
    private static Logger logger = LoggerFactory.getLogger(CallhomeClient.class);
    private Socket socket;

    public CallhomeClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            Device device = new Device("admin", new DeviceUser("admin", "admin", "admin"), socket);
            device.connect("admin", new ServerHostKeyVerifier() {
                @Override
                public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
                    String hexFingerprint = KnownHosts.createHexFingerprint(serverHostKeyAlgorithm, serverHostKey);
                    String bubblebabbleFingerprint = KnownHosts.createBubblebabbleFingerprint(serverHostKeyAlgorithm,
                            serverHostKey);
                    //默认的不支持，CN信息。需提供新算法提取证书CN信息
                    //
                    return false;
                }
            },10);


            PlayNetconfDevice playNetconfDevice = new PlayNetconfDevice(1L,"", device);
            playNetconfDevice.getDefaultNetconfSession();

            playNetconfDevice.createSubscription("alarm",this);
            //开始心跳，心跳失败，退出，并将设备从队列中移除。
            //开始订阅告警，接收告警信息。
        } catch (IOException e) {
            logger.error("",e);
        } catch (JNCException e) {
            logger.error(""+e.toString(),e);
        }
    }

    @Override
    public void receive(Long id, String stream, String ip, String msg) {

    }

    @Override
    public void send(Long id, String stream, String ip, String msg) {

    }
}
