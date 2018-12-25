package com.airlenet.netconf.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallhomeServer {

    ExecutorService executorService = Executors.newFixedThreadPool(3000);

    ServerSocket serverSocket;

    int port;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket accept = serverSocket.accept();
                executorService.execute(new CallhomeClient(accept));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
