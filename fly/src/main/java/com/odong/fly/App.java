package com.odong.fly;

public class App {
    public static void main(String[] args) {
        Server.get().init();
        Server.get().start();
    }

}
