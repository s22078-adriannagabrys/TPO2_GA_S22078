package zad1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Proxy {
    private Socket dictionarySocket;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private PrintWriter outDictionary;
    private BufferedReader inClient;
    private String[] getMessage;
    int port = 1234;
    boolean isClient = false;
    Map<String, String> addressMap = new HashMap<>();

    public static void main(String[] args) {
        Proxy proxy = new Proxy();
        try{
            proxy.serverSocket = new ServerSocket(proxy.port);
            while(true){
                proxy.startListen();
                if(proxy.isClient){
                    proxy.connectionToDictionary(proxy.addressMap.get(proxy.getMessage[1]).split(":")[0], Integer.parseInt(proxy.addressMap.get(proxy.getMessage[1]).split(":")[1]));
                }
                proxy.stopListen();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Proxy(){
    }

    public void connectionToDictionary(String ip, int port) throws IOException {
        System.out.println("connectionToDictionary");
        dictionarySocket = new Socket(ip, port);
        outDictionary = new PrintWriter(dictionarySocket.getOutputStream(), true);
        outDictionary.println(getMessage[0] + "," + clientSocket.getRemoteSocketAddress().toString().split(":")[0].replace("/","") + "," + getMessage[2]);
        outDictionary.close();
        dictionarySocket.close();
    }

    public void startListen() throws IOException {
        System.out.println("startListen");
        clientSocket = serverSocket.accept();
        inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        getMessage = inClient.readLine().split(",");
        if(getMessage[0].equals("register")){
            isClient = false;
            addressMap.put(getMessage[1], clientSocket.getRemoteSocketAddress().toString().split(":")[0].replace("/","") + ":" + getMessage[2]);
        }else{
            isClient = true;
        }

        System.out.println(getMessage[0] + "," + getMessage[1] + "," + getMessage[2]);
    }

    public void stopListen() throws IOException {
        System.out.println("stopListen");
        inClient.close();
        clientSocket.close();
    }
}
