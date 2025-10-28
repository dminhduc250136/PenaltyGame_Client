package penaltyclient.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // Import Alert
import javafx.scene.control.ButtonType; // Import ButtonType
import javafx.stage.Stage;
import penaltyclient.view.LobbyView;
import java.io.*;
import java.util.List;
import java.util.Optional; // Import Optional
import java.util.logging.Level;
import java.util.logging.Logger;
import penaltyclient.model.ClientListener;
import penaltyclient.model.SocketService;
// Import MatchController của bạn ở đây (phiên bản JavaFX)
// import penaltyclient.controller.MatchController; 

/**
 * Controller cho Lobby, quản lý Stage và Scene.
 * Đã cập nhật để xử lý các sự kiện từ ClientListener.
 */
public class LobbyController {

    private LobbyView lobbyView;
    private LoginController loginController; 
    private Stage stage; 
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;

    public LobbyController(String username, Stage stage, LoginController loginController) {
        this.username = username;
        this.stage = stage;
        this.loginController = loginController;
        this.lobbyView = new LobbyView(username, this);

        try {
            this.out = SocketService.getOutputStream();
            this.in = SocketService.getInputStream();
        } catch (IOException e) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, e);
            handleLogout();
        }
    }

    public void showLobbyView() {
        Scene scene = new Scene(lobbyView.getView(), 600, 400);

        stage.setTitle("Lobby - " + username);
        stage.setScene(scene);
        stage.setResizable(true); 
        stage.show();

        this.loadPlayers();
        
        // Khởi tạo và chạy ClientListener
        new Thread(new ClientListener(this)).start();
    }

    public void loadPlayers() {
        try {   
            sendMessage("GET_ONLINE_USERS"); // Dùng hàm sendMessage mới

            List<String> users = (List<String>)in.readObject();
            
            Platform.runLater(new Runnable() { // Sửa cho Java 8
                @Override
                public void run() {
                    for(String user : users) {
                        if(user.equals(username))
                            continue;
                        lobbyView.addPlayer(user, "online", 0);
                    }
                }
            });

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public LobbyView getLobbyView() {
        return lobbyView;
    }
    
    public void handleLogout() {
        try {
            sendMessage("LOGOUT"); // Dùng hàm sendMessage mới
            SocketService.close(); // Đóng socket khi logout
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        loginController.showLoginView();
    }

    public void handleInvite(String playerName) {
        sendMessage("INVITE:" + playerName); // Dùng hàm sendMessage mới
    }

    /**
     * HÀM MỚI: Gửi tin nhắn đến server (thay thế cho hàm trong ClientListener).
     */
    public void sendMessage(String msg) {
        try {
            if (out != null) {
                out.writeObject(msg);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Connection Error", "Could not send message to server.");
        }
    }

    /**
     * HÀM MỚI: Hiển thị Alert thông báo (thay thế JOptionPane.showMessageDialog).
     */
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * HÀM MỚI: Hiển thị Alert mời chơi (thay thế JOptionPane.showOptionDialog).
     */
    public void showInvitationAlert(String inviter) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Invitation");
        alert.setHeaderText(inviter + " muốn chơi với bạn!");
        alert.setContentText("Bạn có đồng ý không?");

        ButtonType btnYes = new ButtonType("Accept");
        ButtonType btnNo = new ButtonType("Refuse");
        alert.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == btnYes) {
            sendMessage("INVITE_ACCEPT:" + inviter);
        } else {
            sendMessage("INVITE_DECLINE:" + inviter);
        }
    }

    /**
     * HÀM MỚI: Xử lý khi nhận được lệnh START_MATCH.
     */
    public void startMatch(int matchId) {
        // Ẩn Lobby (thực ra là chuyển Scene)
        // Cần tạo MatchController (phiên bản JavaFX) và gọi hàm showMatchView()
        
        showAlert("Match Starting", "Trận đấu " + matchId + " đang bắt đầu!");
        // --- BẠN CẦN CHUYỂN MATCHCONTROLLER SANG JAVAFX ---
        
        // Ví dụ (sau khi bạn đã sửa MatchController):
        // MatchController matchController = new MatchController(stage, matchId, username);
        // matchController.showMatchView();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}