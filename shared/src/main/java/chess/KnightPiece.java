package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightPiece {
    private ChessGame.TeamColor color;
    public KnightPiece(ChessGame.TeamColor color){
        this.color = color;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        // UP
        if (row + 2 < 9){
            if(col + 1 < 9){
                ChessPosition nextPos = new ChessPosition(row + 2, col + 1);
                ChessPiece nextPiece = board.getPiece(nextPos);
                if(nextPiece == null || nextPiece.getTeamColor() != color){
                    moves.add(new ChessMove(position, nextPos, null));
                }
            }

            if(col - 1 > 0 ){
                ChessPosition nextPos = new ChessPosition(row + 2, col - 1);
                ChessPiece nextPiece = board.getPiece(nextPos);
                if(nextPiece == null || nextPiece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, nextPos, null));
                }
            }
        }

        // DOWN
        if (row - 2 > 0){
            if(col + 1 < 9){
                ChessPosition nextPos = new ChessPosition(row - 2, col + 1);
                ChessPiece nextPiece = board.getPiece(nextPos);
                if(nextPiece == null || nextPiece.getTeamColor() != color){
                    moves.add(new ChessMove(position, nextPos, null));
                }
            }

            if(col - 1 > 0 ){
                ChessPosition nextPos = new ChessPosition(row - 2, col - 1);
                ChessPiece nextPiece = board.getPiece(nextPos);
                if(nextPiece == null || nextPiece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, nextPos, null));
                }
            }
        }


        // LEFT
        if (col - 2 > 0){
            if(row + 1 < 9){
                ChessPosition nextPos = new ChessPosition(row + 1, col - 2);
                ChessPiece nextPiece = board.getPiece(nextPos);
                if(nextPiece == null || nextPiece.getTeamColor() != color){
                    moves.add(new ChessMove(position, nextPos, null));
                }
            }

            if(row - 1 > 0 ){
                ChessPosition nextPos = new ChessPosition(row - 1, col - 2);
                ChessPiece nextPiece = board.getPiece(nextPos);
                if(nextPiece == null || nextPiece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, nextPos, null));
                }
            }
        }

        // RIGHT

        if (col + 2 < 9){
            if(row + 1 < 9){
                ChessPosition nextPos = new ChessPosition(row + 1, col + 2);
                ChessPiece nextPiece = board.getPiece(nextPos);
                if(nextPiece == null || nextPiece.getTeamColor() != color){
                    moves.add(new ChessMove(position, nextPos, null));
                }
            }

            if(row - 1 > 0 ){
                ChessPosition nextPos = new ChessPosition(row - 1, col + 2);
                ChessPiece nextPiece = board.getPiece(nextPos);
                if(nextPiece == null || nextPiece.getTeamColor() != color) {
                    moves.add(new ChessMove(position, nextPos, null));
                }
            }
        }

        return moves;
    }
}
