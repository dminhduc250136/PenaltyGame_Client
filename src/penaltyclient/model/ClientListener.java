package penaltyclient.model;

import javax.swing.JOptionPane;
import java.io.*;
import java.net.SocketException;
import penaltyclient.controller.LobbyController;
import penaltyclient.controller.MatchController;
import penaltyclient.view.LobbyView;
import javafx.application.Platform;
import java.io.ObjectInputStream;
import javafx.application.Platform; 
import penaltyclient.controller.LobbyController;
import java.io.IOException;
import java.util.List; // Cần import List
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author This PC
 */

public class ClientListener implements Runnable {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private LobbyController lobbyController;
    private MatchController matchController;
    private LobbyView lobbyView;
    
    public ClientListener(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        try {
            out = SocketService.getOutputStream();
            in = SocketService.getInputStream();
        } catch (IOException e) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void setMatchController(MatchController matchController) {
        this.matchController = matchController;
        this.lobbyController = null; // Không cần lobby nữa khi vào trận
        this.lobbyView = null;
    }
    
    @Override
    public void run() {
        try {
            while(true) {
                Object obj = in.readObject(); 
                
                if (obj instanceof String) {
                    // Xử lý các command dạng String
                    String message = (String) obj;
                    String[] parts = message.split(":");
                    String command = parts[0];

                    switch(command) {
                        case "INVITE_FROM": {
                            String invitePlayer = parts[1];
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showInvitationAlert(invitePlayer);
                                }
                            });
                            break;
                        }
                        case "INVITE_SUCCESS": {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showAlert("Invitation Sent", "Invited successfully!");
                                }
                            });
                            break;
                        }
                        // ... (các case khác của bạn) ...
                        
                        case "INVITE_FAIL":
                        case "INVITE_RESPONSE_ACCEPT":
                        case "INVITE_RESPONSE_DECLINE":
                            // Bạn có thể gộp các case xử lý Alert đơn giản
                            final String alertTitle = command;
                            final String alertMessage = parts[1]; // Hoặc xử lý parts[1]
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showAlert(alertTitle, alertMessage);
                                }
                            });
                            break;

                        case "START_MATCH": {
                            int matchId = Integer.parseInt(parts[1]);
//                             Platform.runLater(new Runnable() {
//                                @Override
//                                public void run() {
//                                    lobbyController.startMatch(matchId);
//                                }
//                            });
                            System.out.println("Ban da tham ga match:" + matchId);
                            break;
                        }
