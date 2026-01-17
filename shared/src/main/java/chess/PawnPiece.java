package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnPiece {
    private ChessGame.TeamColor color;

    public PawnPiece(ChessGame.TeamColor color) {
        this.color = color;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        /*
            For every move it needs to check:
            1. Is it the pawns initial move? (White row 2, Black Row 7)
            2. Is the square in front open? Is the square in front of that open? (If initial move)
            3. Are there enemy pieces in the diagonal
            4. Does the move trigger a promotion piece? (White row 7, Black Row 2)

         */
        int row = position.getRow();
        int col = position.getColumn();
        boolean twoOpen;
        boolean leftDiagonal;
        boolean rightDiagonal;

        if (color == ChessGame.TeamColor.WHITE) {
            // Promotion
            if (row == 7) {
            }

            // Captures

            // Initial
            if (row == 2) {
                int i = 1;
                while (i <= 2) {
                    ChessPiece next = board.getPiece(new ChessPosition(row + i, col));
                    if (next == null || next.getTeamColor() != ChessGame.TeamColor.WHITE) {
                        ChessMove valid = new ChessMove(position, new ChessPosition(row + i, col), null);
                        moves.add(valid);
                    } else {
                        break;
                    }
                    i++;
                }
            }
        }
        return moves;
    }

    public boolean isSpaceInFrontOpen(ChessBoard board, ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();

        if(color == ChessGame.TeamColor.WHITE) {
            ChessPiece next = board.getPiece(new ChessPosition(row + 1, col));
            return next == null || next.getTeamColor() != color;
        }

        else if(color == ChessGame.TeamColor.BLACK){
            ChessPiece next = board.getPiece(new ChessPosition(row - 1, col));
            return next == null || next.getTeamColor() != color;
        }
        return true;
    }
    public boolean isSpaceCaptureable(ChessBoard board, ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();

        if(color == ChessGame.TeamColor.WHITE){
            if(col == 1){

            }
            if(col == 8){

            }
        }
        if(color == ChessGame.TeamColor.BLACK){
        }
        return true;
    }

    @Override
    public String toString() {
        return "PawnPiece{" +
                "color=" + color +
                '}';
    }
}
