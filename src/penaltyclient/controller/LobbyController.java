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

    }

    
}
