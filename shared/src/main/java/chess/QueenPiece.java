package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenPiece {

    private ChessGame.TeamColor color;

    public QueenPiece(ChessGame.TeamColor color){
        this.color = color;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();
        RookPiece fakeRook = new RookPiece(color);
        BishopPiece fakeBishop = new BishopPiece(color);

        moves.addAll(fakeRook.validMoves(board, position));
        moves.addAll(fakeBishop.validMoves(board, position));
        return moves;
    }
}
