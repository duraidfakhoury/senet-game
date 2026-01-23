package controller;

import model.ComputerPlayer;
import model.Game;
import model.Piece;
import view.BoardPanel;

import javax.swing.*;

public class GameController {

    private final Game game;
    private final BoardPanel board;
    private final boolean computerMode;
    private ComputerPlayer computerPlayer;
    private JLabel rollLabel;
    private JLabel player1Label;
    private JLabel player2Label;
    private JButton rollButton;
    private int lastRoll = 0;

    public GameController(Game game, BoardPanel board, boolean computerMode) {
        this.game = game;
        this.board = board;
        this.computerMode = computerMode;
        if (computerMode) {
            this.computerPlayer = new ComputerPlayer(game);
        }
        refreshBoard();
    }

    public void setRollLabel(JLabel rollLabel) { this.rollLabel = rollLabel; }

    public void setInfoLabels(JLabel p1Label, JLabel p2Label) {
        this.player1Label = p1Label;
        this.player2Label = p2Label;
    }
    
    public void setRollButton(JButton rollButton) {
        this.rollButton = rollButton;
    }

    // Roll sticks
    public void rollSticks() {
        if (lastRoll != 0) return; // Already rolled, need to move first
        lastRoll = game.rollSticks();
        String playerName = (computerMode && game.getCurrentPlayer() == 2) ? "Computer" : "Player " + game.getCurrentPlayer();
        if (rollLabel != null)
            rollLabel.setText(playerName + " rolled: " + lastRoll);
        updateButtonState();
    }
    
