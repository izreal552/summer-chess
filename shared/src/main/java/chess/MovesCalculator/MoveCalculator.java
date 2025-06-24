package chess.MovesCalculator;

import chess.ChessPosition;

public class MoveCalculator {
    static boolean isValid(ChessPosition position){
        return (position.getRow() >= 1 && position.getRow() <= 8) &&
                (position.getColumn() >= 1 && position.getColumn() <= 8);
    }


}
