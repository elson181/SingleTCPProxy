package com.shipproxy.server;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

public class ProxyServer {
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) {
        System.out.println("Offshore Proxy Server listening on port " + SERVER_PORT);
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            Socket client = serverSocket.accept(); // Single connection

            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();

            while (true) {
                byte[] sizeBytes = in.readNBytes(4);
                int requestLength = bytesToInt(sizeBytes);

                byte[] requestBytes = in.readNBytes(requestLength);
                String rawRequest = new String(requestBytes);

                System.out.println("[SERVER] Received request from client:");
                System.out.println(rawRequest);

                String url = extractUrl(rawRequest);
                System.out.println("Fetching: " + url);

                byte[] responseBytes = fetchHttpResponse(url);
                out.write(intToBytes(responseBytes.length));
                out.write(responseBytes);
                out.flush();

                System.out.println("[SERVER] Sending back HTTP response");

                String statusLine = new String(responseBytes).split("\r\n")[0];
                System.out.println("[SERVER] " + statusLine);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] fetchHttpResponse(String urlStr) {
        try {
            //use thread for delay, for multi request testing purpose.
//            Thread.sleep(3000);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            String statusLine = "HTTP/1.1 " + conn.getResponseCode() + " OK\r\n";
            output.write(statusLine.getBytes());

            for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
                if (entry.getKey() != null) {
                    String headerLine = entry.getKey() + ": " + String.join(",", entry.getValue()) + "\r\n";
                    output.write(headerLine.getBytes());
                }
            }

            output.write("\r\n".getBytes());

            try (InputStream in = conn.getInputStream()) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
            }

            return output.toByteArray();
        } catch (Exception e) {
            return ("HTTP/1.1 500 Internal Server Error\r\n\r\n" + e.getMessage()).getBytes();
        }
    }

    private static String extractUrl(String rawRequest) {
        try {
            String[] lines = rawRequest.split("\r\n");
            if (lines.length > 0) {
                String[] parts = lines[0].split(" ");
                if (parts.length > 1) {
                    return parts[1]; // Extract URL from GET /path HTTP/1.1
                }
            }
        } catch (Exception ignored) {}
        return "http://example.com";
    }

    private static byte[] intToBytes(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value
        };
    }

    private static int bytesToInt(byte[] bytes) {
        return (bytes[0] & 0xFF) << 24 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[2] & 0xFF) << 8 |
                (bytes[3] & 0xFF);
    }
}
