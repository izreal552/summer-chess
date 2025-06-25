package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightMoveCalc {
    public static HashSet<ChessMove> getMove(ChessBoard board, ChessPosition pos){
        int xPos = pos.getColumn();
        int yPos = pos.getRow();
        int[][] possibleMoves = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {-1,2}, {-1,-2}, {1,2}, {1,-2}};
        ChessGame.TeamColor teamColor = board.getPiece(pos).getTeamColor();

        return MoveCalculator.singleMovmement(board, pos, possibleMoves, yPos, xPos, teamColor);
    }
}
