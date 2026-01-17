package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayer {
    private final Game game;
    private final Random random = new Random();
    
    public ComputerPlayer(Game game) {
        this.game = game;
    }
    
    /**
     * Makes a move for the computer player
     * Uses the same game logic as human players
     * @param roll The dice roll value (1-5)
     * @return The piece that was moved, or null if no valid move
     */
    public Piece makeMove(int roll) {
        List<Piece> computerPieces = getComputerPieces();
        
        // Filter pieces that can make valid moves
        List<Piece> validPieces = new ArrayList<>();
        for (Piece piece : computerPieces) {
            if (piece.getPosition() < 0) continue; // Already exited
            if (canMovePiece(piece, roll)) {
                validPieces.add(piece);
            }
        }
        
        if (validPieces.isEmpty()) {
            return null; // No valid moves
        }
        
        // Strategy: Prefer pieces that can exit, then pieces closer to exit
        Piece bestPiece = null;
        int bestScore = Integer.MIN_VALUE;
        
        for (Piece piece : validPieces) {
            int score = evaluateMove(piece, roll);
            if (score > bestScore) {
                bestScore = score;
                bestPiece = piece;
            }
        }
        
        return bestPiece;
    }
    
    private List<Piece> getComputerPieces() {
        List<Piece> allPieces = game.getAllPieces();
        List<Piece> computerPieces = new ArrayList<>();
        for (Piece p : allPieces) {
            if (p.getOwner() == 2) { // Computer is player 2
                computerPieces.add(p);
            }
        }
        return computerPieces;
    }
    
    private boolean canMovePiece(Piece piece, int roll) {
        int pos = piece.getPosition();
        if (pos < 0) return false; // Already exited
        
        int targetPos = pos + roll;
        
        // Check if can exit
        if (targetPos >= 30) {
            // Check special exit conditions
            if (piece.canExitNextTurn()) {
                int houseNum = pos + 1;
                if (houseNum == 28) return roll == 3; // Three Truths
                if (houseNum == 29) return roll == 2; // Re-Atoum
                if (houseNum == 30) return true; // Horus
            }
            return true; // Normal exit
        }
        
        // Check House of Happiness rule
        if (pos < 25 && targetPos > 25) {
            return false; // Cannot jump over
        }
        
        // Check collision with own piece
        Piece occupyingPiece = game.getPieceAt(targetPos);
        if (occupyingPiece != null && occupyingPiece.getOwner() == piece.getOwner()) {
            return false; // Cannot move onto own piece
        }
        
        return true;
    }
    
    private int evaluateMove(Piece piece, int roll) {
        int pos = piece.getPosition();
        int targetPos = pos + roll;
        int score = 0;
        
        // Highest priority: exiting the board
        if (targetPos >= 30) {
            score += 1000;
            // Check if it's a valid exit
            if (piece.canExitNextTurn()) {
                int houseNum = pos + 1;
                if (houseNum == 28 && roll != 3) return -1000; // Invalid exit
                if (houseNum == 29 && roll != 2) return -1000; // Invalid exit
            }
            return score;
        }
        
        // High priority: reaching special houses
        switch (targetPos + 1) {
            case 26 -> score += 50; // House of Happiness
            case 28 -> score += 40; // Three Truths
            case 29 -> score += 40; // Re-Atoum
            case 30 -> score += 50; // Horus
        }
        
        // Medium priority: capturing opponent piece
        Piece occupyingPiece = game.getPieceAt(targetPos);
        if (occupyingPiece != null && occupyingPiece.getOwner() != piece.getOwner()) {
            score += 30;
        }
        
        // Low priority: moving forward (closer to exit)
        score += targetPos;
        
        // Avoid House of Water (position 26, house 27)
        if (targetPos == 26) {
            score -= 20;
        }
        
        return score;
    }
}
