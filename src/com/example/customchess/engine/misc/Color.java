package com.example.customchess.engine.misc;

public enum Color {

    Black, White;

    public static Color getOppositeColor(Color color) {
        return color.equals(Color.White) ? Color.Black : Color.White;
    }
}
