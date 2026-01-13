package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {


    private GameController controller;
    private  CellPanel[] cells ;

    public BoardPanel(GameController controller) {
        this.controller = controller;
        setLayout(null); // نرسم يدوياً
        int cellWidth = 60;
        int cellHeight = 60;

        cells = new CellPanel[30];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 10; col++) {
                int index;
                if (row == 0) index = col;             // صف 1: يسار → يمين
                else if (row == 1) index = 19 - col;   // صف 2: يمين → يسار
                else index = 20 + col;                 // صف 3: يسار → يمين

                CellPanel cell = new CellPanel(index, controller);
                cell.setBounds(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
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
