package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamColor;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {

        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return null;
        }

        HashSet<ChessMove> validMovement = new HashSet<>();
        HashSet<ChessMove> possibleMovement = new HashSet<>(piece.pieceMoves(board, startPosition));

        for(ChessMove movement:possibleMovement){
            ChessPosition endPosition = movement.getEndPosition();
            ChessPiece capturedPiece = board.getPiece(endPosition);

            board.addPiece(startPosition, null);
            board.addPiece(endPosition, piece);

            if (!isInCheck(piece.getTeamColor())) {
                validMovement.add(movement);
            }

            board.addPiece(endPosition, capturedPiece);
            board.addPiece(startPosition, piece);

        }
        return validMovement;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if(piece == null){
            throw new InvalidMoveException("No piece at startPosition");
        }

        boolean isTeamTurn = getTeamTurn() == piece.getTeamColor();
        if(!isTeamTurn){
            throw new InvalidMoveException("Not piece's turn");
        }

        Collection<ChessMove> movement = validMoves(startPosition);
        boolean isValidMovement = movement.contains(move);
        if(movement == null || !isValidMovement){
            throw new InvalidMoveException("No valid moves");
        }

        ChessPiece newPiece = piece;
        if(move.getPromotionPiece() != null){
            newPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(startPosition, null);
        board.addPiece(endPosition, newPiece);

        setTeamTurn((getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE));

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
