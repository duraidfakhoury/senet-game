package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private GameController controller;
    private CellPanel[] cells;

    public BoardPanel(GameController controller) {
        this.controller = controller;
        setLayout(null);
        setBackground(new Color(222, 184, 135)); // Burlywood background
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        int cellWidth = 70;
        int cellHeight = 70;

        cells = new CellPanel[30];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 10; col++) {
                int index;
                if (row == 0) index = col;             // Row 1: left → right
                else if (row == 1) index = 19 - col;   // Row 2: right → left
                else index = 20 + col;                 // Row 3: left → right

                CellPanel cell = new CellPanel(index, controller);
                cell.setBounds(col * cellWidth + 15, row * cellHeight + 15, cellWidth, cellHeight);
                add(cell);
                cells[index] = cell;
            }
        }
    }

    public void setController(GameController controller) {
        this.controller = controller;
        for (CellPanel cell : cells)
            cell.setController(controller);
    }

    public void clearBoard() {
        for (CellPanel cell : cells)
            cell.clear();
    }

    public void placePiece(int index, model.Piece piece) {
        if (index >= 0 && index < 30)
            cells[index].setPiece(piece);
    }

}
