package com.hamusuke.paint.client;

public class MainClient {
    public static void main(String[] args) {
        PaintClient client = new PaintClient();
        client.run();
        client.stop();
    }
}
