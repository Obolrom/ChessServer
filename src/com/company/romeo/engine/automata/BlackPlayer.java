package com.company.romeo.engine.automata;


import com.company.romeo.engine.exceptions.InvalidOrderMoveException;
import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.Game;
import com.company.romeo.engine.figures.ChessPiece;

public class BlackPlayer implements Player {

    private final Game game;
    private final Color teamColor;

    public BlackPlayer(Game game) {
        this.game = game;
        teamColor = Color.Black;
    }

    @Override
    public boolean isCorrectPlayerMove(ChessPiece selected) throws InvalidOrderMoveException {
        if (selected.isBlack()) {
            return true;
        }
        throw new InvalidOrderMoveException("Now the move of black player");
    }

    @Override
    public void changePlayer() {
        game.setCurrentPlayer(new WhitePlayer(game));
    }

    @Override
    public Color getColor() {
        return teamColor;
    }
}
