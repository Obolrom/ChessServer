package com.example.customchess.engine.movements;

import java.io.Serializable;
import java.util.LinkedList;


public class Movement implements Movable, Serializable {

    private Position start;
    private Position destination;

    public Movement(Position start, Position destination) {
        this.start = start;
        this.destination = destination;
    }

    @Override
    public Position getStart() {
        return start;
    }

    @Override
    public Position getDestination() {
        return destination;
    }

    // TODO: 29.01.21 try to refactor this piece of shit
    public static LinkedList<Position> getPositionsOnDistance(Movable movement) {
        Position start = movement.getStart();
        Position destination = movement.getDestination();
        int startVertical = start.getVertical().ordinal();
        int startHorizontal = start.getHorizontal() - 1;
        int destVertical = destination.getVertical().ordinal();
        int destHorizontal = destination.getHorizontal() - 1;
        LinkedList<Position> distance = new LinkedList<>();

        if (startVertical > destVertical) {
            int temp = startVertical;
            startVertical = destVertical;
            destVertical = temp;
        }
        if (startHorizontal > destHorizontal) {
            int temp = startHorizontal;
            startHorizontal = destHorizontal;
            destHorizontal = temp;
        }

        if (startHorizontal - destHorizontal == 0) {
            for (int i = startVertical + 1; i < destVertical; i++) {
                distance.add(new BoardPosition(i, startHorizontal + 1));
            }
        } else if (startVertical - destVertical == 0) {
            for (int i = startHorizontal + 1; i < destHorizontal; i++) {
                distance.add(new BoardPosition(startVertical, i + 1));
            }
        } else if (Math.abs(startHorizontal - destHorizontal) == Math.abs(startVertical - destVertical)) {
            if ((start.getVertical().ordinal() < destination.getVertical().ordinal() && start.getHorizontal() < destination.getHorizontal())
                    || (start.getVertical().ordinal() > destination.getVertical().ordinal() && start.getHorizontal() > destination.getHorizontal())) {
                for (int i = 1; (startVertical + i) < destVertical; i++) {
                    Position fuck = new BoardPosition(startVertical + i, startHorizontal + i + 1);
                    distance.add(fuck);
                }
            } else {
                for (int i = 1; (startVertical + i) < destVertical; i++) {
                    Position fuck = new BoardPosition(startVertical + i, destHorizontal - i + 1);
                    distance.add(fuck);
                }
            }
        }

        return distance;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (start != null) {
            builder.append(start.toString());
        }
        builder.append("-");
        if (destination != null) {
            builder.append(destination.toString());
        }
        return new String(builder);
    }
}
