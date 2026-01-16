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
        return moves;
    }
}
