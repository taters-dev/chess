package chess;

import java.util.*;

public class RookPiece {
    private ChessGame.TeamColor color;
    public RookPiece(ChessGame.TeamColor color){
        this.color = color;
    }

    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();
        return moves;
    }
}
