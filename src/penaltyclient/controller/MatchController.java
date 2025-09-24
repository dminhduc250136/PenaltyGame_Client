package penaltyclient.controller;

import javafx.application.Platform;
import javafx.scene.control.Button;
import penaltyclient.view.MatchView1;

import java.io.*;
import java.net.Socket;

public class MatchController {
    private final MatchView1 view;
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String playerName;   // tên bạn
    private boolean isShooter;   // true nếu lượt này bạn sút, false nếu bạn làm GK

    public MatchController(MatchView1 view, String host, int port, String playerName) throws IOException {
        this.view = view;
        this.playerName = playerName;
        this.socket = new Socket(host, port);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        setupButtonHandlers();
        listenServer();
    }

    // Gán sự kiện cho 6 nút
    private void setupButtonHandlers() {
        Button[][] buttons = view.getChoiceButtons();
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                int choice = r * 3 + c; // 0..5
                buttons[r][c].setOnAction(e -> {
                    if (isShooter || !isShooter) {
                        sendChoice(choice);
                        view.disableButtons(); // khóa nút sau khi chọn
                    }
                });
            }
        }
    }

    private void sendChoice(int choice) {
        System.out.println("Choice" + choice);
        out.println("CHOICE:" + choice);
    }

    // Lắng nghe dữ liệu từ server
    private void listenServer() {
        Thread listener = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    handleMessage(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        listener.setDaemon(true);
        listener.start();
    }

    // Xử lý message từ server
    private void handleMessage(String msg) {
        Platform.runLater(() -> {
            if (msg.startsWith("ROLE:")) {
                // ROLE:SHOOTER hoặc ROLE:KEEPER
                isShooter = msg.contains("SHOOTER");
                view.updateRole(isShooter ? "Bạn đang sút!" : "Bạn là thủ môn!");

            } else if (msg.startsWith("TIMER:")) {
                int t = Integer.parseInt(msg.split(":")[1]);
                view.updateTimer(t);

            } else if (msg.startsWith("RESULT:")) {
                // RESULT:P1:0:GOAL hoặc RESULT:P2:3:MISS
                String[] parts = msg.split(":");
                int player = parts[1].equals("P1") ? 1 : 2;
                int turn = Integer.parseInt(parts[2]);
                boolean goal = parts[3].equals("GOAL");
                view.setScore(player, turn, goal);

            } else if (msg.startsWith("RESET:")) {
                view.resetButtons();
            }
        });
    }

    public void close() throws IOException {
        socket.close();
    }
}
