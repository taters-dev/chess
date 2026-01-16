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
        return moves;
    }
}
