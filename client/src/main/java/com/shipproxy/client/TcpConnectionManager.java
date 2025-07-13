package com.shipproxy.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpConnectionManager {
    private final String serverHost;
    private final int serverPort;

    private Socket socket;
    private OutputStream out;
    private InputStream in;

    public TcpConnectionManager(String host, int port) throws Exception {
        this.serverHost = host;
        this.serverPort = port;
        connect();
    }

    private void connect() throws Exception {
        socket = new Socket(serverHost, serverPort);
        out = socket.getOutputStream();
        in = socket.getInputStream();
        System.out.println("Connected to offshore proxy at " + serverHost + ":" + serverPort);
    }

    public synchronized void sendRequest(byte[] requestBytes) throws Exception {
        out.write(intToBytes(requestBytes.length));
        out.write(requestBytes);
        out.flush();
    }

    public synchronized byte[] receiveResponse() throws Exception {
        byte[] sizeBytes = in.readNBytes(4);
        int responseLength = bytesToInt(sizeBytes);
        return in.readNBytes(responseLength);
    }

    private byte[] intToBytes(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value };
    }

    private int bytesToInt(byte[] bytes) {
        return (bytes[0] & 0xFF) << 24 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[2] & 0xFF) << 8 |
                (bytes[3] & 0xFF);
    }
}
