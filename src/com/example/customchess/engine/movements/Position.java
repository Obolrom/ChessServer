package com.example.customchess.engine.movements;

import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.misc.Verticals;

import java.io.Serializable;
import java.util.List;

public interface Position extends Serializable {
    long serialVersionUID = 423501924;
    Verticals getVertical();
    Integer getHorizontal();
    List<Position> getPositionsAround();
    Position getPawnBeatenOnPassPosition(Color attacking);
    Position getRookPositionOnFlank();
    Position getRookPositionOnFlankAfterCastling();
    List<Position> getPositionsAroundKnight();
}
