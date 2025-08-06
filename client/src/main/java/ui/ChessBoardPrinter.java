package ui;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class ChessBoardPrinter {
    private ChessGame game;

    public ChessBoardPrinter(ChessGame game) {
        this.game = game;
    }

    public void updateGame(ChessGame game) {
        this.game = game;
    }

    public void printBoard(ChessGame.TeamColor color, ChessPosition selectedPos) {
        boolean isBlackView = (color == ChessGame.TeamColor.BLACK);
        BoardLayout layout = new BoardLayout(isBlackView);

        StringBuilder output = new StringBuilder();
        output.append(SET_TEXT_BOLD);

        Collection<ChessMove> possibleMoves = selectedPos != null ? game.validMoves(selectedPos) : null;
        HashSet<ChessPosition> possibleSquares = new HashSet<>();
        if (possibleMoves != null) {
            for (ChessMove move : possibleMoves) {
                possibleSquares.add(move.getEndPosition());
            }
        }

        printColLabels(layout, output);

        for (int row : layout.rowOrder) {
            printRow(row, layout, selectedPos, possibleSquares, output);
        }

        printColLabels(layout, output);
        output.append(RESET_TEXT_BOLD_FAINT);

        out.println(output);
        out.printf("Turn: %s\n", game.getTeamTurn().toString());
    }

    private void printColLabels(BoardLayout boardlayout, StringBuilder output) {
        output.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_BLUE).append("   ");
        for (String col : boardlayout.colLabels) {
            output.append(" ").append(col).append(" ");
        }
        output.append("   ").append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append("\n");
    }

    private void printRow(int row, BoardLayout boardlayout, ChessPosition selectedPos,
                          HashSet<ChessPosition> possibleSquares, StringBuilder output) {
        output.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_BLUE);
        output.append(" ").append(row).append(" ");

        for (int col : boardlayout.colOrder) {
            ChessPosition position = new ChessPosition(row, col);
            boolean isDark = (row + col) % 2 == 0;
            boolean isSelected = position.equals(selectedPos);
            boolean isTarget = possibleSquares.contains(position);

            output.append(formatSquare(position, isDark, isSelected, isTarget));
        }

        output.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_BLUE);
        output.append(" ").append(row).append(" ").append(RESET_TEXT_COLOR).append(RESET_BG_COLOR).append("\n");
    }

    private String formatSquare(ChessPosition pos, boolean isDark, boolean isSelected, boolean isTarget) {
        StringBuilder sb = new StringBuilder();
        if (isSelected) {
            sb.append(SET_BG_COLOR_YELLOW);
        } else if (isTarget) {
            sb.append(SET_BG_COLOR_GREEN);
        } else {
            sb.append(isDark ? SET_BG_COLOR_RED : SET_BG_COLOR_LIGHT_GREY);
        }

        ChessPiece piece = game.getBoard().getPiece(pos);
        if (piece != null) {
            sb.append(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK);
            sb.append(" ").append(getPieceSymbol(piece)).append(" ");
        } else {
            sb.append("   ");
        }

        return sb.toString();
    }

    private String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };
    }

    private static class BoardLayout {
        final int[] rowOrder;
        final int[] colOrder;
        final String[] colLabels;

        BoardLayout(boolean isBlackView) {
            rowOrder = isBlackView ? new int[]{1, 2, 3, 4, 5, 6, 7, 8} : new int[]{8, 7, 6, 5, 4, 3, 2, 1};
            colOrder = isBlackView ? new int[]{8, 7, 6, 5, 4, 3, 2, 1} : new int[]{1, 2, 3, 4, 5, 6, 7, 8};
            colLabels = isBlackView ? new String[]{"h", "g", "f", "e", "d", "c", "b", "a"}
                    : new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        }
    }
}
