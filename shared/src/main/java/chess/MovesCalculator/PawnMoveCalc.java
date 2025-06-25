package chess.MovesCalculator;

import chess.*;

import java.util.HashSet;

public class PawnMoveCalc {
    public static HashSet<ChessMove> getMove(ChessBoard board, ChessPosition pos){
        int xPos = pos.getColumn();
        int yPos = pos.getRow();
        ChessPiece.PieceType[] promotionPiece = new ChessPiece.PieceType[]{null};
        ChessGame.TeamColor teamColor = board.getPiece(pos).getTeamColor();

        int moveForward;
        if(teamColor == ChessGame.TeamColor.WHITE){
            moveForward = 1;
        }
        else{
            moveForward = -1;
        }

        //determine promotion
        boolean promote = (teamColor == ChessGame.TeamColor.WHITE && yPos == 7) ||
                (teamColor == ChessGame.TeamColor.BLACK && yPos == 2);
        if(promote){
            promotionPiece = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.QUEEN};
        }
        //determine start position
        boolean start = (teamColor == ChessGame.TeamColor.WHITE && yPos == 2) ||
                (teamColor == ChessGame.TeamColor.BLACK && yPos == 7);


        HashSet<ChessMove> moves = new HashSet<>();

        for(ChessPiece.PieceType promotion: promotionPiece){
            //derermine capture cases
            ChessPosition forwardProsition = new ChessPosition(yPos + moveForward, xPos);
            ChessPosition doubleForward = new ChessPosition(yPos + moveForward * 2, xPos);
            ChessPosition rightCapture = new ChessPosition(yPos + moveForward, xPos + 1);
            ChessPosition leftCapture = new ChessPosition(yPos + moveForward, xPos - 1);
            //determine special movement

            if(MoveCalculator.isValid(doubleForward) &&
                    (start) &&
                    (board.getPiece(forwardProsition) == null) &&
                    (board.getPiece(doubleForward) == null)){
                moves.add(new ChessMove(pos, doubleForward, promotion));
            }
            if(MoveCalculator.isValid(forwardProsition) &&
                    (board.getPiece(forwardProsition) == null)){
                moves.add(new ChessMove(pos, forwardProsition, promotion));
            }
            if(MoveCalculator.isValid(rightCapture) &&
                    (board.getPiece(rightCapture) != null) &&
                    (board.getPiece(rightCapture).getTeamColor()) != teamColor){
                moves.add(new ChessMove(pos, rightCapture, promotion));
            }
            if(MoveCalculator.isValid(leftCapture) &&
                    (board.getPiece(leftCapture) != null) &&
                    (board.getPiece(leftCapture).getTeamColor()) != teamColor){
                moves.add(new ChessMove(pos, leftCapture, promotion));
            }
        }
    return moves;
    }
}
