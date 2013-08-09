package com.odong.relay;

public class App {
    public static void main(String[] args) {
        Server.get().init();
        Server.get().start();
    }

}
