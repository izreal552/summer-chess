package ui;


import model.GameData;

import java.util.*;


public class ChessClient {
    ServerFacade server;
    private String currentUser;
    private final String serverUrl;
    private final ChessREPL chessREPL;
    private static ChessState ChessState = ui.ChessState.LOGGED_OUT;
    private final List<GameData> games = new ArrayList<>();



    public ChessClient(String serverUrl, ChessREPL chessREPL){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.chessREPL = chessREPL;
    }

    public static ChessState getState() {
        return ChessState;
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

    private String logout() throws Exception {
        assertSignedIn();
        server.logout();
        currentUser = null;
        ChessState = ui.ChessState.LOGGED_OUT;
        return ("logged out");
    }

    private String register(String[] params) {
        if (params.length != 3) {
            System.out.println("Invalid Command");
            return "register <USERNAME> <PASSWORD> <EMAIL>";
        }
        if (server.register(params[0], params[1], params[2])) {
            ChessState = ui.ChessState.LOGGED_IN;
            currentUser = params[0];
            return "Registered and logged in as " + currentUser;
        }
        return "Registration failed. Username may already be taken.";
    }

    private String login(String[] params){
        if (params.length != 2) {
            System.out.println("Invalid Command");
            return "login <USERNAME> <PASSWORD>";
        }
        if (server.login(params[0], params[1])) {
            ChessState = ui.ChessState.LOGGED_IN;
            currentUser = params[0];
            return "Logged in as: " + currentUser;
        }
        return "Login failed. Check your username or password.";
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
