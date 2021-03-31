package com.example.customchess.engine.movements;

import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.misc.Verticals;

import java.util.ArrayList;
import java.util.List;


public class BoardPosition implements Position {
    private static final long serialVersionUID = 76767676L;
    private final Verticals vertical;
    private final String    horizontal;

    public BoardPosition(Verticals vertical, String horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    public BoardPosition(Integer vertical, Integer horizontal) {
        Verticals[] verticals = Verticals.values();
        this.vertical = verticals[vertical];
        this.horizontal = horizontal.toString();
    }

    public BoardPosition(Verticals vertical, Integer horizontal) {
        this.vertical = vertical;
        this.horizontal = horizontal.toString();
    }

    public Verticals getVertical() {
        return vertical;
    }

    public static boolean isShortSchemeCastling(Position destination) {
        return destination.getVertical().equals(Verticals.g);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof BoardPosition) {
            if (object == this) {
                return true;
            }
            return ((BoardPosition) object).horizontal.equals(this.horizontal)
                    && ((BoardPosition) object).vertical.equals(this.vertical);
        }

        return false;
    }

    @Override
    public String toString() {
        return vertical + horizontal;
    }

    public Integer getHorizontal() {
        return Integer.parseInt(horizontal);
    }

    @Override
    public List<Position> getPositionsAround() {
        List<Position> cagesAround = new ArrayList<>(9);
        // for example original position is 'G7'
        int originalVertical   = vertical.ordinal();
        int originalHorizontal = getHorizontal();
        int[][] arr = new int[][] {
                new int[] { originalVertical - 1, originalHorizontal - 1 }, // H6
                new int[] { originalVertical - 1, originalHorizontal     }, // H7
                new int[] { originalVertical - 1, originalHorizontal + 1 }, // H8

                new int[] { originalVertical, originalHorizontal - 1 }, // G6
                new int[] { originalVertical, originalHorizontal + 1 }, // G8

                new int[] { originalVertical + 1, originalHorizontal - 1 }, // F6
                new int[] { originalVertical + 1, originalHorizontal     }, // F7
                new int[] { originalVertical + 1, originalHorizontal + 1 }, // F8
        };

        int currentVertical;
        int currentHorizontal;
        for (int[] ints : arr) {
            currentVertical = ints[0];
            currentHorizontal = ints[1];
            if (currentVertical >= 0 && currentVertical <= 7
                    && currentHorizontal >= 1 && currentHorizontal <= 8) {
                cagesAround.add(new BoardPosition(currentVertical, currentHorizontal));
            }
        }

        return cagesAround;
    }

    public List<Position> getPositionsAroundKnight() {
        List<Position> cagesAround = new ArrayList<>(9);
        // for example original position is 'F3'
        int originalVertical = vertical.ordinal();
        int originalHorizontal = getHorizontal();
        int[][] arr = new int[][]{
                new int[]{originalVertical - 1, originalHorizontal - 2}, // G1
                new int[]{originalVertical + 1, originalHorizontal - 2}, // E1
                new int[]{originalVertical - 2, originalHorizontal - 1}, // H2
                new int[]{originalVertical + 2, originalHorizontal - 1}, // D2

                new int[]{originalVertical - 1, originalHorizontal + 2}, // G5
                new int[]{originalVertical - 2, originalHorizontal + 1}, // H4
                new int[]{originalVertical + 2, originalHorizontal + 1}, // D4
                new int[]{originalVertical + 1, originalHorizontal + 2}, // E5
        };

        int currentVertical;
        int currentHorizontal;
        for (int[] ints : arr) {
            currentVertical = ints[0];
            currentHorizontal = ints[1];
            if (currentVertical >= 0 && currentVertical <= 7
                    && currentHorizontal >= 1 && currentHorizontal <= 8) {
                cagesAround.add(new BoardPosition(currentVertical, currentHorizontal));
            }
        }
        return cagesAround;
    }

    @Override
    public Position getPawnBeatenOnPassPosition(Color attacking) {
        int originalHorizontal = getHorizontal();
        Position beaten;

        if (attacking.equals(Color.White)) {
            beaten = new BoardPosition(vertical, originalHorizontal - 1);
        } else {
            beaten = new BoardPosition(vertical, originalHorizontal + 1);
        }
        return beaten;
    }

    @Override
    public Position getRookPositionOnFlank() {
        Verticals temp;
        if (getVertical().ordinal() > 3) { // queen's flank
            temp = Verticals.a;
        } else {
            temp = Verticals.h;
        }

        return new BoardPosition(temp, getHorizontal());
    }

    @Override
    public Position getRookPositionOnFlankAfterCastling() {
        Verticals temp;
        if (getVertical().ordinal() > 3) { // queen's flank
            temp = Verticals.d;
        } else {
            temp = Verticals.f;
        }

        return new BoardPosition(temp, getHorizontal());
    }
}
