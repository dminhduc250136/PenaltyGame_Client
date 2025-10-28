/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package penaltyclient.model;

import java.io.ObjectInputStream;
import javafx.application.Platform; // Import thư viện JavaFX
import penaltyclient.controller.LobbyController;
import java.io.IOException;
import java.io.ObjectOutputStream; // Cần import
import java.util.logging.Level;
import java.util.logging.Logger;
import penaltyclient.controller.MatchController;
        /**
 *
 * @author This PC
 */
public class ClientListener implements Runnable {
    private ObjectOutputStream out = SocketService.getOutputStream();
    private ObjectInputStream in = SocketService.getInputStream();
    private LobbyController lobbyController;
    private MatchController matchController;
    
    public ClientListener(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }
    
    @Override
    public void run() {
        try {
            while(true) {
                Object obj = in.readObject();
                if(obj instanceof String) {
                    String[] parts = ((String) obj).split(":");
                    String command = parts[0];

                    switch(command) {
                        case "INVITE_FROM": {
                            String invitePlayer = parts[1];
                            
                            // Yêu cầu LobbyController hiển thị Alert trên luồng JavaFX
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showInvitationAlert(invitePlayer);
                                }
                            });
                            break;
                        }
                        case "INVITE_SUCCESS": {
                            // Yêu cầu LobbyController hiển thị thông báo
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showAlert("Invitation Sent", "Invited successfully!");
                                }
                            });
                            break;
                        }
                        case "INVITE_FAIL": {
                             Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showAlert("Invitation Failed", "Invite failed. The user might be busy.");
                                }
                            });
                            break;
                        }
                        case "INVITE_RESPONSE_ACCEPT": {
                            String responder = parts[1];
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showAlert("Invitation Accepted", responder + " accepted your challenge!");
                                }
                            });
                            break;
                        }
                        case "INVITE_RESPONSE_DECLINE": {
                            String responder2 = parts[1];
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showAlert("Invitation Declined", responder2 + " declined your challenge.");
                                }
                            });
                            break;
                        }
                        case "START_MATCH": {
                            int matchId = Integer.parseInt(parts[1]);
                            // Yêu cầu LobbyController bắt đầu trận đấu
                             Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
//                                    lobbyController.startMatch(matchId);
                                }
                            });
                            break;
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}