package com.example.customchess.engine;

import com.example.customchess.engine.exceptions.ChessException;
import com.example.customchess.engine.figures.King;
import com.example.customchess.engine.figures.Knight;
import com.example.customchess.engine.figures.Piece;
import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.movements.Movement;
import com.example.customchess.engine.movements.MovementHistory;
import com.example.customchess.engine.movements.Position;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EndGameChecker {
    private final Board board;
    private List<Piece> whiteTeam;
    private List<Piece> blackTeam;

    public EndGameChecker(Board board) {
        this.board = board;
    }

    public boolean isCheckMate(Color teamColor) {
        updateTeams();
        List<Piece> enemyTeam = getTeamBy(Color.getOppositeColor(teamColor));
        Piece king = getKingBy(teamColor);
        assert king != null;
        Position kingPos = board.getPositionOfPiece(king);
        boolean answer = false;
        List<Position> attackingFigures = getAttackingFigures(enemyTeam, kingPos);
        int attackingFiguresAmount = attackingFigures.size();
        int cagesAroundKingUnderAttack = 0;
        List<Position> emptyCagesAroundKing = getEmptyPositionsAround(kingPos);

        for (Position position : emptyCagesAroundKing) {
            if (isPositionUnderAttackByEnemyTeam(king.getColor(), position)) {
                cagesAroundKingUnderAttack++;
            }
        }

        if (attackingFiguresAmount > 1) {
            if (cagesAroundKingUnderAttack == emptyCagesAroundKing.size()) {
                answer = true;
            }
        } else if (attackingFiguresAmount == 1) {
            boolean canBeat = isFigureToBeatAttackingPiece(teamColor, attackingFigures.get(0));
            boolean canCover = isPieceToCoverKingFromCheck(teamColor, kingPos, attackingFigures.get(0));
            boolean canMoveAway = isCageToMoveKingAway(emptyCagesAroundKing, kingPos);
            if ( ! canBeat
                    & ! canMoveAway
                    & ! canCover) {
                answer = true;
            }
        }

        return answer;
    }

    private List<Position> getAttackingFigures(List<Piece> enemyTeam, Position attackedPosition) {
        updateTeams();
        List<Position> attackingFigures = new LinkedList<>();
        Position currentPosition;

        for (Piece figure : enemyTeam) {
            currentPosition = board.getPositionOfPiece(figure);

            try {
                if (figure.isFightTrajectoryValid(new Movement(currentPosition, attackedPosition))
                        & board.isDistanceFree(new Movement(currentPosition, attackedPosition))) {
                    attackingFigures.add(currentPosition);
                }
            } catch (ChessException e) {
                // trajectory is incorrect
            }
        }

        return attackingFigures;
    }

    private Piece getKingBy(Color teamColor) {
        updateTeams();
        List<Piece> team = getTeamBy(teamColor);

        for (Piece currentPiece : team) {
            if ( currentPiece instanceof King ) {
                return currentPiece;
            }
        }
        return null;
    }

    public boolean isKingUnderAttack(Color teamColor) {
        updateTeams();
        Piece king = getKingBy(teamColor);
        assert king != null;
        return isPositionUnderAttackByEnemyTeam(teamColor, board.getPositionOfPiece(king));
    }

    public boolean isPositionUnderAttackByEnemyTeam(Color teamColor, Position position) {
        updateTeams();
        List<Piece> enemyTeam = getTeamBy( Color.getOppositeColor(teamColor) );
        Position currentPosition;
        boolean answer = false;

        for (Piece figure : enemyTeam) {
            currentPosition = board.getPositionOfPiece(figure);

            try {
                assert currentPosition != null;
                Movement move = new Movement(currentPosition, position);

                if (figure.isFightTrajectoryValid(move)
                        & board.isDistanceFree(move)
                        & ! position.equals(currentPosition)) {
                    answer = true;
                    break;
                }
            } catch (ChessException e) {
                // trajectory is incorrect
            }
        }

        return answer;
    }

    public boolean isCageToMoveKingAway(List<Position> emptyCagesAroundKing, Position kingPosition) {
        updateTeams();
        MovementHistory backUpMove;
        Movement currentMovement;
        Piece king = board.findBy(kingPosition);
        boolean answer = false;

        for (Position cage : emptyCagesAroundKing) {
            try {
                assert king != null;
                currentMovement = new Movement(kingPosition, cage);

                if (king.isTrajectoryValid(currentMovement)
                        && board.isDistanceFree(currentMovement)) {
                    backUpMove = new MovementHistory(currentMovement, king, board.findBy(cage));
                    board.swapFigures(kingPosition, cage);
                    if ( ! isKingUnderAttack(king.getColor())) {
                        answer = true;
                        board.restorePreviousTurn(backUpMove);
                        break;
                    }
                    board.restorePreviousTurn(backUpMove);
                }
            } catch (ChessException ignored) {

            }
        }

        return answer;
    }

    public boolean isPieceToCoverKingFromCheck(Color kingColor,
                                               Position kingPosition,
                                               Position attackingPiece) {
        updateTeams();
        List<Piece> ourTeam = getTeamBy(kingColor);
        MovementHistory backUpMove;
        Movement currentMovement;
        Position currentPosition;
        boolean answer = false;
        LinkedList<Position> distance = Movement.getPositionsOnDistance(new Movement(kingPosition, attackingPiece));

        for (Piece piece : ourTeam) {
            currentPosition = board.getPositionOfPiece(piece);
            // cover attacking figure case

            for (Position cage : distance) {
                try {
                    assert currentPosition != null;
                    currentMovement = new Movement(currentPosition, cage);

                    if (piece.isTrajectoryValid(currentMovement)
                            && board.isDistanceFree(currentMovement)) {
                        backUpMove = new MovementHistory(currentMovement, board.findBy(currentPosition), board.findBy(cage));
                        board.swapFigures(currentPosition, cage);
                        if ( ! isKingUnderAttack(kingColor)) {
                            answer = true;
                            board.restorePreviousTurn(backUpMove);
                            break;
                        }
                        board.restorePreviousTurn(backUpMove);
                    }
                } catch (ChessException e) {
                    // trajectory is incorrect
                }
            }
        }

        return answer;
    }

    public boolean isFigureToBeatAttackingPiece(Color kingColor, Position attackingPiece) {
        updateTeams();
        List<Piece> ourTeam = getTeamBy(kingColor);
        List<Piece> enemyTeam = getTeamBy(Color.getOppositeColor(kingColor));
        MovementHistory backUpMove;
        Movement currentMovement;
        Position currentPosition;
        boolean answer = false;

        for (Piece piece : ourTeam) {
            currentPosition = board.getPositionOfPiece(piece);
            // beat attacking figure case
            try {
                assert currentPosition != null;

                currentMovement = new Movement(currentPosition, attackingPiece);
                if (piece.isFightTrajectoryValid(currentMovement)
                        && board.isDistanceFree(currentMovement)) {
                    backUpMove = new MovementHistory(currentMovement, board.findBy(currentPosition), board.findBy(attackingPiece));
                    enemyTeam.remove(board.findBy(attackingPiece));
                    board.beatFigure(currentPosition, attackingPiece);
                    if ( ! isKingUnderAttack(kingColor)) {
                        answer = true;
                        board.restorePreviousTurn(backUpMove);
                        enemyTeam.add(board.findBy(attackingPiece));
                        break;
                    }
                    board.restorePreviousTurn(backUpMove);
                    enemyTeam.add(board.findBy(attackingPiece));
                }
            } catch (ChessException e) {
                // trajectory is incorrect
            }
        }

        return answer;
    }

    // +
    public void updateTeams() {
        whiteTeam = board.getTeamBy(Color.White);
        blackTeam = board.getTeamBy(Color.Black);
    }

    // +
    public List<Piece> getTeamBy(Color teamColor) {
        updateTeams();
        return teamColor.equals(Color.White) ? whiteTeam : blackTeam;
    }

//     todo move these 2 methods to DrawChecker class,
//      because it will be correct,
//      and now it works incorrect I assume
    public boolean checkForDraw(Color teamColor) {
        List<Piece> team = getTeamBy(teamColor);
        int figuresCannotMove = 0;

        for (Piece piece : team) {
            if ( ! canMakeAnyMove(piece)) {
                figuresCannotMove++;
            }
        }

        return figuresCannotMove == team.size() && ! isKingUnderAttack(teamColor);
    }

    public boolean canMakeAnyMove(Piece piece) {
        Movement movement;
        int canMove = 0;
        Piece king = getKingBy(piece.getColor());
        assert king != null;
        Position currentPosition = board.getPositionOfPiece(piece);
        List<Position> cagesAround = piece instanceof Knight ?
                currentPosition.getPositionsAroundKnight() :
                currentPosition.getPositionsAround();

        for (Position cageAround : cagesAround) {
            try {
                movement = new Movement(currentPosition, cageAround);

                boolean canBeat = piece.isTrajectoryValid(movement)
                        && board.isDistanceFree(movement)
                        && ! board.isCageEmpty(board.findBy(cageAround))
                        && ! piece.getColor().equals(board.findBy(cageAround).getColor());
                boolean canMoveToEmptyCage = piece.isFightTrajectoryValid(movement)
                        && board.isDistanceFree(movement)
                        && board.isCageEmpty(board.findBy(cageAround));
                MovementHistory backUpMove = new MovementHistory(movement, piece, board.findBy(cageAround));
                if ( canBeat | canMoveToEmptyCage ) {
                    if (canBeat) {
                        board.beatFigure(currentPosition, cageAround);
                    } else {
                        board.swapFigures(currentPosition, cageAround);
                    }
                    if ( ! isKingUnderAttack(king.getColor())) {
                        canMove++;
                    }
                    board.restorePreviousTurn(backUpMove);
                }

            } catch (ChessException ignored) { }
        }

        return canMove > 0;
    }

    public List<Position> getEmptyPositionsAround(Position position) {
        List<Position> cagesAround = position.getPositionsAround();
        List<Position> freeCages = new ArrayList<>(cagesAround.size());

        for (Position place : cagesAround) {
            if ( board.isCageEmpty(board.findBy(place)) ) {
                freeCages.add(place);
            }
        }

        return freeCages;
    }
}
