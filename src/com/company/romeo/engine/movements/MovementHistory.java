package com.company.romeo.engine.movements;


import com.company.romeo.engine.figures.Piece;

public class MovementHistory {

    // that's just stupid DTO
    public final Movable movement;
    public final Piece start;
    public final Piece destination;

    public MovementHistory(Movable movement, Piece start, Piece destination) {
        this.movement = movement;
        this.start = start;
        this.destination = destination;
    }
}
