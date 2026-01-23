package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Computer player using Expectiminimax algorithm
 * Uses arithmetic mean at chance nodes (dice rolls)
 */
public class ComputerPlayer {
    private final Game game;
    private final Random random = new Random();
    private static final int MAX_DEPTH = 3; // Search depth for Expectiminimax
    private static final Logger logger = Logger.getLogger(ComputerPlayer.class.getName());
    private static int calculationLogDepth = 0; // Track indentation for logging
    
    static {
        // Configure logger to output to console
        logger.setLevel(Level.INFO);
        java.util.logging.ConsoleHandler handler = new java.util.logging.ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new java.util.logging.SimpleFormatter() {
            @Override
            public String format(java.util.logging.LogRecord record) {
                return record.getMessage() + "\n";
            }
        });
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }
    
    public ComputerPlayer(Game game) {
        this.game = game;
    }
    
    /**
     * Makes a move for the computer player using Expectiminimax
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
        
        // Use Expectiminimax to find best move
        logger.info("=== Expectiminimax Calculation Start ===");
        logger.info("Current roll: " + roll);
        logger.info("Valid pieces to evaluate: " + validPieces.size());
        
        GameSnapshot snapshot = new GameSnapshot(game);
        Piece bestPiece = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        
        calculationLogDepth = 0;
        for (Piece piece : validPieces) {
            GameSnapshot testSnapshot = new GameSnapshot(snapshot);
            GameSnapshot.PieceSnapshot testPiece = findPieceInSnapshot(testSnapshot, piece);
            
            if (testPiece != null && simulateMove(testSnapshot, testPiece, roll, 2)) {
                logger.info("Evaluating move: Piece at position " + piece.getPosition() + " -> " + (piece.getPosition() + roll));
                double value = expectiminimax(testSnapshot, 2, MAX_DEPTH - 1, false);
                logger.info("Move evaluation result: " + String.format("%.2f", value));
                if (value > bestValue) {
                    bestValue = value;
                    bestPiece = piece;
                    logger.info("New best move found! Value: " + String.format("%.2f", bestValue));
                }
            }
        }
        
        if (bestPiece != null) {
            logger.info("=== Best Move Selected ===");
            logger.info("Piece at position: " + bestPiece.getPosition());
            logger.info("Target position: " + (bestPiece.getPosition() + roll));
            logger.info("Expected value: " + String.format("%.2f", bestValue));
        } else {
            logger.info("=== No Valid Move Found - Turn Skipped ===");
        }
        logger.info("=== Expectiminimax Calculation End ===\n");
        
        return bestPiece;
    }
    
    /**
     * Expectiminimax algorithm
     * @param snapshot Current game state
     * @param player Current player (1 or 2)
     * @param depth Remaining depth
     * @param isMaxNode True if MAX node (computer), false if MIN node (opponent)
     * @return Expected value of the position
     */
    private double expectiminimax(GameSnapshot snapshot, int player, int depth, boolean isMaxNode) {
        String indent = "  ".repeat(calculationLogDepth);
        String nodeType = isMaxNode ? "MAX" : "MIN";
        String playerName = player == 2 ? "Computer" : "Player";
        
        // Terminal conditions
        int winner = snapshot.checkWinner();
        if (winner == 2) {
            logger.fine(indent + "Terminal: Computer wins (INFINITY)");
            return Double.POSITIVE_INFINITY; // Computer wins
        }
        if (winner == 1) {
            logger.fine(indent + "Terminal: Player wins (-INFINITY)");
            return Double.NEGATIVE_INFINITY; // Opponent wins
        }
        if (depth == 0) {
            double eval = evaluatePosition(snapshot);
            logger.fine(indent + "Leaf node evaluation: " + String.format("%.2f", eval));
            return eval;
        }
        
        // Chance node: dice roll (all rolls have equal probability 1/5)
        if (depth > 0) {
            logger.fine(indent + "CHANCE node (Depth: " + depth + ", Player: " + playerName + ")");
            logger.fine(indent + "Calculating arithmetic mean over all possible rolls (1-5, each with probability 1/5)");
            
            double expectedValue = 0.0;
            double[] rollValues = new double[5];
            
            calculationLogDepth++;
            for (int roll = 1; roll <= 5; roll++) {
                double rollValue = expectiminimaxWithRoll(snapshot, player, roll, depth, isMaxNode);
                rollValues[roll - 1] = rollValue;
                expectedValue += rollValue / 5.0; // Arithmetic mean (equal probability)
                logger.fine(indent + "  Roll " + roll + ": value = " + String.format("%.2f", rollValue) + 
                           ", contribution = " + String.format("%.2f", rollValue / 5.0));
            }
            calculationLogDepth--;
            
            // Log the arithmetic calculation
            logger.info(indent + "Arithmetic Mean Calculation (CHANCE node):");
            logger.info(indent + "  E[Value] = (1/5) * [");
            for (int i = 0; i < 5; i++) {
                logger.info(indent + "    Roll " + (i + 1) + ": " + String.format("%.2f", rollValues[i]) + 
                           (i < 4 ? " +" : ""));
            }
            logger.info(indent + "  ]");
            logger.info(indent + "  Expected Value = " + String.format("%.2f", expectedValue) + 
                       " (arithmetic mean of all possible rolls)");
            
            return expectedValue;
        }
        
        return evaluatePosition(snapshot);
    }
    
    /**
     * Handle a specific dice roll at a chance node
     */
    private double expectiminimaxWithRoll(GameSnapshot snapshot, int player, int roll, int depth, boolean isMaxNode) {
        List<GameSnapshot.PieceSnapshot> playerPieces = snapshot.getPlayerPieces(player);
        List<Move> validMoves = new ArrayList<>();
        
        // Find all valid moves for this roll
        for (GameSnapshot.PieceSnapshot piece : playerPieces) {
            if (piece.getPosition() < 0) continue; // Already exited
            if (canMovePieceInSnapshot(snapshot, piece, roll)) {
                validMoves.add(new Move(piece, roll));
            }
        }
        
        // If no valid moves, evaluate current position and switch player
        if (validMoves.isEmpty()) {
            GameSnapshot nextSnapshot = new GameSnapshot(snapshot);
            nextSnapshot.currentPlayer = (player == 1) ? 2 : 1;
            return expectiminimax(nextSnapshot, (player == 1) ? 2 : 1, depth - 1, !isMaxNode);
        }
        
        // MAX node (computer's turn)
        if (isMaxNode) {
            String indent = "  ".repeat(calculationLogDepth);
            logger.fine(indent + "MAX node: Evaluating " + validMoves.size() + " moves for roll " + roll);
            double maxValue = Double.NEGATIVE_INFINITY;
            for (Move move : validMoves) {
                GameSnapshot testSnapshot = new GameSnapshot(snapshot);
                GameSnapshot.PieceSnapshot testPiece = findPieceInSnapshot(testSnapshot, move.piece);
                if (testPiece != null && simulateMove(testSnapshot, testPiece, move.roll, player)) {
                    int nextPlayer = (move.roll == 1 || move.roll == 3 || move.roll == 5) ? player : ((player == 1) ? 2 : 1);
                    double value = expectiminimax(testSnapshot, nextPlayer, depth - 1, false);
                    logger.fine(indent + "  Move (pos " + move.piece.getPosition() + " -> " + 
                               (move.piece.getPosition() + move.roll) + "): value = " + String.format("%.2f", value));
                    maxValue = Math.max(maxValue, value);
                }
            }
            logger.fine(indent + "MAX result: " + String.format("%.2f", maxValue));
            return maxValue;
        } 
        // MIN node (opponent's turn)
        else {
            String indent = "  ".repeat(calculationLogDepth);
            logger.fine(indent + "MIN node: Evaluating " + validMoves.size() + " moves for roll " + roll);
            double minValue = Double.POSITIVE_INFINITY;
            for (Move move : validMoves) {
                GameSnapshot testSnapshot = new GameSnapshot(snapshot);
                GameSnapshot.PieceSnapshot testPiece = findPieceInSnapshot(testSnapshot, move.piece);
                if (testPiece != null && simulateMove(testSnapshot, testPiece, move.roll, player)) {
                    int nextPlayer = (move.roll == 1 || move.roll == 3 || move.roll == 5) ? player : ((player == 1) ? 2 : 1);
                    double value = expectiminimax(testSnapshot, nextPlayer, depth - 1, true);
                    logger.fine(indent + "  Move (pos " + move.piece.getPosition() + " -> " + 
                               (move.piece.getPosition() + move.roll) + "): value = " + String.format("%.2f", value));
                    minValue = Math.min(minValue, value);
                }
            }
            logger.fine(indent + "MIN result: " + String.format("%.2f", minValue));
            return minValue;
        }
    }
    
    /**
     * Evaluate the current board position
     * Positive values favor computer (player 2), negative favor opponent (player 1)
     */
    private double evaluatePosition(GameSnapshot snapshot) {
        double score = 0.0;
        double exitedScore = 0.0;
        double positionScore = 0.0;
        double specialHouseScore = 0.0;
        double exitBonusScore = 0.0;
        
        // Exited pieces (highest priority)
        int exitedDiff = snapshot.getPlayer2Exited() - snapshot.getPlayer1Exited();
        exitedScore = exitedDiff * 1000.0;
        score += exitedScore;
        
        // Position of pieces on board
        for (GameSnapshot.PieceSnapshot piece : snapshot.getPieces()) {
            if (piece.getPosition() < 0) continue; // Already exited
            
            double pieceValue = piece.getPosition();
            if (piece.getOwner() == 2) {
                positionScore += pieceValue; // Computer pieces closer to exit are good
            } else {
                positionScore -= pieceValue; // Opponent pieces closer to exit are bad
            }
            
            // Special houses bonus
            int houseNum = piece.getPosition() + 1;
            if (piece.getOwner() == 2) {
                switch (houseNum) {
                    case 26 -> specialHouseScore += 50; // House of Happiness
                    case 28 -> specialHouseScore += 40; // Three Truths
                    case 29 -> specialHouseScore += 40; // Re-Atoum
                    case 30 -> specialHouseScore += 50; // Horus
                }
                if (houseNum == 27) specialHouseScore -= 20; // Avoid House of Water
            } else {
                switch (houseNum) {
                    case 26 -> specialHouseScore -= 50;
                    case 28 -> specialHouseScore -= 40;
                    case 29 -> specialHouseScore -= 40;
                    case 30 -> specialHouseScore -= 50;
                }
                if (houseNum == 27) specialHouseScore += 20;
            }
            
            // Can exit next turn bonus
            if (piece.canExitNextTurn() && piece.getOwner() == 2) {
                exitBonusScore += 30;
            } else if (piece.canExitNextTurn() && piece.getOwner() == 1) {
                exitBonusScore -= 30;
            }
        }
        
        score += positionScore + specialHouseScore + exitBonusScore;
        
        // Log evaluation breakdown
        String indent = "  ".repeat(calculationLogDepth);
        logger.fine(indent + "Position Evaluation:");
        logger.fine(indent + "  Exited pieces: " + exitedDiff + " * 1000 = " + String.format("%.2f", exitedScore));
        logger.fine(indent + "  Position score: " + String.format("%.2f", positionScore));
        logger.fine(indent + "  Special houses: " + String.format("%.2f", specialHouseScore));
        logger.fine(indent + "  Exit bonus: " + String.format("%.2f", exitBonusScore));
        logger.fine(indent + "  Total: " + String.format("%.2f", score));
        
        return score;
    }
    
    /**
     * Simulate a move on a snapshot (without affecting actual game)
     */
    private boolean simulateMove(GameSnapshot snapshot, GameSnapshot.PieceSnapshot piece, int roll, int player) {
        int pos = piece.getPosition();
        if (pos < 0) return false;
        
        // Handle special exit conditions
        if (piece.canExitNextTurn()) {
            int houseNum = pos + 1;
            if (houseNum == 28 && roll != 3) {
                piece.setPosition(14);
                piece.setCanExitNextTurn(false);
                return true;
            }
            if (houseNum == 29 && roll != 2) {
                piece.setPosition(14);
                piece.setCanExitNextTurn(false);
                return true;
            }
            if (houseNum == 30) {
                piece.setCanExitNextTurn(false);
            }
        }
        
        int targetPos = pos + roll;
        
        // House of Happiness rule
        if (pos < 25 && targetPos > 25) {
            return false;
        }
        
        // Collision handling
        GameSnapshot.PieceSnapshot occupyingPiece = snapshot.getPieceAt(targetPos);
        if (occupyingPiece != null) {
            if (occupyingPiece.getOwner() == piece.getOwner()) {
                return false; // Cannot move onto own piece
            } else {
                // Swap
                occupyingPiece.setPosition(pos);
            }
        }
        
        // House of Rebirth if occupied
        if (targetPos == 14) {
            GameSnapshot.PieceSnapshot p = snapshot.getPieceAt(14);
            if (p != null && p != piece) {
                targetPos = snapshot.findEmptyBeforeRebirth();
            }
        }
        
        // Move piece
        piece.setPosition(targetPos);
        
        // Handle special houses
        switch (targetPos + 1) {
            case 27 -> piece.setPosition(14); // House of Water
            case 28 -> piece.setCanExitNextTurn(true); // Three Truths
            case 29 -> piece.setCanExitNextTurn(true); // Re-Atoum
            case 30 -> piece.setCanExitNextTurn(true); // Horus
        }
        
        // Exit piece
        if (targetPos >= 30) {
            piece.setPosition(-1);
            if (player == 1) {
                snapshot.player1Exited++;
            } else {
                snapshot.player2Exited++;
            }
        }
        
        return true;
    }
    
    private boolean canMovePieceInSnapshot(GameSnapshot snapshot, GameSnapshot.PieceSnapshot piece, int roll) {
        int pos = piece.getPosition();
        if (pos < 0) return false;
        
        int targetPos = pos + roll;
        
        // Check if can exit
        if (targetPos >= 30) {
            if (piece.canExitNextTurn()) {
                int houseNum = pos + 1;
                if (houseNum == 28) return roll == 3;
                if (houseNum == 29) return roll == 2;
                if (houseNum == 30) return true;
            }
            return true;
        }
        
        // Check House of Happiness rule
        if (pos < 25 && targetPos > 25) {
            return false;
        }
        
        // Check collision with own piece
        GameSnapshot.PieceSnapshot occupyingPiece = snapshot.getPieceAt(targetPos);
        if (occupyingPiece != null && occupyingPiece.getOwner() == piece.getOwner()) {
            return false;
        }
        
        return true;
    }
    
    private boolean canMovePiece(Piece piece, int roll) {
        int pos = piece.getPosition();
        if (pos < 0) return false;
        
        int targetPos = pos + roll;
        
        // Check if can exit
        if (targetPos >= 30) {
            if (piece.canExitNextTurn()) {
                int houseNum = pos + 1;
                if (houseNum == 28) return roll == 3;
                if (houseNum == 29) return roll == 2;
                if (houseNum == 30) return true;
            }
            return true;
        }
        
        // Check House of Happiness rule
        if (pos < 25 && targetPos > 25) {
            return false;
        }
        
        // Check collision with own piece
        Piece occupyingPiece = game.getPieceAt(targetPos);
        if (occupyingPiece != null && occupyingPiece.getOwner() == piece.getOwner()) {
            return false;
        }
        
        return true;
    }
    
    private List<Piece> getComputerPieces() {
        List<Piece> allPieces = game.getAllPieces();
        List<Piece> computerPieces = new ArrayList<>();
        for (Piece p : allPieces) {
            if (p.getOwner() == 2) {
                computerPieces.add(p);
            }
        }
        return computerPieces;
    }
    
    private GameSnapshot.PieceSnapshot findPieceInSnapshot(GameSnapshot snapshot, Piece piece) {
        for (GameSnapshot.PieceSnapshot p : snapshot.getPieces()) {
            if (p.getOwner() == piece.getOwner() && p.getPosition() == piece.getPosition()) {
                return p;
            }
        }
        return null;
    }
    
    private GameSnapshot.PieceSnapshot findPieceInSnapshot(GameSnapshot snapshot, GameSnapshot.PieceSnapshot piece) {
        for (GameSnapshot.PieceSnapshot p : snapshot.getPieces()) {
            if (p.getOwner() == piece.getOwner() && p.getPosition() == piece.getPosition()) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * Helper class to represent a move
     */
    private static class Move {
        GameSnapshot.PieceSnapshot piece;
        int roll;
        
        Move(GameSnapshot.PieceSnapshot piece, int roll) {
            this.piece = piece;
            this.roll = roll;
        }
    }
    
    /**
     * Snapshot of game state for simulation
     */
    private static class GameSnapshot {
        private final List<PieceSnapshot> pieces;
        private int currentPlayer;
        public int player1Exited;
        public int player2Exited;
        
        public GameSnapshot(Game game) {
            this.pieces = new ArrayList<>();
            for (Piece p : game.getAllPieces()) {
                pieces.add(new PieceSnapshot(p));
            }
            this.currentPlayer = game.getCurrentPlayer();
            this.player1Exited = game.getPlayer1Exited();
            this.player2Exited = game.getPlayer2Exited();
        }
        
        public GameSnapshot(GameSnapshot other) {
            this.pieces = new ArrayList<>();
            for (PieceSnapshot p : other.pieces) {
                this.pieces.add(new PieceSnapshot(p));
            }
            this.currentPlayer = other.currentPlayer;
            this.player1Exited = other.player1Exited;
            this.player2Exited = other.player2Exited;
        }
        
        public List<PieceSnapshot> getPieces() { return pieces; }
        public int getCurrentPlayer() { return currentPlayer; }
        public int getPlayer1Exited() { return player1Exited; }
        public int getPlayer2Exited() { return player2Exited; }
        
        public PieceSnapshot getPieceAt(int position) {
            for (PieceSnapshot p : pieces) {
                if (p.getPosition() == position) return p;
            }
            return null;
        }
        
        public List<PieceSnapshot> getPlayerPieces(int player) {
            List<PieceSnapshot> result = new ArrayList<>();
            for (PieceSnapshot p : pieces) {
                if (p.getOwner() == player) result.add(p);
            }
            return result;
        }
        
        public int checkWinner() {
            if (player1Exited == 7) return 1;
            if (player2Exited == 7) return 2;
            return 0;
        }
        
        public int findEmptyBeforeRebirth() {
            for (int pos = 14; pos >= 0; pos--) {
                if (getPieceAt(pos) == null) return pos;
            }
            return 0;
        }
        
        public static class PieceSnapshot {
            private int position;
            private final int owner;
            private boolean canExitNextTurn;
            
            public PieceSnapshot(Piece piece) {
                this.position = piece.getPosition();
                this.owner = piece.getOwner();
                this.canExitNextTurn = piece.canExitNextTurn();
            }
            
            public PieceSnapshot(PieceSnapshot other) {
                this.position = other.position;
                this.owner = other.owner;
                this.canExitNextTurn = other.canExitNextTurn;
            }
            
            public int getPosition() { return position; }
            public void setPosition(int position) { this.position = position; }
            public int getOwner() { return owner; }
            public boolean canExitNextTurn() { return canExitNextTurn; }
            public void setCanExitNextTurn(boolean value) { this.canExitNextTurn = value; }
        }
    }
}