package ui;


import model.GameData;

import java.util.*;


public class ChessClient {
    ServerFacade server;
    private String username;
    private final String serverUrl;
    private final ChessREPL chessREPL;
    private final ChessState ChessState = ui.ChessState.LOGGED_OUT;
    private final List<GameData> games = new ArrayList<>();



    public ChessClient(String serverUrl, ChessREPL chessREPL){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.chessREPL = chessREPL;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> createGame();
                case "list" -> listGames(params);
                case "join" -> joinGame();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String joinGame() {
        return null;
    }

    private String listGames(String[] params) {
        return null;
    }

    private String createGame() {
        return null;
    }

    private String logout() {
        return null;
    }

    private String register(String[] params) {
    return null;
    }

    private String login(String[] params) throws Exception{

        return null;
    }


    public String help() {
        if (ChessState == ui.ChessState.LOGGED_OUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - Create a new account
                    - login <USERNAME> <PASSWORD> - Login to your account
                    - help - Show this help message
                    - quit - Exit the application
                    """;
        }
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE|BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - help - with possible commands
                - quit - playing chess
                """;
    }

    private void assertSignedIn() throws Exception {
        if (ChessState == ui.ChessState.LOGGED_OUT) {
            throw new Exception("Error: You must sign in");
        }
    }
    
}
