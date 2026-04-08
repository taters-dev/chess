package client;

import chess.*;
import client.websocket.ServerMessageHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.GameData;
import ui.EscapeSequences;
import java.util.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private WebSocketFacade webSocketFacade;
    private ServerMessageHandler serverMessageHandler;
    private State state = State.SIGNEDOUT;
    private String authToken;
    private Map<Integer, GameData> mapOfGames = new HashMap();
    private int port;
    private int gameID;
    private ChessGame.TeamColor teamColor;
    private ChessGame chessGame;

    public ChessClient(int port) throws  Exception{
        serverFacade = new ServerFacade(port);
        this.port = port;
        this.teamColor = ChessGame.TeamColor.WHITE;
    }

    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public void run() {
        System.out.println("Welcome to chess. Type help to get started");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!"quit".equals(result)){
            printPrompt();
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.println(result);
                System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE);
            } catch(Throwable e){
                System.out.println(e.toString());
            }
        }
    }

    public void printPrompt(){
        if(state.equals(State.SIGNEDOUT)){
            System.out.print("[LOGGEDOUT] >>>");
        }
        if(state.equals(State.SIGNEDIN)){
            System.out.print("[LOGGEDIN] >>>");
        }
        if(state.equals(State.INGAME)){
            System.out.print("[INGAME] >>>");
        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help(); case "quit" -> "quit"; case "login" -> login(params); case "register" -> register(params);
                case "logout" -> logout(); case "clear" -> clear(); case "create" -> createGame(params); case "list" -> listGames();
                case "join" -> joinGame(params); case "observe" -> observeGame(params); case "redraw" -> redraw(); case "leave" -> leave();
                case "move" -> move(params); case "resign" -> resign(); case "highlight" -> highlight(params); default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        if(state == State.SIGNEDOUT){

            return EscapeSequences.SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>" +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - to create an account\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>" +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - to play chess\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "quit" +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - playing chess\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "help" +
                            EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- with possible commands\n";
        }
        else if(state == State.SIGNEDIN){
            return EscapeSequences.SET_TEXT_COLOR_BLUE + "create <NAME>" +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - a game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "list" +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - games\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]" +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - a game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "observe <ID>" +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- a game\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "logout " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- when you are done\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "quit " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- playing chess\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "help " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- with possible commands\n";
        }
        else{
            return
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "help " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- with possible commands\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "redraw " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- redraw board upon request\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "leave " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- return to lobby\n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "move <START> <END>" +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- make your move \n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "resign " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- do you want to forfeit the game [Y|N]? \n" +
                    EscapeSequences.SET_TEXT_COLOR_BLUE + "highlight <POSITION> " +
                    EscapeSequences.SET_TEXT_COLOR_MAGENTA + "- highlight all legal moves\n";

        }
    }

    public String register(String ... params) throws ResponseException{
        assertSignedOut();
        if(params.length == 3){
            String username = params[0];
            String password = params[1];
            String email = params[2];

            var registerResult = serverFacade.register(username, password, email);
            authToken = registerResult.authToken();

            state = State.SIGNEDIN;
            return "Successfully Registered. Welcome to chess " + username;
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
        }
    }

    public String login(String ... params) throws ResponseException{
        assertSignedOut();
        if(params.length == 2){
            String username = params[0];
            String password = params[1];

            var loginResult = serverFacade.login(username, password);
            authToken = loginResult.authToken();

            state=State.SIGNEDIN;
            return "Successful Login. Welcome to chess " + username;
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
        }
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        serverFacade.logout(authToken);
        authToken = null;
        state = State.SIGNEDOUT;
        return "Successfully logged out";
    }

    public String createGame(String ... params) throws ResponseException{
        assertSignedIn();
        if(params.length == 1){
            String gameName = params[0];

            serverFacade.createGame(authToken, gameName);
            return "Game created: " + gameName;
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <NAME> ");
        }
    }

    public String listGames() throws ResponseException{
        assertSignedIn();
        var listGamesResult = serverFacade.listGames(authToken);
        var games = listGamesResult.games();
        String returnVal = "";
        int i = 1;
        for(var game : games){
            mapOfGames.put(i, game);
            returnVal = returnVal.concat(i + " - Game Name: " + game.gameName() + "\n     White Player: " + game.whiteUsername()
            + " \n     Black Player: " + game.blackUsername() + "\n\n");
            i++;
        }
        if(returnVal.isEmpty()){
            return "No games found.";
        }
        return returnVal;
    }

    public String joinGame(String ... params) throws Exception{
        assertSignedIn();
        if(params.length == 2){
            var key = params[0];

            try{
                Integer.parseInt(key);
            }catch (NumberFormatException e){
                throw new ResponseException(ResponseException.Code.ClientError, "Enter a valid gameID");
            }

            var game = mapOfGames.get(Integer.parseInt(key));

            if(game == null){
                throw new ResponseException(ResponseException.Code.ClientError, "Enter a valid gameID");
            }

            serverFacade.joinGame(authToken, params[1].toUpperCase(), game.gameID());

            this.serverMessageHandler = new ServerMessageHandler(this);
            this.webSocketFacade = new WebSocketFacade(port, serverMessageHandler);

            webSocketFacade.connect(authToken, game.gameID());

            if(params[1].toUpperCase().equals("WHITE")){
                teamColor = ChessGame.TeamColor.WHITE;
            }
            else{
                teamColor = ChessGame.TeamColor.BLACK;
            }

            gameID = game.gameID();
            state = State.INGAME;
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> [WHITE|BLACK]");
        }
        return "Game Joined!";
    }

    public String observeGame(String ... params) throws Exception{
        assertSignedIn();
        if(params.length == 1){
            var key = params[0];

            try{
                Integer.parseInt(key);
            }catch (NumberFormatException e){
                throw new ResponseException(ResponseException.Code.ClientError, "Enter a valid gameID");
            }

            var game = mapOfGames.get(Integer.parseInt(key));

            if(game == null){
                throw new ResponseException(ResponseException.Code.ClientError, "Enter a valid gameID");
            }

            serverMessageHandler = new ServerMessageHandler(this);
            webSocketFacade = new WebSocketFacade(port, serverMessageHandler);

            webSocketFacade.connect(authToken, game.gameID());
            state = State.INGAME;
            teamColor = null;
            gameID = game.gameID();

        }
        return "Observing Game";
    }

    public String clear() throws ResponseException{
        serverFacade.clear();
        return "Clearing Server";
    }

    public void drawBoard(ChessGame.TeamColor teamColor, Collection validMoves, ChessPosition highlightedPos){
        List<Character> columns = new ArrayList<>(List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' ));
        List<Integer> rows = new ArrayList<>(List.of(8, 7, 6, 5, 4, 3, 2, 1));
        ChessBoard chessBoard = chessGame.getBoard();

        if(teamColor == ChessGame.TeamColor.BLACK){
            Collections.reverse(columns);
            Collections.reverse(rows);
        }

        System.out.println();
        printColumnLabels(columns);

        for(int i = 0; i < 8; i++){
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY+ ' ');
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + rows.get(i));
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY+ ' ');

            for(int j = 0; j < 8; j++){
                ChessPosition currPosition = new ChessPosition(rows.get(i), columns.get(j) - 'a' + 1);

                if((i + j) % 2 == 0){

                    if(validMoves != null && validMoves.contains(currPosition)){
                        System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                    }
                    else if(currPosition.equals(highlightedPos)){
                        System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                    }
                    else{
                        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                    }
                }
                else{
                    if(validMoves != null && validMoves.contains(currPosition)){
                        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    }
                    else if(currPosition.equals(highlightedPos)){
                        System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                    }
                    else{
                        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                    }
                }


                ChessPiece currPiece = chessBoard.getPiece(currPosition);
                if(currPiece != null){
                    drawCorrectCharacter(currPiece);
                }
                else{
                    System.out.print("   ");
                }

            }

            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY+ ' ');
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + rows.get(i));
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY+ ' ');
            System.out.println(EscapeSequences.RESET_BG_COLOR);

        }

        printColumnLabels(columns);
    }

    public void drawCorrectCharacter(ChessPiece piece){

        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        }
        else{
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        }

        switch (piece.getPieceType()){
            case KING -> System.out.print(" K "); case PAWN -> System.out.print(" P "); case ROOK -> System.out.print(" R ");
            case QUEEN -> System.out.print(" Q "); case BISHOP -> System.out.print(" B "); case KNIGHT -> System.out.print(" N ");
        }
    }

    public String redraw() throws Exception{
        assertInGame();
        drawBoard(teamColor, null, null);
        return "Board Redrawn";
    }

    public String leave() throws Exception{
        assertInGame();
        webSocketFacade.leave(authToken, gameID);
        state = State.SIGNEDIN;
        chessGame = null;
        gameID = 0;
        teamColor = null;
        return "Returned to lobby";
    }

    public String move(String ... params) throws  Exception{
        assertInGame();
        if(params.length > 2 || params.length < 2){
            return "Expected: <START> <END>";
        }
        char startRowChar= params[0].charAt(1);
        char startColumnChar = params[0].charAt(0);
        char endRowChar = params[1].charAt(1);
        char endColumnChar = params[1].charAt(0);

        int startRow = Character.getNumericValue(startRowChar);
        int startColumn = startColumnChar - 'a' + 1;
        int endRow = Character.getNumericValue(endRowChar);
        int endColumn = endColumnChar - 'a' + 1;

        if(startRow < 1 || startRow > 8 || startColumn < 1 || startColumn > 8 || endRow < 1 || endRow > 8 ||
                endColumn < 1 || endColumn >  8){
            return "Please valid positions on the board";
        }

        ChessPosition startPosition = new ChessPosition(startRow, startColumn);
        ChessPosition endPosition = new ChessPosition(endRow, endColumn);
        ChessMove chessMove;

        if(chessGame.getBoard().getPiece(startPosition) == null){
            return "Please select a valid piece";
        }
        else if(chessGame.getBoard().getPiece(startPosition).getPieceType() == ChessPiece.PieceType.PAWN
                && (endPosition.getRow() == 1 || endPosition.getRow() == 8)){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Select your promotion piece");
            String nextLine = scanner.nextLine();
            Set<String> possiblePromotion = new HashSet<>(List.of("queen", "rook", "bishop", "knight"));
            while (!possiblePromotion.contains(nextLine.toLowerCase())){
                System.out.println("Please enter a valid promotion type");
                nextLine = scanner.nextLine();
            }
            ChessPiece.PieceType promotion = promotionHelp(nextLine.toLowerCase());
            chessMove = new ChessMove(startPosition, endPosition, promotion);
        }
        else{
            chessMove = new ChessMove(startPosition, endPosition, null);
        }
        webSocketFacade.makeMove(authToken, gameID, chessMove);
        return "";
    }

    public ChessPiece.PieceType promotionHelp(String nextLine){
        return switch(nextLine){
            case "queen" -> ChessPiece.PieceType.QUEEN; case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP; case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> ChessPiece.PieceType.PAWN;};
    }

    public String resign(String ... params) throws Exception{
        assertInGame();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Confirm if you want to forfeit [Y | N]");
        String nextLine = scanner.nextLine();
        if(nextLine.toLowerCase().equals("y") && nextLine.toLowerCase().equals("n")){
            throw new ResponseException(ResponseException.Code.ClientError, "Expected [Y | N]");
        }
        else if(nextLine.toLowerCase().equals("y")){
            webSocketFacade.resign(authToken, gameID);
            return "You forfeit the game";
        }
        else if( nextLine.toLowerCase().equals("n")){
            return "";
        }
        return "The game will continue";
    }

    public String highlight(String ... params){
        if(params.length != 1){
            return "Expected <POSITION>";
        }

        char rowChar= params[0].charAt(1);
        char columnChar = params[0].charAt(0);
        int row = Character.getNumericValue(rowChar);
        int column = columnChar - 'a' + 1;

        if(row < 1 || row > 8 || column < 1 || column > 8){
            return "Please a valid position on the board";
        }

        ChessPosition chessPosition = new ChessPosition(row, column);
        var validMoves = chessGame.validMoves(chessPosition);
        Set<ChessPosition> valid = new HashSet<>();

        for(ChessMove move : validMoves){
            valid.add(move.getEndPosition());
        }

        drawBoard(teamColor, valid, chessPosition);

        return "";
    }

    public void printColumnLabels(List<Character> columns){
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "   ");
        for(int i = 0; i < 8; i++){
            System.out.print(" " +columns.get(i) + " ");
        }
        System.out.print("   ");
        System.out.println(EscapeSequences.RESET_BG_COLOR);
    }

    private void assertSignedIn() throws ResponseException {
        if (state != State.SIGNEDIN) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
    private void assertSignedOut() throws ResponseException {
        if(state != State.SIGNEDOUT){
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign out first");
        }
    }

    private void assertInGame() throws Exception{
        if(state != State.INGAME){
            throw new ResponseException(ResponseException.Code.ClientError, "You must be in game");
        }
    }
}