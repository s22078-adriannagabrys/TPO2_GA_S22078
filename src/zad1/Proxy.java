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
    private PrintWriter outClient;
    private BufferedReader inClient;
    private String[] getMessage;
    int port;
    boolean isClient = false;
    boolean isCorrectCode = false;
    Map<String, String> addressMap = new HashMap<>();

    public static void main(String[] args) {
        Proxy proxy = new Proxy();
        proxy.port = Integer.parseInt(args[0]);
        try{
            proxy.serverSocket = new ServerSocket(proxy.port);
            while(true){
                proxy.startListen();
                if(proxy.isClient){
                    boolean existingCode = false;
                    for (Map.Entry<String, String> entry : proxy.addressMap.entrySet()){
                        if (entry.getKey().equals(proxy.getMessage[1])) {
                            existingCode = true;
                        }
                    }
                    if(!existingCode){
                        proxy.outClient = new PrintWriter(proxy.clientSocket.getOutputStream(), true);
                        proxy.outClient.println("wrong code");
                        proxy.outClient.close();
                    } else{
                        System.out.println("Correct code");
                        proxy.outClient = new PrintWriter(proxy.clientSocket.getOutputStream(), true);
                        proxy.outClient.println("okay");
                        proxy.outClient.close();
                        proxy.connectionToDictionary(proxy.addressMap.get(proxy.getMessage[1]).split(":")[0], Integer.parseInt(proxy.addressMap.get(proxy.getMessage[1]).split(":")[1]));
                    }
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
        dictionarySocket = new Socket(ip, port);
        outDictionary = new PrintWriter(dictionarySocket.getOutputStream(), true);
        outDictionary.println(getMessage[0] + "," + clientSocket.getRemoteSocketAddress().toString().split(":")[0].replace("/","") + "," + getMessage[2]);
        outDictionary.close();
        dictionarySocket.close();
    }

    public void startListen() throws IOException {
        clientSocket = serverSocket.accept();
        inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        getMessage = inClient.readLine().split(",");
        if(getMessage[0].equals("register")){
            isClient = false;
            addressMap.put(getMessage[1], clientSocket.getRemoteSocketAddress().toString().split(":")[0].replace("/","") + ":" + getMessage[2]);
        }else{
            isClient = true;

        }
    }

    public void stopListen() throws IOException {
        inClient.close();
        clientSocket.close();
    }
}
