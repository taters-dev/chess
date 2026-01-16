package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    public PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        ChessPiece piece = board.getPiece(myPosition);
        System.out.println((piece));
        if(piece == null){
            throw new RuntimeException("NULL");
        }
        if(piece.getPieceType() == PieceType.PAWN){
            PawnPiece pawn = new PawnPiece(pieceColor);
            return pawn.validMoves(board, myPosition);
        }
        else if(piece.getPieceType() == PieceType.ROOK){
            RookPiece rook = new RookPiece(pieceColor);
            return rook.validMoves(board, myPosition);
        }
        else if(piece.getPieceType() == PieceType.KNIGHT){
            KnightPiece knight = new KnightPiece(pieceColor);
            return knight.validMoves(board, myPosition);
        }
        else if(piece.getPieceType() == PieceType.BISHOP){
            BishopPiece bishop = new BishopPiece(pieceColor);
            return bishop.validMoves(board, myPosition);
        }
        else if(piece.getPieceType() == PieceType.QUEEN){
            QueenPiece queen = new QueenPiece(pieceColor);
            return queen.validMoves(board, myPosition);
        }
        else if(piece.getPieceType() == PieceType.KING){
            KingPiece king = new KingPiece(pieceColor);
            return king.validMoves(board, myPosition);
        }
        throw new RuntimeException("Didn't Match any piece");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return this.pieceColor == that.pieceColor && this.type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
