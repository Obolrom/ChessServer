package com.example.customchess.engine.movements;

import java.io.Serializable;

public interface Movable extends Serializable {
    long serialVersionUID = 1220959842;
    Position getStart();
    Position getDestination();
}
