package chess.movescalculator;

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
                if(!isValid(newPosition)){
                    blocked = true;
                }
                //check if there is a piece at new location
                else if (board.getPiece(newPosition) == null){
                    moves.add(new ChessMove(position, newPosition,null));
                }
                //check if captured piece
                else if (board.getPiece(newPosition).getTeamColor() != team){
                    moves.add(new ChessMove(position, newPosition, null));
                    blocked = true;
                }
                //check if blocked by own piece
                else if (board.getPiece(newPosition).getTeamColor() == team){
                    blocked = true;
                }
                i++;
            }
        }
        return moves;
    }

    static HashSet<ChessMove> singleMovmement(ChessBoard board, ChessPosition position, int[][] possibleMoves,
                                                   int yPos, int xPos, ChessGame.TeamColor team) {
    HashSet<ChessMove> moves = new HashSet<>();
    for(int[] direction:possibleMoves){
        ChessPosition newPosition = new ChessPosition(yPos + direction[1], xPos + direction[0]);

        if(isValid(newPosition)){
            //check if there is a piece at new location
            if(board.getPiece(newPosition) == null){
                moves.add(new ChessMove(position, newPosition, null));
            }
            //check if captured piece
            else if (board.getPiece(newPosition).getTeamColor() != team) {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
    }
    return moves;
    }

}
