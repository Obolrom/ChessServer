package com.example.customchess.engine.movements;

import com.example.customchess.engine.figures.Piece;

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
