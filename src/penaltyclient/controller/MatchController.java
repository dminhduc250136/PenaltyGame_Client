/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package penaltyclient.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import penaltyclient.model.SocketService;
import penaltyclient.view.MatchView;

/**
 *
 * @author This PC
 */
public class MatchController {
    private int matchId;
    private ObjectOutputStream out = SocketService.getOutputStream();
    private ObjectInputStream in = SocketService.getInputStream();
    private MatchView matchView;
    
    public MatchController(int matchId, String username) {
        this.matchId = matchId;
        matchView = new MatchView(matchId, username, this);
        matchView.setVisible(true);
    }

}
