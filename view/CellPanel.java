package view;

import controller.GameController;
import model.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CellPanel extends JPanel {

    private Piece piece;
    private GameController controller;
    private final int index;
    private boolean isHovered = false;

    public CellPanel(int index, GameController controller) {
        this.index = index;
        this.controller = controller;

        setBackground(new Color(245, 235, 200));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createLoweredBevelBorder()
        ));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onCellClicked();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    private void onCellClicked() {
        if (piece != null && controller != null) {
            controller.onPieceSelected(piece);
        }
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        repaint();
    }

    public void clear() {
        this.piece = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw special cells with enhanced colors
        Color cellColor;
        String cellName = "";
        switch (index + 1) { // 1-indexed
            case 15 -> {
                cellColor = new Color(255, 165, 0); // Orange - House of Rebirth
                cellName = "Rebirth";
            }
            case 26 -> {
                cellColor = new Color(50, 205, 50); // Green - House of Happiness
                cellName = "Happiness";
            }
            case 27 -> {
                cellColor = new Color(0, 191, 255); // Cyan - House of Water
                cellName = "Water";
            }
            case 28 -> {
                cellColor = new Color(255, 20, 147); // Magenta - House of Three Truths
                cellName = "3 Truths";
            }
            case 29 -> {
                cellColor = new Color(255, 255, 0); // Yellow - House of Re-Atoum
                cellName = "Re-Atoum";
            }
            case 30 -> {
                cellColor = new Color(255, 192, 203); // Pink - House of Horus
                cellName = "Horus";
            }
            default -> cellColor = new Color(245, 235, 200);
        }
        
        // Apply hover effect
        if (isHovered && piece != null) {
            cellColor = cellColor.brighter();
        }
        
        g2d.setColor(cellColor);
        g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);

        // Draw piece with gradient effect
        if (piece != null) {
            int owner = piece.getOwner();
            Color pieceColor = owner == 1 ? new Color(220, 20, 60) : new Color(30, 144, 255); // Red or Blue
            
            // Draw piece shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(12, 12, getWidth() - 20, getHeight() - 20);
            
            // Draw piece with gradient
            GradientPaint gradient = new GradientPaint(
                getWidth() / 4, getHeight() / 4, pieceColor.brighter(),
                getWidth() * 3 / 4, getHeight() * 3 / 4, pieceColor.darker()
            );
            g2d.setPaint(gradient);
            g2d.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
            
            // Draw piece border
            g2d.setColor(pieceColor.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
            
            // Draw owner number
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            String text = String.valueOf(owner);
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 2;
            g2d.drawString(text, x, y);
        }

        // Draw cell number for special cells
        if (!cellName.isEmpty()) {
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(cellName)) / 2;
            g2d.drawString(cellName, x, getHeight() - 5);
        }

        // Draw border
        g2d.setColor(new Color(139, 69, 19));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
    }
}
