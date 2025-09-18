/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package penaltyclient.controller;

import penaltyclient.view.LobbyView;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author This PC
 */
public class LobbyController {

    /**
     * @param args the command line arguments
     */
    private LobbyView lobbyView;

    public LobbyController(String username) {
        this.lobbyView = new LobbyView(username, this);
        this.lobbyView.setVisible(true);
        loadPlayers();
        
    }

    public void loadPlayers() {
        lobbyView.addPlayer("player2", "Online", 1200);
        lobbyView.addPlayer("player3", "Online", 980);
        lobbyView.addPlayer("player4", "Đang bận", 1500);
    }

    public void handleInvite(String playerName) {
        JOptionPane.showMessageDialog(null, "Đã gửi lời mời đến: " + playerName);
        // TODO: sau này gửi lệnh mời tới server
    }

    
}
