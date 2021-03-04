package com.example.customchess.networking;

import com.example.customchess.engine.misc.Pieces;
import com.example.customchess.engine.movements.Movable;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class ChessNetMovementPacket implements ChessNetPacket {
    private static final long serialVersionUID = 333555777L;
    private final Date movementTime;
    private final Movable movement;
    private String promotion;
    private boolean isLegal;

    public ChessNetMovementPacket(Movable movement) {
        this(movement, null);
    }

    public ChessNetMovementPacket(Movable movement, String promotion) {
        this.movementTime = Calendar.getInstance().getTime();
        this.movement = movement;
        this.promotion = promotion;
        this.isLegal  = false;
    }

    @Override
    public Movable getMovement() {
        return movement;
    }

    @Override
    public boolean isPromotionPacket() {
        return promotion != null;
    }

    @Override
    public String getPromotionPiece() {
        return promotion;
    }

    public void makeMovementLegal() {
        isLegal = true;
    }

    public boolean isMovementLegal() {
        return isLegal;
    }

    @Override
    public String toString() {
        return "Packet { " +
                " time = " + movementTime +
                ", movement = " + movement +
                ", promotion = " + promotion +
                ", legal = " + isLegal + " }";
    }
}
