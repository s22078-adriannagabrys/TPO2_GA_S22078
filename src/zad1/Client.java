package zad1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends Application {
    private Socket proxySocket;
    private Socket dictionarySocket;
    private ServerSocket serverSocket;
    private PrintWriter outProxy;
    private BufferedReader inDictionary;
    private BufferedReader inProxy;
    static int port;

    public Client(){
    }

    public void connectionToProxy(String ip, int port) throws IOException {
        proxySocket = new Socket(ip, port);
        outProxy = new PrintWriter(proxySocket.getOutputStream(), true);
    }

    public void stopConnectionClient() throws IOException {
        outProxy.close();
        proxySocket.close();
    }

    public String startListen(int port) throws IOException {
        dictionarySocket = serverSocket.accept();
        inDictionary = new BufferedReader(new InputStreamReader(dictionarySocket.getInputStream()));
        String translatedWord = inDictionary.readLine();
        return translatedWord;
    }

    public String startListenProxy(int port) throws IOException {
        inProxy = new BufferedReader(new InputStreamReader(proxySocket.getInputStream()));
        String message = inProxy.readLine();
        return message;
    }

    public void stopListen() throws IOException {
        inDictionary.close();
        dictionarySocket.close();
    }

    public void stopListenProxy() throws IOException {
        inProxy.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vBox = new VBox();

        Label infLabel = new Label("Enter word you want to translate: ");
        Label inWordLabel = new Label("Word in Polish: ");
        Label languageCodeLabel = new Label("Language code: ");
        Label outWordLabel = new Label("Translated word: ");

        TextField inputWord = new TextField();
        inputWord.setPrefWidth(100);

        TextField inputLanguageCode = new TextField();
        inputLanguageCode.setPrefWidth(100);

        TextField outputWord = new TextField();
        outputWord.setPrefWidth(100);

        Button applyInputButton = new Button("Apply");
        serverSocket = new ServerSocket(port);

        applyInputButton.setOnMouseClicked(event -> {
            try {
                connectionToProxy("localhost", 1234);
                outProxy.println(inputWord.getText() + "," + inputLanguageCode.getText() + "," + port);
                //odpalanie serwera klienta
                if(startListenProxy(port).equals("wrong code")){
                    getErrorAlert("Wrong code");
                    stopListenProxy();
                }else {
                    outputWord.setText(startListen(port));
                    stopListen();
                    stopConnectionClient();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        vBox.getChildren().addAll(infLabel, inWordLabel, inputWord, languageCodeLabel, inputLanguageCode, applyInputButton, outWordLabel, outputWord);

        Scene scene = new Scene(vBox, 400, 400);
        primaryStage.setTitle("TPO2_s22078");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void getErrorAlert(String textToPrint) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setContentText(textToPrint);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        port = Integer.parseInt(args[0]);
        Application.launch(Client.class);
    }
}
