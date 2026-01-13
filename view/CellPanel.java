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

    public CellPanel(int index, GameController controller) {
        this.index = index;
        this.controller = controller;

        setBackground(new Color(245, 235, 200));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onCellClicked();
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

        // رسم الخانات الخاصة
        switch (index + 1) { // 1-indexed
            case 15 -> g.setColor(Color.ORANGE); // House of Rebirth
            case 26 -> g.setColor(Color.GREEN);  // House of Happiness
            case 27 -> g.setColor(Color.CYAN);   // House of Water
            case 28 -> g.setColor(Color.MAGENTA); // House of Three Truths
            case 29 -> g.setColor(Color.YELLOW);  // House of Re-Atoum
            case 30 -> g.setColor(Color.PINK);    // House of Horus
            default -> g.setColor(getBackground());
        }
        g.fillRect(0, 0, getWidth(), getHeight());

        if (piece != null) {
            g.setColor(piece.getOwner() == 1 ? Color.RED : Color.BLUE);
            g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
        }

        // إعادة رسم الحدود
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth()-1, getHeight()-1);
    }
}
