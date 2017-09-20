import com.airlenet.neconf.server.NetconfServer;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.subsystem.sftp.SftpClient;
import org.apache.sshd.common.SyspropsMapWrapper;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.channel.ChannelListener;
import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.common.util.buffer.ByteArrayBuffer;
import org.apache.sshd.common.util.io.NoCloseInputStream;
import org.apache.sshd.common.util.io.NoCloseOutputStream;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author airlenet
 * @version 2017-09-09
 */
public class Demo {
    static String hello="<hello xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" +
        "  <capabilities>\n" +
        "    <capability>urn:ietf:params:netconf:base:1.0</capability>\n" +
        "  </capabilities>\n" +
        "</hello>";
    static Logger logger = LoggerFactory.getLogger(NetconfServer.class);
    public static void main(String args[]) throws IOException {

        SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.start();
        ClientSession clientSession = sshClient.connect("admin", "172.16.129.181", 2022).verify(10*1000L).getSession();

       clientSession.addPasswordIdentity("admin");


        final ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
        clientSession.addChannelListener(new ChannelListener() {
            @Override
            public void channelInitialized(Channel channel) {

            }

            @Override
            public void channelOpenSuccess(Channel channel) {

                channel.getSession().getSessionId();
                try {
                    ByteArrayBuffer buffer = byteArrayBuffer;
                    byteArrayBuffer.putString(hello);
                    clientSession.writePacket(byteArrayBuffer).verify(10*1000L);
//                    buffer.putString(hello);
//                    Buffer request = channel.getSession().request("admin", buffer, 10, TimeUnit.MINUTES);
//                    request.getString();
//                    buffer.getString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void channelOpenFailure(Channel channel, Throwable reason) {

            }

            @Override
            public void channelStateChanged(Channel channel, String hint) {
                channel.getId();
            }

            @Override
            public void channelClosed(Channel channel, Throwable reason) {

            }
        });
        boolean auth=  clientSession.auth().verify(10*1000L).isSuccess();



        ChannelShell shellChannel = clientSession.createShellChannel();


        shellChannel.addChannelListener(new ChannelListener() {
            @Override
            public void channelInitialized(Channel channel) {

            }

            @Override
            public void channelOpenSuccess(Channel channel) {

            }

            @Override
            public void channelOpenFailure(Channel channel, Throwable reason) {

            }

            @Override
            public void channelStateChanged(Channel channel, String hint) {

            }

            @Override
            public void channelClosed(Channel channel, Throwable reason) {

            }
        });
//        shellChannel.setOut(System.out);
//        shellChannel.setIn(System.in);
//        shellChannel.setErr(System.err);
        byte[] bytes = new byte[4096];
        shellChannel.open().await();
        shellChannel.getOut().write(hello.getBytes());
        NoCloseOutputStream noCloseOutputStream = new NoCloseOutputStream(new ByteArrayOutputStream());
        shellChannel.setOut(noCloseOutputStream);
        shellChannel.setIn(new NoCloseInputStream(new ByteArrayInputStream(bytes)));
        noCloseOutputStream.write(hello.getBytes());

        int i = (shellChannel).getIn().read(bytes);

        String str=  new String(bytes);
        shellChannel.open().await();
        System.out.print("");
//        shellChannel.setIn(new NoCloseInputStream(new ByteArrayInputStream(hello.getBytes())));
//        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//        PrintStream out = new PrintStream( byteOut );
//        shellChannel.setOut(out);
//        shellChannel.setErr(new NoCloseOutputStream(System.err));
//        shellChannel.open().await();
//
//
//
//        shellChannel.waitFor(Arrays.asList( ClientChannelEvent.CLOSED), 0);
//        shellChannel.close();
    }
}
