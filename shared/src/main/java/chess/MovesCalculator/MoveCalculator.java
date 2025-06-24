package chess.MovesCalculator;

import chess.*;

import java.util.HashSet;

public class MoveCalculator {
    static boolean isValid(ChessPosition position){
        return (position.getRow() >= 1 && position.getRow() <= 8) &&
                (position.getColumn() >= 1 && position.getColumn() <= 8);
    }

    static HashSet<ChessMove> directionalMovmement(ChessBoard board, ChessPosition position, int[][] possibleMoves,
                                                   int yPos, int xPos, ChessGame.TeamColor team){
        HashSet<ChessMove> moves = new HashSet<>();
        for(int[] direction:possibleMoves){
            boolean blocked = false;
            int i = 1;
            while(!blocked){
                ChessPosition newPosition = new ChessPosition(yPos + direction[1]*i, xPos + direction[0]*i);
                //check if on board
                //check if there is movement
                //check if captured piece
                //check if blocked by own piece
            }
            i++;
        }
        return moves;
    }

}
