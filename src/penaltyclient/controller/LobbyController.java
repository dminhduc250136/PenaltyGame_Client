/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package penaltyclient.controller;

import penaltyclient.view.LobbyView;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import penaltyclient.model.SocketService;
/**
 *
 * @author This PC
 */
public class LobbyController {

    /**
     * @param args the command line arguments
     */
    private LobbyView lobbyView;
    private LoginController loginController;
    private ObjectOutputStream out = SocketService.getOutputStream();
    private ObjectInputStream in = SocketService.getInputStream();

    private String username;

    public LobbyController(String username) {
        this.lobbyView = new LobbyView(username, this);
        this.lobbyView.setVisible(true);
        this.loginController = new LoginController();
        this.username = username;
        this.loadPlayers();
        
        new Thread(new ClientListener()).start();
    }
    public void showLobbyView() {
        this.lobbyView.setVisible(true);
    }
    public void hideLobbyView() {
        this.lobbyView.dispose();
    }

    public void loadPlayers() {
        

        try {   
            // gui yeu cau lay onlineusers
            out.writeObject("GET_ONLINE_USERS");
            out.flush();

            // nhan du lieu
            List<String> users = (List<String>)in.readObject();
            for(String user : users) {
                if(user.equals(username))
                    continue;
                this.lobbyView.addPlayer(user, "online", 0);
            }
        } catch (IOException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
        
    public void handleLogout() {
        try {
            out.writeObject("LOGOUT");
            out.flush();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        this.hideLobbyView();
        loginController.showLoginView();
    }

    public void handleInvite(String playerName) {
        try {
            out.writeObject("INVITE:" + playerName);
            out.flush();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public class ClientListener implements Runnable {

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
                                String[] options = {"Accept", "Refuse"};

                                
                                int choice = JOptionPane.showOptionDialog(
                                        lobbyView, // giao dien hien thi
                                        "You have been invited by " + invitePlayer, //message
                                        "Lời mời",                     // tiêu đề dialog
                                        JOptionPane.DEFAULT_OPTION,    // kiểu option
                                        JOptionPane.INFORMATION_MESSAGE, // icon
                                        null,                          // icon custom
                                        options,                       // text của các nút
                                        options[0]   
                                        );
                                
                                System.out.println(choice);
                                
                                // xử lý theo lựa chọn
                                if (choice == 0) {
                                    // người chơi bấm Đồng ý
                                    sendMessage("INVITE_ACCEPT:" + invitePlayer);
                                } else if (choice == 1) {
                                    // người chơi bấm Từ chối
                                    sendMessage("INVITE_DECLINE:" + invitePlayer);
                                }
                                break;
                            }
                            case "INVITE_SUCCESS": {
                                JOptionPane.showMessageDialog(lobbyView, "Invited");
                                break;
                            }
                            case "INVITE_FAIL": {
                                JOptionPane.showMessageDialog(lobbyView, "Invite failed");
                                break;
                            }
                            
                            case "INVITE_RESPONSE_ACCEPT":
                                String responder = parts[1];
                                JOptionPane.showMessageDialog(lobbyView, "accepted by "  + responder);
                                break;
                            
                            case "INVITE_RESPONSE_DECLINE":
                                String responder2 = parts[1];
                                JOptionPane.showMessageDialog(lobbyView, "declined by "  + responder2);
                                break;
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
    
}
