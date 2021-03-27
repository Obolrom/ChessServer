package com.example.customchess.engine.figures;

import com.example.customchess.engine.misc.Color;

public abstract class ChessPiece implements Piece {
    public final Color color;
    protected boolean  firstMove;
    protected double   weight;


    public ChessPiece(double weight, Color color) {
        this.color = color;
        this.firstMove = true;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Piece";
    }

    public boolean hasSameColor(ChessPiece figure) {
        return this.color.equals(figure.color);
    }

    public boolean isWhite() {
        return color.equals(Color.White);
    }

    public boolean isBlack() {
        return color.equals(Color.Black);
    }

    @Override
    public Color getColor() {
        return color;
    }
}