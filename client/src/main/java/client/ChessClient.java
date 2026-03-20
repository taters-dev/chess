package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import ui.EscapeSequences;

import java.util.*;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State state = State.SIGNEDOUT;
    private String authToken;
    private Map<Integer, GameData> mapOfGames = new HashMap();

    public ChessClient(int port){
        serverFacade = new ServerFacade(port);
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
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "clear" -> clear();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                default -> help();
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
        else{
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
        return returnVal;
    }

    public String joinGame(String ... params) throws ResponseException{
        assertSignedIn();
        if(params.length == 2){
            var key = params[0];
            var game = mapOfGames.get(Integer.parseInt(key));

            if(game == null){
                throw new ResponseException(ResponseException.Code.ClientError, "Enter a valid gameID");
            }

            serverFacade.joinGame(authToken, params[1].toUpperCase(), game.gameID());
            drawBoard(params[1].toUpperCase());
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> [WHITE|BLACK]");
        }
        return "Game Joined!";
    }

    public String observeGame(String ... params) throws ResponseException{
        assertSignedIn();
        if(params.length == 1){
            var key = params[0];
            var game = mapOfGames.get(Integer.parseInt(key));
            drawBoard("WHITE");

        }
        return "Observing Game";
    }

    public String clear() throws ResponseException{
        serverFacade.clear();
        return "Clearing Server";
    }

    public void drawBoard(String teamColor){
        List<Character> columns = new ArrayList<>(List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' ));
        List<Integer> rows = new ArrayList<>(List.of(8, 7, 6, 5, 4, 3, 2, 1));
        List<Character> backRow = new ArrayList<>(List.of('R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'));
        List<Character> frontRow = new ArrayList<>(List.of('P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'));

        // go through rows

        if(teamColor.equals("BLACK")){
            Collections.reverse(columns);
            Collections.reverse(rows);
            Collections.reverse(backRow);
        }

        printColumnLabels(columns);

        for(int i = 0; i < 8; i++){
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY+ ' ');
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + rows.get(i));
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY+ ' ');

            for(int j = 0; j < 8; j++){

                if((i + j) % 2 == 0){
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }
                else{
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                }

                if(rows.get(i) == 1){
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                    System.out.print(" " + backRow.get(j) + " ");
                }
                else if(rows.get(i) == 2){
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                    System.out.print(" " + frontRow.get(j) + " ");
                }
                else if(rows.get(i) == 7){
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
                    System.out.print(" " + frontRow.get(j) + " ");
                }
                else if(rows.get(i) == 8){
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
                    System.out.print(" " + backRow.get(j) + " ");
                }
                else{
                    System.out.print(EscapeSequences.EMPTY);
                }

            }

            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY+ ' ');
            System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + rows.get(i));
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY+ ' ');
            System.out.println(EscapeSequences.RESET_BG_COLOR);

        }

        printColumnLabels(columns);
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
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
    private void assertSignedOut() throws ResponseException {
        if(state == State.SIGNEDIN){
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign out first");
        }
    }

}