    // Make computer move
    public void makeComputerMove() {
        if (!computerMode || game.getCurrentPlayer() != 2 || lastRoll == 0) {
            return;
        }
        
        Piece pieceToMove = computerPlayer.makeMove(lastRoll);
        if (pieceToMove != null) {
            onPieceSelected(pieceToMove);
        } else {
            // No valid moves found - skip turn
            if (rollLabel != null)
                rollLabel.setText("Computer has no valid moves. Turn skipped automatically.");
            System.out.println("Computer skipped turn - no valid moves available for roll: " + lastRoll);
            lastRoll = 0;
            game.nextPlayer();
            refreshBoard();
            updateButtonState();
            
            // If it's now computer's turn again (shouldn't happen, but handle it)
            if (computerMode && game.getCurrentPlayer() == 2) {
                Timer timer = new Timer(1000, evt -> {
                    rollSticks();
                    Timer moveTimer = new Timer(1500, evt2 -> {
                        makeComputerMove();
                    });
                    moveTimer.setRepeats(false);
                    moveTimer.start();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    // Move piece
    public void onPieceSelected(Piece piece) {
        if (piece.getOwner() != game.getCurrentPlayer()) return;
        if (lastRoll == 0) return;

        int pos = piece.getPosition();

        // 1️⃣ Three Truths / Re-Atoum / Horus
        if (piece.canExitNextTurn()) {
            int p = pos + 1;
            switch (p) {
                case 28 -> {
                    if (lastRoll == 3) {
                        piece.setCanExitNextTurn(false);
                    } else {
                        // Failed to exit Three Truths
                        piece.setPosition(14);
                        piece.setCanExitNextTurn(false);
                        String playerName = (computerMode && game.getCurrentPlayer() == 2) ? "Computer" : "Player " + game.getCurrentPlayer();
                        if (rollLabel != null) rollLabel.setText(playerName + " failed to exit Three Truths → back to Rebirth");
                        lastRoll = 0;
                        game.nextPlayer();
                        refreshBoard();
                        updateButtonState();
                        // If computer mode and it's now computer's turn, auto-roll
                        if (computerMode && game.getCurrentPlayer() == 2) {
                            if (rollButton != null) rollButton.setEnabled(false);
                            Timer timer = new Timer(1000, evt -> {
                                rollSticks();
                                Timer moveTimer = new Timer(1500, evt2 -> {
                                    makeComputerMove();
                                });
                                moveTimer.setRepeats(false);
                                moveTimer.start();
                            });
                            timer.setRepeats(false);
                            timer.start();
                        }
                        return;
                    }
                }
                case 29 -> {
                    if (lastRoll == 2) {
                        piece.setCanExitNextTurn(false);
                    } else {
                        // Failed to exit Re-Atoum
                        piece.setPosition(14);
                        piece.setCanExitNextTurn(false);
                        String playerName = (computerMode && game.getCurrentPlayer() == 2) ? "Computer" : "Player " + game.getCurrentPlayer();
                        if (rollLabel != null) rollLabel.setText(playerName + " failed to exit Re-Atoum → back to Rebirth");
                        lastRoll = 0;
                        game.nextPlayer();
                        refreshBoard();
                        updateButtonState();
                        // If computer mode and it's now computer's turn, auto-roll
                        if (computerMode && game.getCurrentPlayer() == 2) {
                            if (rollButton != null) rollButton.setEnabled(false);
                            Timer timer = new Timer(1000, evt -> {
                                rollSticks();
                                Timer moveTimer = new Timer(1500, evt2 -> {
                                    makeComputerMove();
                                });
                                moveTimer.setRepeats(false);
                                moveTimer.start();
                            });
                            timer.setRepeats(false);
                            timer.start();
                        }
                        return;
                    }
                }
                case 30 -> piece.setCanExitNextTurn(false);
            }
        }

        int targetPos = pos + lastRoll;

        // 2️⃣ House of Happiness
        if (pos < 25 && targetPos > 25) {
            if (rollLabel != null)
                rollLabel.setText("Cannot move past House of Happiness! Must roll exact number.");
            return;
        }

        // 3️⃣ Collision
        Piece occupyingPiece = game.getPieceAt(targetPos);
        if (occupyingPiece != null) {
            if (occupyingPiece.getOwner() != piece.getOwner()) {
                // Swap
                occupyingPiece.setPosition(pos);
            } else {
                if (rollLabel != null)
                    rollLabel.setText("Cannot move onto your own piece!");
                return;
            }
        }

        // 4️⃣ House of Rebirth if occupied
        if (targetPos == 14) {
            Piece p = game.getPieceAt(14);
            if (p != null && p != piece) {
                targetPos = game.findEmptyBeforeRebirth();
            }
        }

        // 5️⃣ Move piece
        piece.setPosition(targetPos);

        // 6️⃣ Other special houses
        switch (targetPos + 1) {
            case 15 -> { if (rollLabel != null) rollLabel.setText("Player landed on Rebirth!"); }
            case 26 -> { if (rollLabel != null) rollLabel.setText("Player landed on Happiness!"); }
            case 27 -> { piece.setPosition(14); if (rollLabel != null) rollLabel.setText("Player landed on Water → back to Rebirth!"); }
            case 28 -> { piece.setCanExitNextTurn(true); if (rollLabel != null) rollLabel.setText("Player landed on Three Truths!"); }
            case 29 -> { piece.setCanExitNextTurn(true); if (rollLabel != null) rollLabel.setText("Player landed on Re-Atoum!"); }
            case 30 -> { piece.setCanExitNextTurn(true); if (rollLabel != null) rollLabel.setText("Player landed on Horus!"); }
        }

        // 7️⃣ Exit piece
        if (targetPos >= 30) {
            piece.setPosition(-1);
            game.incrementPlayerExited(piece.getOwner());
            String playerName = (computerMode && piece.getOwner() == 2) ? "Computer" : "Player " + piece.getOwner();
            if (rollLabel != null)
                rollLabel.setText(playerName + " exited a piece!");
            int winner = game.checkWinner();
            if (winner != 0) {
                String winnerName = (computerMode && winner == 2) ? "Computer" : "Player " + winner;
                JOptionPane.showMessageDialog(board, winnerName + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        // 8️⃣ Extra turn for 1,3,5
        if (lastRoll == 1 || lastRoll == 3 || lastRoll == 5) {
            String playerName = (computerMode && game.getCurrentPlayer() == 2) ? "Computer" : "Player " + game.getCurrentPlayer();
            if (rollLabel != null) rollLabel.setText(playerName + " gets another turn!");
            lastRoll = 0;
            refreshBoard();
            updateButtonState();
            
            // If computer mode and it's computer's turn, auto-roll
            if (computerMode && game.getCurrentPlayer() == 2) {
                if (rollButton != null) rollButton.setEnabled(false);
                Timer timer = new Timer(1000, evt -> {
                    rollSticks();
                    Timer moveTimer = new Timer(1500, evt2 -> {
                        makeComputerMove();
                    });
                    moveTimer.setRepeats(false);
                    moveTimer.start();
                });
                timer.setRepeats(false);
                timer.start();
            }
            return;
        }

        lastRoll = 0;
        game.nextPlayer();
        refreshBoard();
        
        // Update button state
        updateButtonState();
        
        // If computer mode and it's now computer's turn, auto-roll
        if (computerMode && game.getCurrentPlayer() == 2) {
            if (rollButton != null) rollButton.setEnabled(false);
            Timer timer = new Timer(1000, evt -> {
                rollSticks();
                Timer moveTimer = new Timer(1500, evt2 -> {
                    makeComputerMove();
                });
                moveTimer.setRepeats(false);
                moveTimer.start();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    // Refresh board & info panel
    public void refreshBoard() {
        board.clearBoard();
        for (Piece p : game.getAllPieces()) {
            if (p.getPosition() >= 0 && p.getPosition() < 30)
                board.placePiece(p.getPosition(), p);
        }
        if (player1Label != null)
            player1Label.setText("Player 1: " + game.getPlayer1Exited() + " pieces exited");
        if (player2Label != null) {
            String player2Text = computerMode ? 
                "Computer: " + game.getPlayer2Exited() + " pieces exited" :
                "Player 2: " + game.getPlayer2Exited() + " pieces exited";
            player2Label.setText(player2Text);
        }
    }
    
    // Update button state based on current player
    private void updateButtonState() {
        if (rollButton != null && computerMode) {
            // Enable button only for player 1's turn
            rollButton.setEnabled(game.getCurrentPlayer() == 1 && lastRoll == 0);
        } else if (rollButton != null) {
            // Two player mode: enable when no roll or after move
            rollButton.setEnabled(lastRoll == 0);
        }
    }
}
