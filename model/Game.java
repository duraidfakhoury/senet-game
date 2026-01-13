package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    private final List<Piece> player1 = new ArrayList<>();
    private final List<Piece> player2 = new ArrayList<>();
    private final Board board = new Board();
    private int currentPlayer = 1;
    private final Random random = new Random();
    private int player1Exited = 0;
    private int player2Exited = 0;

    public Game() {
        for (int i = 0; i < 7; i++) {
            player1.add(new Piece(1));
            player2.add(new Piece(2));
        }
        setupInitialPositions();
    }

    private void setupInitialPositions() {
        int pos = 0;
        for (int i = 0; i < 7; i++) {
            player1.get(i).setPosition(pos++);
            player2.get(i).setPosition(pos++);
        }
    }

    public int getCurrentPlayer() { return currentPlayer; }

    public void nextPlayer() { currentPlayer = currentPlayer == 1 ? 2 : 1; }

    public int rollSticks() { return random.nextInt(5) + 1; }

    public List<Piece> getAllPieces() {
        List<Piece> all = new ArrayList<>();
        all.addAll(player1);
        all.addAll(player2);
        return all;
    }

    public Piece getPieceAt(int position) {
        for (Piece p : getAllPieces()) {
            if (p.getPosition() == position) return p;
        }
        return null;
    }

    public Board getBoard() { return board; }

    // Exit logic
    public int getPlayer1Exited() { return player1Exited; }
    public int getPlayer2Exited() { return player2Exited; }

    public void incrementPlayerExited(int player) {
        if (player == 1) player1Exited++;
        else player2Exited++;
    }

    // Check winner
    public int checkWinner() {
        if (player1Exited == 7) return 1;
        if (player2Exited == 7) return 2;
        return 0;
    }

    // Find first empty before Rebirth (House 15 / index 14)
    public int findEmptyBeforeRebirth() {
        for (int pos = 14; pos >= 0; pos--) {
            if (getPieceAt(pos) == null) return pos;
        }
        return 0;
    }
}
