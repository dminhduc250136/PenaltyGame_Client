/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package penaltyclient.controller;

import penaltyclient.view.LoginView;
import penaltyclient.view.LobbyView;
import javax.swing.*;
import java.io.*;
import java.net.*;
/**
 *
 * @author This PC
 */
public class LoginController {
    private LoginView loginView;

    public LoginController() {
        loginView = new LoginView(this);
        loginView.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public void login(String username, String password) {

        try(Socket socket = new Socket("localhost", 12345)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // send to server
            out.writeObject(username);
            out.writeObject(password);

            // response
            String response = (String) in.readObject();
            if(response.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(loginView, "Login Success");
                
                loginView.dispose();
                
                new LobbyController(username);
            }
            else {
                JOptionPane.showMessageDialog(loginView, "Invalid information");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(loginView, "Error connecting to server!");
        }
    }
}
