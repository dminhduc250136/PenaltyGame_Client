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
    private Socket socket;
    

    public LobbyController(String username) {
        this.lobbyView = new LobbyView(username, this);
        this.lobbyView.setVisible(true);
        this.loginController = new LoginController();
        this.socket = socket;
        this.loadPlayers();
    }
    public void showLobbyView() {
        this.lobbyView.setVisible(true);
    }
    public void hideLobbyView() {
        this.lobbyView.dispose();
    }

    public void loadPlayers() {
        

        try {
            ObjectInputStream in = SocketService.getInputStream();
            ObjectOutputStream out = SocketService.getOutputStream();
            // gui yeu cau lay onlineusers

            out.writeObject("GET_ONLINE_USERS");
            
            
            // nhan du lieu
            List<String> users = (List<String>)in.readObject();
            for(String user : users) {
                this.lobbyView.addPlayer(user, "online", 0);
            }
        } catch (IOException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
        
    public void handleLogout() {
        this.hideLobbyView();
        loginController.showLoginView();
    }

    public void handleInvite(String playerName) {
        JOptionPane.showMessageDialog(null, "Đã gửi lời mời đến: " + playerName);
        // TODO: sau này gửi lệnh mời tới server
    }

    
}
