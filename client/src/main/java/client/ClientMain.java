package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        int port = 8080;
        if (args.length == 1){
            port = Integer.parseInt(args[0]);
        }
        try{
            new ChessClient(port).run();
        }catch (Throwable ex){
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
