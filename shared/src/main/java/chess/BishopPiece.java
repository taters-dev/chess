package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopPiece {
    private ChessGame.TeamColor color;
    public BishopPiece(ChessGame.TeamColor color){
        this.color = color;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        // Up Right
        int i = 1;
        while((row + i < 9) && (col + i < 9)){
            ChessPosition nextPos = new ChessPosition(row + i, col + i);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != color){
                moves.add(new ChessMove(position, nextPos, null));
            }
            if(nextPiece != null){
                break;
            }
            i++;
        }

        // Up Left
        i = 1;
        while((row + i < 9) && (col - i > 0)){
            ChessPosition nextPos = new ChessPosition(row + i, col - i);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != color){
                moves.add(new ChessMove(position, nextPos, null));
            }
            if(nextPiece != null){
                break;
            }
            i++;
        }

        // Down Right
        i = 1;
        while((row - i > 0) && (col + i < 9)){
            ChessPosition nextPos = new ChessPosition(row - i, col + i);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != color){
                moves.add(new ChessMove(position, nextPos, null));
            }
            if(nextPiece != null){
                break;
            }
            i++;
        }

        // Down Left
        i = 1;
        while((row - i > 0) && (col - i > 0)){
            ChessPosition nextPos = new ChessPosition(row - i, col - i);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != color){
                moves.add(new ChessMove(position, nextPos, null));
            }
            if(nextPiece != null){
                break;
            }
            i++;
        }

        return moves;
    }
}
