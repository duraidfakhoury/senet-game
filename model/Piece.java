package model;

public class Piece {
    private int position;
    private final int owner;

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
