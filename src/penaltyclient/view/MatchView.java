/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package penaltyclient.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import javax.swing.*;
import penaltyclient.controller.MatchController;

/**
 *
 * @author This PC
 */
public class MatchView extends JFrame {
    private MatchController matchController;
    private JLabel lblRound, lblScore, lblTimer;
    private JButton btnShoot, btnBlock, btnExit, btnSendChat;
    private JTextArea chatArea;
    private JTextField chatInput;
    
    
    public MatchView(int matchId, String username, MatchController matchController) {
        this.matchController = matchController;
        
        setTitle("Penalty Match");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel header = new JPanel(new FlowLayout());
        lblRound = new JLabel("Round: 1");
        lblScore = new JLabel("You: 0 - Enermy: 0");
        lblTimer = new JLabel("Time: 15s");
        lblTimer.setForeground(Color.RED);
        header.add(lblRound);
        header.add(lblScore);
        header.add(lblTimer);
        
        add(header, BorderLayout.NORTH);
        JPanel fieldPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // field color 
                for(int i = 0; i < getHeight(); i+= 40) {
                    g.setColor((i/40 % 2 == 0) ? new Color(50,150,50) : new Color(30,120,30));
                    g.fillRect(0, i, getWidth(), 40);
                }
                // goal
                g.setColor(Color.WHITE);
                g.fillRect(getWidth()/2 - 80, 20, 160, 10);
                g.drawRect(getWidth()/2 - 80, 20, 160, 60);
                
                // goalkeeper
                g.setColor(Color.RED);
                g.fillOval(getWidth()/2 - 15, 50,30,30);
                
                
                // ball
                g.setColor(Color.WHITE);
                g.fillOval(getWidth()/2 - 10, getHeight()-180, 20, 20);
                // player
                g.setColor(Color.BLUE);
                g.fillOval(getWidth()/2 - 10, getHeight()-140, 20, 20);
            }
        };
        
        add(fieldPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        btnShoot = new JButton("Shoot");
        btnBlock = new JButton("Block");
        btnExit = new JButton("Exit");
        controlPanel.add(btnShoot);
        controlPanel.add(btnBlock);
        controlPanel.add(btnExit);
        add(controlPanel, BorderLayout.SOUTH);
        
    }


    

    @SuppressWarnings("unchecked")
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
}    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

