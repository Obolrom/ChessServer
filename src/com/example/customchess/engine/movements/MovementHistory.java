package com.example.customchess.engine.movements;

import com.example.customchess.engine.figures.Piece;

import java.io.Serializable;

public class MovementHistory implements Serializable {
    private static final long serialVersionUID = 120542434L;
    public final Movable movement;
    public final Piece start;
    public final Piece destination;

    public MovementHistory(Movable movement, Piece start, Piece destination) {
        this.movement = movement;
        this.start = start;
        this.destination = destination;
    }
}
