package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private boolean gameOver = false;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
        this.gameOver = gameOver;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */

    public void setTeamTurn(TeamColor team) {
       this.teamTurn = team;
    }

    public boolean isGameOver(){
        return this.gameOver;
    }

    public void setGameOver(boolean result){
        this.gameOver = result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) && getTeamTurn() == chessGame.getTeamTurn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), getTeamTurn());
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamTurn=" + teamTurn +
                '}';
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
        ChessPiece currPiece = this.board.getPiece(startPosition);

        if (currPiece == null){
            return null;
        }
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> possibleMoves = currPiece.pieceMoves(this.board, startPosition);
        for(ChessMove move : possibleMoves){
            ChessPosition endPos = move.getEndPosition();
            ChessPiece endPiece = this.board.getPiece(endPos);
            this.board.addPiece(endPos, currPiece);
            this.board.addPiece(startPosition, null);

            boolean check = isInCheck(currPiece.getTeamColor());

            if(!check){
                validMoves.add(move);
            }

            this.board.addPiece(endPos, endPiece);
            this.board.addPiece(startPosition, currPiece);

        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        Collection<ChessMove> moves = validMoves(startPos);
        ChessPiece currPiece = this.board.getPiece(startPos);

        if(moves == null || this.teamTurn != currPiece.getTeamColor() || !moves.contains(move)){
            throw new InvalidMoveException("Not a valid move");
        }
        else{
            if(move.getPromotionPiece() != null){
                currPiece.type = move.getPromotionPiece();
            }
            this.board.addPiece(endPos, currPiece);
            this.board.addPiece(startPos, null);

            if(this.teamTurn == TeamColor.WHITE){
                this.teamTurn = TeamColor.BLACK;
            }

            else{
                this.teamTurn = TeamColor.WHITE;
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> oppPositions = new ArrayList<>();
        Collection<ChessMove> currMoves;
        ChessPosition kingPos = null;
        ChessPosition currPos;
        ChessPiece currPiece;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                currPos = new ChessPosition(row + 1, col + 1);
                currPiece = this.board.getPiece(currPos);
                if(currPiece != null){
                    if (currPiece.getTeamColor() != teamColor) {
                        oppPositions.add(currPos);
                    }
                    if(currPiece.type == ChessPiece.PieceType.KING && currPiece.getTeamColor() == teamColor){
                        kingPos = currPos;
                    }
                }
            }
        }

        for(ChessPosition pos : oppPositions){
            currPiece = board.getPiece(pos);
            currMoves = currPiece.pieceMoves(board, pos);
            for(ChessMove move : currMoves){
                if(move.getEndPosition().equals(kingPos)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }
        else {
            if(mateHelper(teamColor)){
                gameOver = true;
                return true;
            }
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        else{
            if(mateHelper(teamColor)){
                gameOver = true;
                return true;
            }
            return false;
        }
    }

    public boolean mateHelper(TeamColor teamColor){
        Collection<ChessMove> currMoves;
        int numberOfMoves = 0;
        ChessPosition currPos;
        ChessPiece currPiece;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                currPos = new ChessPosition(row + 1, col + 1);
                currPiece = this.board.getPiece(currPos);
                if (currPiece != null) {
                    if (currPiece.getTeamColor() == teamColor) {
                        currMoves = validMoves(currPos);
                        numberOfMoves += currMoves.size();
                    }
                }
            }
        }
        return numberOfMoves == 0;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
