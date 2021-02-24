package com.company.romeo.engine.automata;


import com.company.romeo.engine.exceptions.InvalidOrderMoveException;
import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.Game;
import com.company.romeo.engine.figures.ChessPiece;

public class WhitePlayer implements Player {

    private final Game game;
    private final Color teamColor;


    public WhitePlayer(Game game) {
        this.game = game;
        teamColor = Color.White;
    }

    @Override
    public boolean isCorrectPlayerMove(ChessPiece selected)  throws InvalidOrderMoveException {
        if (selected.isWhite()) {
            return true;
        }
        throw new InvalidOrderMoveException("Now the move of white player");
    }

    @Override
    public void changePlayer() {
        game.setCurrentPlayer(new BlackPlayer(game));
    }

    @Override
    public Color getColor() {
        return teamColor;
    }
}
