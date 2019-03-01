package com.synet;

public class MainServer {

    public static void main(String[] args) throws InterruptedException {
        TcpNetServer server = new TcpNetServer("",1234);
        server.CreateServer();
    }
}
