package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingPiece {

    private ChessGame.TeamColor color;
    public KingPiece(ChessGame.TeamColor color){
        this.color = color;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        // LEFT
        if(col - 1 > 0){
            ChessPiece next = board.getPiece(new ChessPosition(row, col - 1));
            if(next == null || next.getTeamColor() != color){
                moves.add(new ChessMove(position, new ChessPosition(row, col - 1), null));
            }
        }
        // RIGHT
        if(col + 1 < 9){
            ChessPiece next = board.getPiece(new ChessPosition(row, col + 1));
            if(next == null || next.getTeamColor() != color) {
                moves.add(new ChessMove(position, new ChessPosition(row, col + 1), null));
            }
        }
        // FRONT
        if(row + 1 < 9){
            ChessPiece next = board.getPiece(new ChessPosition(row + 1, col));
            if(next == null || next.getTeamColor() != color) {
                moves.add(new ChessMove(position, new ChessPosition(row + 1, col), null));
            }
        }
        // BACK
        if(row - 1 > 0){
            ChessPiece next = board.getPiece(new ChessPosition(row - 1, col));
            if(next == null || next.getTeamColor() != color) {
                moves.add(new ChessMove(position, new ChessPosition(row - 1, col), null));
            }
        }
        // DIAGONALS

        if(row - 1 > 0 && col - 1 > 0){
            ChessPiece next = board.getPiece(new ChessPosition(row - 1, col - 1));
            if(next == null || next.getTeamColor() != color) {
                moves.add(new ChessMove(position, new ChessPosition(row - 1, col - 1), null));
            }
        }

        if(row - 1 > 0 && col + 1 < 9){
            ChessPiece next = board.getPiece(new ChessPosition(row - 1, col + 1));
            if(next == null || next.getTeamColor() != color) {
                moves.add(new ChessMove(position, new ChessPosition(row - 1, col + 1), null));
            }
        }

        if(row + 1 < 9 && col + 1 < 9){
            ChessPiece next = board.getPiece(new ChessPosition(row + 1, col + 1));
            if(next == null || next.getTeamColor() != color) {
                moves.add(new ChessMove(position, new ChessPosition(row + 1, col + 1), null));
            }
        }

        if(row + 1 < 9 && col - 1 > 0){
            ChessPiece next = board.getPiece(new ChessPosition(row + 1, col - 1));
            if(next == null || next.getTeamColor() != color) {
                moves.add(new ChessMove(position, new ChessPosition(row + 1, col - 1), null));
            }
        }
        return moves;
    }
}
