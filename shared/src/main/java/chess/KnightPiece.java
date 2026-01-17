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
        return moves;
    }
}
