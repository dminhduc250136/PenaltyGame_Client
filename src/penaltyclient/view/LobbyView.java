/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package penaltyclient.view;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import penaltyclient.controller.LobbyController;
/**
 *
 * @author This PC
 */
public class LobbyView extends JFrame {
    private JLabel lblTitle, lblUserInfo;
    private JButton btnLogout;
    private JTable tblPlayers;
    private DefaultTableModel tableModel;
    private LobbyController lobbyController;

    public LobbyView() {
    }

    /** Creates new form LobbyView */
    public LobbyView(String username, LobbyController controller) {
        this.lobbyController = controller;
        setTitle("Lobby");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        lblTitle  = new JLabel("Penalty Game Lobby", SwingConstants.LEFT);
        lblUserInfo = new JLabel("Welcome: " + username);
        btnLogout = new JButton("Logout");

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.add(lblUserInfo);
        userPanel.add(btnLogout);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    

   
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }
    // </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
