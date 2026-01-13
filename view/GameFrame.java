package view;

import controller.GameController;
import model.Game;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private JLabel rollLabel;

    public GameFrame() {
        setTitle("Senet Game");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1️⃣ Game Logic
        Game game = new Game();
        BoardPanel boardPanel = new BoardPanel(null);
        GameController controller = new GameController(game, boardPanel);
        boardPanel.setController(controller);
        // 4️⃣ Label لعرض الرمية
        rollLabel = new JLabel("Welcome to Senet! Roll: -");
        rollLabel.setFont(new Font("Arial", Font.BOLD, 18));
        rollLabel.setHorizontalAlignment(SwingConstants.CENTER);
        controller.setRollLabel(rollLabel);
        JButton rollButton = new JButton("Roll");
        rollButton.addActionListener(e -> controller.rollSticks());
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 2));

        JLabel player1Label = new JLabel("Player 1 exited: 0 pieces");
        JLabel player2Label = new JLabel("Player 2 exited: 0 pieces");

        infoPanel.add(player1Label);
        infoPanel.add(player2Label);

        add(infoPanel, BorderLayout.SOUTH);

        add(rollButton, BorderLayout.SOUTH);

        add(rollLabel, BorderLayout.NORTH);

        add(boardPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
