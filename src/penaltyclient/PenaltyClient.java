/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package penaltyclient;

import penaltyclient.controller.LoginController;
import penaltyclient.view.LoginView;
/**
 *
 * @author This PC
 */
public class PenaltyClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LoginView view = new LoginView();
        LoginController loginController = new LoginController(view);
    }

}
