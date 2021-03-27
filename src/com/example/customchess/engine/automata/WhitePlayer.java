package com.example.customchess.engine.automata;

import com.example.customchess.engine.Game;
import com.example.customchess.engine.exceptions.InvalidOrderMoveException;
import com.example.customchess.engine.figures.ChessPiece;
import com.example.customchess.engine.misc.Color;


public class WhitePlayer implements Player {

    private final Game game;
    private final Color          teamColor;


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
