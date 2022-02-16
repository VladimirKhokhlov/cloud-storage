package com.geekbrains.cloud.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private File currentDirectory;
    private File serverDirectory;
    private static final int BUFFER_SIZE = 8192;

    public TextField serverPath;
    public TextField clientPath;
    public ListView<String> clientView;
    public ListView<String> serverView;

    private DataInputStream is;
    private DataOutputStream os;
    private byte[] buf;
    private File clientDirectory;

    private void updateClientView() {
        Platform.runLater(() -> {
            clientPath.setText(currentDirectory.getAbsolutePath());
            clientView.getItems().clear();
            clientView.getItems().add("...");
            clientView.getItems().addAll(currentDirectory.list());
        });
    }

    private void updateServerView() {
        Platform.runLater(() -> {
            serverPath.setText(serverDirectory.getAbsolutePath());
            serverView.getItems().clear();
            serverView.getItems().add("...");
            serverView.getItems().addAll(serverDirectory.list());
        });
    }

    public void download(ActionEvent actionEvent) {
        try {
            while (true) {
                String command = is.readUTF();
                if ("#file_message#".equals(command)) {
                    String name = is.readUTF();
                    long size = is.readLong();
                    File newFle = clientDirectory.toPath().resolve(name).toFile();
                    try (OutputStream fos = new FileOutputStream(newFle)) {
                        for (int i = 0; i < (size + BUFFER_SIZE - 1) / BUFFER_SIZE; i++) {
                            int readCount = is.read(buf);
                            fos.write(buf, 0, readCount);
                        }
                    }
                    System.out.println("Fie: " + name + " is uploaded");
                    updateServerView();
                    updateClientView();
                } else {
                    System.err.println("Unknown command: " + command);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String item = clientView.getSelectionModel().getSelectedItem();
        File selected = currentDirectory.toPath().resolve(item).toFile();
        if (selected.isFile()) {
            os.writeUTF("#file_message#");
            os.writeUTF(selected.getName());
            os.writeLong(selected.length());
            try (InputStream fis = new FileInputStream(selected)) {
                while (fis.available() > 0) {
                    int readBytes = fis.read(buf);
                    os.write(buf, 0, readBytes);
                }
            }
            os.flush();
            updateServerView();
            updateClientView();
        }
    }

    private void initNetwork() {
        try {
            buf = new byte[BUFFER_SIZE];
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentDirectory = new File(System.getProperty("user.home"));
        serverDirectory = new File("server");
        updateClientView();
        updateServerView();
        initNetwork();
        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = clientView.getSelectionModel().getSelectedItem();
                if (item.equals("...")) {
                    currentDirectory = currentDirectory.getParentFile();
                    updateClientView();
                } else {
                    File selected = currentDirectory.toPath().resolve(item).toFile();
                    if (selected.isDirectory()) {
                        currentDirectory = selected;
                        updateClientView();
                    }
                }
            }
        });
        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = serverView.getSelectionModel().getSelectedItem();
                if (item.equals("...")) {
                    serverDirectory = serverDirectory.getParentFile();
                    updateServerView();
                } else {
                    File selected =  serverDirectory.toPath().resolve(item).toFile();
                    if (selected.isDirectory()) {
                        serverDirectory = selected;
                        updateServerView();
                    }
                }
            }
        });
    }
}