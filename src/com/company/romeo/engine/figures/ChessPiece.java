package com.company.romeo.engine.figures;


import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.movements.Position;

public abstract class ChessPiece implements Piece {

    public final Color color;
    protected boolean  firstMove;
    protected Position currentPosition;
    protected double   weight;


    public ChessPiece(Position position, double weight, Color color) {
        this.color = color;
        this.firstMove = true;
        this.weight = weight;
        this.currentPosition = position;
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
    public Position getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setPosition(Position newPosition) {
        this.currentPosition = newPosition;
    }
}