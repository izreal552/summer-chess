package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class QueenMoveCalc {
    public static HashSet<ChessMove> getMove(ChessBoard board, ChessPosition pos){
        int xPos = pos.getColumn();
        int yPos = pos.getRow();
        int[][] possibleMoves = {{1,1}, {-1,-1}, {1,-1}, {-1,1}, {0,1}, {1,0}, {-1,0}, {0,-1}};
        ChessGame.TeamColor teamColor = board.getPiece(pos).getTeamColor();

        return MoveCalculator.directionalMovmement(board, pos, possibleMoves, yPos, xPos, teamColor);
    }
}
