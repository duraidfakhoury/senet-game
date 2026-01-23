package model;

public class Board {
    public static final int SIZE = 30;

    public boolean isSafeCell(int index) {
        return index == 25;
    }

    public boolean isSpecialCell(int index) {
        return index >= 25;
    }
    public int getCellEffect(int index, Piece piece) {
        switch (index) {
            case 14:
                return 0;
            case 25: // 26 House of Happiness
                return 0;
            case 26: // 27 House of Water
                return -10;
            case 27: // 28 House of Three Truths
                return 3;
            case 28: // 29 House of Re-Atoum
                return 2;
            case 29: // 30 House of Horus
                return -15;
            default:
                return 0;
        }
    }



    public boolean canExit(int position, int roll) {
        return position + roll == SIZE;
    }
}
