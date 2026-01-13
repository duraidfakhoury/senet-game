package model;

public class Piece {
    private int position; // -1 = خارج اللوح
    private final int owner; // 1 أو 2

    public Piece(int owner) {
        this.owner = owner;
        this.position = -1;
    }
    private boolean canExitNextTurn = false;

    public boolean canExitNextTurn() { return canExitNextTurn; }
    public void setCanExitNextTurn(boolean value) { this.canExitNextTurn = value; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public int getOwner() { return owner; }
}
