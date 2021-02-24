package com.company.romeo.engine.movements;


import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.misc.Verticals;

import java.util.List;

public interface Position {
    Verticals getVertical();
    Integer getHorizontal();
    List<Position> getPositionsAround();
    Position getPawnBeatenOnPassPosition(Color attacking);
    Position getRookPositionOnFlank();
    Position getRookPositionOnFlankAfterCastling();
    List<Position> getPositionsAroundKnight();
}
