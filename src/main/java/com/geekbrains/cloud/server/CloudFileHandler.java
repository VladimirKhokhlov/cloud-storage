package com.geekbrains.cloud.server;

import javafx.scene.control.ListView;

import java.io.*;
import java.net.Socket;

public class CloudFileHandler implements Runnable {

    public static final int BUFFER_SIZE = 8192;
    private final Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private byte[] buf;
    private File serverDirectory;
    public ListView<String> serverView;

    public CloudFileHandler(Socket socket) throws IOException {
        this.socket = socket;
        System.out.println("Client connected!");
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        buf = new byte[BUFFER_SIZE];
        serverDirectory = new File("server");
    }

    @Override
    public void run() {
        try {
//            while (!socket.isClosed()) {
                String command = is.readUTF();
                if ("#file_message#".equals(command)) {
                    String name = is.readUTF();
                    long size = is.readLong();
                    File newFle = serverDirectory.toPath().resolve(name).toFile();
                    try (OutputStream fos = new FileOutputStream(newFle)) {
                        for (int i = 0; i < (size + BUFFER_SIZE - 1) / BUFFER_SIZE; i++) {
                            int readCount = is.read(buf);
                            fos.write(buf, 0, readCount);
                        }
                    }
                    System.out.println("Fie: " + name + " is uploaded");
                } else {
                    System.err.println("Unknown command: " + command);
                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String item = serverView.getSelectionModel().getSelectedItem();
        File selected = serverDirectory.toPath().resolve(item).toFile();
        try {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
