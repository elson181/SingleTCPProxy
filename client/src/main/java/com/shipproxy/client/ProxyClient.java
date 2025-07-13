package com.shipproxy.client;


import java.io.*;
import java.net.*;
import java.time.Instant;

public class ProxyClient {
    private static final int LISTEN_PORT = 8080;
    private static final String SERVER_HOST = "offshore-proxy";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws Exception {
        TcpConnectionManager tcpManager = new TcpConnectionManager(SERVER_HOST, SERVER_PORT);

        // Worker thread to process requests one-by-one
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = RequestQueue.dequeue();
                    handleHttpRequest(clientSocket, tcpManager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // HTTP listener
        ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);
        System.out.println("Ship Proxy listening on port " + LISTEN_PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            RequestQueue.enqueue(clientSocket);
        }
    }

    private static void handleHttpRequest(Socket clientSocket, TcpConnectionManager tcpManager) {
        try (
                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream()
        ) {

            System.out.println("[" + Instant.now() + "] [CLIENT] ...");

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] temp = new byte[4096];
            int bytesRead;
            clientSocket.setSoTimeout(200); // prevent hanging

            while ((bytesRead = in.read(temp)) != -1) {
                buffer.write(temp, 0, bytesRead);
                if (bytesRead < temp.length) break; // crude end
            }

            byte[] requestBytes = buffer.toByteArray();

            System.out.println("[CLIENT] Received HTTP request from browser:");
            System.out.println(new String(requestBytes));

            tcpManager.sendRequest(requestBytes);

            byte[] responseBytes = tcpManager.receiveResponse();
            out.write(responseBytes);
            out.flush();

            System.out.println("[CLIENT] Sending response back to browser:");
            System.out.println(new String(responseBytes));

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
