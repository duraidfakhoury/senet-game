package model;

import java.util.Random;

public class StickThrow {
    public static int roll() {
        Random r = new Random();
        int white = 0;

        for (int i = 0; i < 4; i++)
            if (r.nextBoolean()) white++;

        return white == 0 ? 5 : white;
    }
}
