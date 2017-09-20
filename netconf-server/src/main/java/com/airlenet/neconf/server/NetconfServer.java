package com.airlenet.neconf.server;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator;
import org.apache.sshd.server.auth.password.StaticPasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author airlenet
 * @version 2017-09-09
 */
public class NetconfServer {
  static  Logger logger = LoggerFactory.getLogger(NetconfServer.class);
    public static void main(String args[]) throws IOException {
//        SshServer sshServer = new SshServer();
//        sshServer.setPort(2022);
//        sshServer.setUserAuthFactoriesNameList("admin");
//        sshServer.setPasswordAuthenticator(new StaticPasswordAuthenticator(true));
//        sshServer.start();


        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(2022);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("hostkey.ser")));
        sshd.setPasswordAuthenticator(AcceptAllPasswordAuthenticator.INSTANCE);

//        sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }));
        sshd.setCommandFactory(new ScpCommandFactory());
        sshd.start();
        // read lines form input
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            if (buffer.readLine().equalsIgnoreCase("EXIT")) {
                break;
            }
        }

        logger.info("Exiting");
        System.exit(0);
        new SshClient().connect("admin","172.16.129.181",2022);
    }
}
