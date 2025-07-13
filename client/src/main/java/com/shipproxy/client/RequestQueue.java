package com.shipproxy.client;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestQueue {
    private static final BlockingQueue<Socket> queue = new LinkedBlockingQueue<>();

    public static void enqueue(Socket socket) {
        queue.offer(socket);
    }

    public static Socket dequeue() throws InterruptedException {
        return queue.take(); // blocks until available
    }
}
