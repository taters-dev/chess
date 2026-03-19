package client;

import exception.ResponseException;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Scanner;

import static java.awt.Color.BLUE;

public class ChessClient {
    private final ServerFacade serverFacade;
    private State state = State.SIGNEDOUT;

    public ChessClient(int port){
        serverFacade = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to chess. Type help to get started");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
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

                /**
                case "login" -> login();
                case "register" -> register();

                case "logout" -> logout(params);
                case "create" -> createGame();
                case "list" -> lisGames(params);
                case "play" -> playGame(params);
                case "observe" -> oberserveGame(params);
                 **/
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

    public String register(){
        return "";
    }

    public String login(){
        return "";
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }

}