//=======
//                Object obj = in.readObject();
//                if(obj instanceof String) {
//                    String msg = (String) obj;
//                    String[] parts = msg.split(":");
//                    String command = parts[0];
//
//                    if (matchController != null) {
//                        Platform.runLater(() -> matchController.handleServerMessage(msg));
//                    } else if (lobbyController != null) {
//                        switch(command) {
//                            case "INVITE_FROM": {
//                                String invitePlayer = parts[1];
//                                String[] options = {"Accept", "Refuse"};
//
//                                int choice = JOptionPane.showOptionDialog(
//                                        lobbyView, // giao dien hien thi
//                                        "You have been invited by " + invitePlayer, //message
//                                        "Lời mời",                     // tiêu đề dialog
//                                        JOptionPane.DEFAULT_OPTION,    // kiểu option
//                                        JOptionPane.INFORMATION_MESSAGE, // icon
//                                        null,                          // icon custom
//                                        options,                       // text của các nút
//                                        options[0]   
//                                        );
//
//                                // xử lý theo lựa chọn
//                                if (choice == 0) {
//                                    // người chơi bấm Đồng ý
//                                    sendMessage("INVITE_ACCEPT:" + invitePlayer);
//                                } else if (choice == 1) {
//                                    // người chơi bấm Từ chối
//                                    sendMessage("INVITE_DECLINE:" + invitePlayer);
//                                }
//                                break;
//                            }
//                            case "INVITE_SUCCESS": {
//                                JOptionPane.showMessageDialog(lobbyView, "Invited");
//                                break;
//                            }
//                            case "INVITE_FAIL": {
//                                JOptionPane.showMessageDialog(lobbyView, "Invite failed");
//                                break;
//                            }
//
//                            case "INVITE_RESPONSE_ACCEPT": {
//                                String responder = parts[1];
//                                JOptionPane.showMessageDialog(lobbyView, "accepted by "  + responder);
//                                break;
//                            }
//
//                            case "INVITE_RESPONSE_DECLINE": {
//                                String responder2 = parts[1];
//                                JOptionPane.showMessageDialog(lobbyView, "declined by "  + responder2);
//                                break;
//                            }
//                            
//                            case "START_MATCH": {
//                                int matchId = Integer.parseInt(parts[1]);
//                                String opponentUsername = parts[1];
//                                // String firstShooter = parts[2]; // Và người sút trước
//
//                                final String finalOpponent = opponentUsername;
//                                // final String finalFirstShooter = firstShooter; // Nếu có
//
//                                Platform.runLater(() -> {
//                                    lobbyController.hideLobbyView(); // Ẩn lobby
//                                    // Tạo MatchController, nó sẽ tự tạo MatchView
//                                    matchController = new MatchController(lobbyController.getUsername(), finalOpponent, this);
//                                    setMatchController(matchController); // Cập nhật listener để biết đang ở trong match
//                                    matchController.showMatchView();
//                                    // Thông báo cho MatchController biết trận đấu bắt đầu (có thể gộp vào constructor)
//                                     matchController.handleMatchStart(new String[]{"MATCH_START", finalOpponent, finalFirstShooter});
//                                    // Server cần gửi thêm message MATCH_START sau khi client sẵn sàng
//                                });
//                                break;
//                            }
//                            default:
//                                System.out.println("unknown msg:" + msg);
//                        }
//                    }else {
//                         System.out.println("Listener received message but no active controller: " + msg);
//>>>>>>> Trung
                    }
                } 
                // === SỬA LỖI ĐA LUỒNG ===
                // Nếu đối tượng nhận được là một List (phản hồi từ GET_ONLINE_USERS)
                else if (obj instanceof List) { 
                    try {
                        // Giả định đây là List<String>
                        @SuppressWarnings("unchecked") // Bỏ qua cảnh báo cast
                        List<String> userList = (List<String>) obj;
                        
                        // Gọi hàm cập nhật giao diện trong LobbyController
                        lobbyController.updateOnlinePlayers(userList);
                        
                    } catch (ClassCastException e) {
                        System.err.println("ClientListener: Đã nhận 1 List nhưng không phải List<String>!");
                    }
                    // TODO: Bạn cũng nên làm điều tương tự cho
                    // List<MatchRecord> (cho lịch sử) và List<RankingEntry> (cho xếp hạng)
                    // Bằng cách kiểm tra `obj instanceof List` và kiểm tra phần tử đầu tiên
                }
            }
        } catch (EOFException | SocketException e) {
             System.out.println("Connection closed by server or client.");
             // Có thể thêm logic để hiển thị thông báo lỗi và quay về màn hình Login
             Platform.runLater(() -> {
                if(matchController != null) matchController.showErrorAndClose("Connection lost.");
                // Cần thêm xử lý quay về màn hình Login
             });
        } catch (Exception e) {
            e.printStackTrace();
             Platform.runLater(() -> {
                if(matchController != null) matchController.showErrorAndClose("An error occurred: " + e.getMessage());
                 // Cần thêm xử lý quay về màn hình Login
             });
        } finally {
             // Dọn dẹp tài nguyên nếu cần
             // try { SocketService.close(); } catch (IOException ioEx) { }
        }
    }


    public void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("client send: " + msg);
        }
        catch(Exception e) {
            System.out.println("ClientListener ngắt kết nối: " + e.getMessage());
            // (Lỗi StreamCorruptedException sẽ xảy ra ở đây nếu 2 luồng cùng đọc)
            e.printStackTrace();
        }
    }
}