package view;

import controller.GameController;
import model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    private JLabel rollLabel;
    private JButton rollButton;
    private boolean computerMode;
    private GameController controller;

    public GameFrame(boolean computerMode) {
        this.computerMode = computerMode;
        setTitle("Senet Game - " + (computerMode ? "Player vs Computer" : "Two Players"));
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Main panel with styled background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 220));

        // 1️⃣ Game Logic
        Game game = new Game();
        BoardPanel boardPanel = new BoardPanel(null);
        controller = new GameController(game, boardPanel, computerMode);
        boardPanel.setController(controller);

        // Top panel with roll label and button
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(245, 245, 220));
        
        rollLabel = new JLabel("Welcome to Senet! Roll the sticks to begin.");
        rollLabel.setFont(new Font("Arial", Font.BOLD, 20));
        rollLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rollLabel.setForeground(new Color(139, 69, 19));
        rollLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controller.setRollLabel(rollLabel);
        
        rollButton = new JButton("Roll Sticks");
        rollButton.setFont(new Font("Arial", Font.BOLD, 16));
        rollButton.setPreferredSize(new Dimension(150, 40));
        rollButton.setBackground(new Color(34, 139, 34));
        rollButton.setForeground(Color.WHITE);
        rollButton.setFocusPainted(false);
        rollButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rollButton.addActionListener(e -> {
            controller.rollSticks();
            if (computerMode && game.getCurrentPlayer() == 2) {
                // Use Timer for delayed computer move
                Timer timer = new Timer(1500, evt -> {
                    controller.makeComputerMove();
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        // Set button reference after creating it
        controller.setRollButton(rollButton);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 245, 220));
        buttonPanel.add(rollButton);
        
        topPanel.add(rollLabel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Info panel with player stats
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        infoPanel.setBackground(new Color(245, 245, 220));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            "Game Status",
            0, 0,
            new Font("Arial", Font.BOLD, 14),
            new Color(139, 69, 19)
        ));

        JLabel player1Label = new JLabel("Player 1: 0 pieces exited");
        player1Label.setFont(new Font("Arial", Font.BOLD, 16));
        player1Label.setForeground(new Color(200, 0, 0));
        player1Label.setHorizontalAlignment(SwingConstants.CENTER);
        player1Label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String player2Text = computerMode ? "Computer: 0 pieces exited" : "Player 2: 0 pieces exited";
        JLabel player2Label = new JLabel(player2Text);
        player2Label.setFont(new Font("Arial", Font.BOLD, 16));
        player2Label.setForeground(new Color(0, 0, 200));
        player2Label.setHorizontalAlignment(SwingConstants.CENTER);
        player2Label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        infoPanel.add(player1Label);
        infoPanel.add(player2Label);

        controller.setInfoLabels(player1Label, player2Label);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
