package zad1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class DictionaryServer {
    private Socket clientSocket;
    private Socket proxySocket;
    private ServerSocket serverSocket;
    private PrintWriter outClient;
    private PrintWriter outProxy;
    private BufferedReader inProxy;
    private String[] getMessage;
    int port = 1235;
    Map<String, String> translationMap = new HashMap<>();

    public static void main(String[] args) {
        DictionaryServer dictionaryServer = new DictionaryServer();
        dictionaryServer.translationMap.put("krzesło", "chair");
        dictionaryServer.translationMap.put("stół", "table");
        try{
            dictionaryServer.serverSocket = new ServerSocket(dictionaryServer.port);
            dictionaryServer.proxySocket = new Socket("localhost", 1234);
            dictionaryServer.outProxy = new PrintWriter(dictionaryServer.proxySocket.getOutputStream(), true);
            dictionaryServer.outProxy.println("register" + "," + args[0] + "," + dictionaryServer.port);
            dictionaryServer.outProxy.close();
            dictionaryServer.proxySocket.close();
            while(true){
                dictionaryServer.startListen();
                dictionaryServer.connectionToClient(dictionaryServer.getMessage[1], Integer.parseInt(dictionaryServer.getMessage[2]));
                dictionaryServer.stopListen();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public DictionaryServer(){}

    public void connectionToClient(String ip, int port) throws IOException {
        System.out.println("connectionToClient");
        clientSocket = new Socket(ip, port);
        outClient = new PrintWriter(clientSocket.getOutputStream(), true);
        outClient.println(translationMap.get(getMessage[0]));
        outClient.close();
        clientSocket.close();
    }

    public void startListen() throws IOException {
        System.out.println("startListenDict");
        proxySocket = serverSocket.accept();
        inProxy = new BufferedReader(new InputStreamReader(proxySocket.getInputStream()));
        getMessage = inProxy.readLine().split(",");
        System.out.println(getMessage[0] + getMessage[1] + getMessage[2]);
    }

    public void stopListen() throws IOException {
        System.out.println("stopListen");
        inProxy.close();
        proxySocket.close();
    }
}
