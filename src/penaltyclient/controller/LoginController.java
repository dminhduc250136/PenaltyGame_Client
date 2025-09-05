/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package penaltyclient.controller;

import penaltyclient.view.LoginView;
import javax.swing.*;
import java.io.*;
import java.net.*;
/**
 *
 * @author This PC
 */
public class LoginController {
    private LoginView loginView;

    public LoginController(LoginView view) {
        this.loginView = view;

        loginView.btnLogin.addActionListener(e -> login());
        loginView.btnExit.addActionListener(e -> System.exit(0));
    }

    /**
     * @param args the command line arguments
     */
    private void login() {
        String user = loginView.txtUsername.getText();
        String pass = new String(loginView.txtPassword.getPassword());

        try(Socket socket = new Socket("localhost", 12345)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // send to server
            out.writeObject(user);
            out.writeObject(pass);

            // response
            String response = (String) in.readObject();
            if(response.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(loginView, "Login Success");
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
