package com.example.customchess.networking;

import com.example.customchess.engine.movements.Movable;

import java.io.Serializable;

public interface ChessNetPacket extends Serializable {
    Movable getMovement();
    boolean isPromotionPacket();
    String getPromotionPiece();
    boolean isMovementLegal();
    void makeMovementLegal();
}
