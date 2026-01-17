package chess;

import java.util.*;

public class RookPiece {
    private ChessGame.TeamColor color;
    public RookPiece(ChessGame.TeamColor color){
        this.color = color;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        // Back
        int i = 1;
        while(row - i > 0){
            ChessPosition nextPos = new ChessPosition(row - i, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != color){
                moves.add(new ChessMove(position, nextPos, null));
            }
            if(nextPiece != null){
                break;
            }
            i++;
        }

        // Forward
        i = 1;
        while(row + i < 9){
            ChessPosition nextPos = new ChessPosition(row + i, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != color){
                moves.add(new ChessMove(position, nextPos, null));
            }
            if(nextPiece != null){
                break;
            }
            i++;
        }

        // Right
        i = 1;
        while(col + i < 9){
            ChessPosition nextPos = new ChessPosition(row, col + i);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != color){
                moves.add(new ChessMove(position, nextPos, null));
            }
            if(nextPiece != null){
                break;
            }
            i++;
        }

        // Left
        i = 1;
        while(col - i > 0){
            ChessPosition nextPos = new ChessPosition(row, col - i);
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
