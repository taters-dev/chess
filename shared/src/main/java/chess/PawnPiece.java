package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnPiece {
    private ChessGame.TeamColor color;

    public PawnPiece(ChessGame.TeamColor color) {
        this.color = color;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position) {
        /*
            For every move it needs to check:
            1. Is it the pawns initial move? (White row 2, Black Row 7)
            2. Is the square in front open? Is the square in front of that open? (If initial move)
            3. Are there enemy pieces in the diagonal
            4. Does the move trigger a promotion piece? (White row 7, Black Row 2)

         */
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        if(color == ChessGame.TeamColor.WHITE){
            if(row == 7){
                if(col + 1 < 9){
                    ChessPosition newPos = new ChessPosition(row + 1, col + 1);
                    ChessPiece newPiece = board.getPiece(newPos);
                    if(newPiece != null && newPiece.getTeamColor() != color){
                        moves.addAll(promotionalMoves(position, newPos));
                    }
                }
                if(col - 1 > 0){
                    ChessPosition newPos = new ChessPosition(row + 1, col - 1);
                    ChessPiece newPiece = board.getPiece(newPos);
                    if(newPiece!= null && newPiece.getTeamColor() != color){
                        moves.addAll(promotionalMoves(position, newPos));
                    }
                }

                ChessPosition newPos = new ChessPosition(row + 1, col);
                ChessPiece newPiece = board.getPiece(newPos);

                if(newPiece == null){
                    moves.addAll(promotionalMoves(position, newPos));
                }
                return moves;
            }

            else{
                ChessPosition nextPos = new ChessPosition(row + 1, col);
                ChessPiece nextPiece = board.getPiece(nextPos);

                if(nextPiece == null){
                    moves.add(new ChessMove(position, nextPos, null));
                    ChessPosition nextNextPos = new ChessPosition(row + 2, col);
                    ChessPiece nextNextPiece = board.getPiece(nextNextPos);
                    if(row == 2 && nextNextPiece == null){
                        moves.add(new ChessMove(position, nextNextPos, null));
                    }
                }

                if(row + 1 < 9 && col + 1 < 9){
                    nextPos = new ChessPosition(row + 1, col + 1);
                    nextPiece = board.getPiece(nextPos);
                    if(nextPiece != null && nextPiece.getTeamColor() != color){
                        moves.add(new ChessMove(position, nextPos, null));
                    }
                }

                if(row + 1 < 9 && col - 1 > 0){
                    nextPos = new ChessPosition(row + 1, col - 1);
                    nextPiece = board.getPiece(nextPos);
                    if(nextPiece != null && nextPiece.getTeamColor() != color){
                        moves.add(new ChessMove(position, nextPos, null));
                    }
                }

            }
        }

        if(color == ChessGame.TeamColor.BLACK){
            if(row == 2){
                if(col + 1 < 9){
                    ChessPosition newPos = new ChessPosition(row - 1, col + 1);
                    ChessPiece newPiece = board.getPiece(newPos);
                    if(newPiece != null && newPiece.getTeamColor() != color){
                        moves.addAll(promotionalMoves(position, newPos));
                    }
                }
                if(col - 1 > 0){
                    ChessPosition newPos = new ChessPosition(row - 1, col - 1);
                    ChessPiece newPiece = board.getPiece(newPos);
                    if(newPiece != null && newPiece.getTeamColor() != color){
                        moves.addAll(promotionalMoves(position, newPos));
                    }
                }

                ChessPosition newPos = new ChessPosition(row - 1, col);
                ChessPiece newPiece = board.getPiece(newPos);

                if(newPiece == null){
                    moves.addAll(promotionalMoves(position, newPos));
                }
                return moves;
            }

            else{
                ChessPosition nextPos = new ChessPosition(row - 1, col);
                ChessPiece nextPiece = board.getPiece(nextPos);

                if(nextPiece == null){
                    moves.add(new ChessMove(position, nextPos, null));
                    ChessPosition nextNextPos = new ChessPosition(row - 2, col);
                    ChessPiece nextNextPiece = board.getPiece(nextNextPos);
                    if(row == 7 && nextNextPiece == null){
                        moves.add(new ChessMove(position, nextNextPos, null));
                    }
                }
                if(row - 1 > 0 && col + 1 < 9){
                    nextPos = new ChessPosition(row - 1, col + 1);
                    nextPiece = board.getPiece(nextPos);
                    if(nextPiece != null && nextPiece.getTeamColor() != color){
                        moves.add(new ChessMove(position, nextPos, null));
                    }
                }

                if(row - 1 > 0 && col - 1 > 0){
                    nextPos = new ChessPosition(row - 1, col - 1);
                    nextPiece = board.getPiece(nextPos);
                    if(nextPiece != null && nextPiece.getTeamColor() != color){
                        moves.add(new ChessMove(position, nextPos, null));
                    }
                }
            }
        }

        return moves;
    }

    public Collection<ChessMove> promotionalMoves(ChessPosition startPos, ChessPosition endPos){
        Collection<ChessMove> moves = new ArrayList<>();
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.QUEEN));
        return moves;
    }

    @Override
    public String toString() {
        return "PawnPiece{" +
                "color=" + color +
                '}';
    }
}
