package ui;


import chess.ChessGame;
import model.GameData;

import java.util.*;


public class ChessClient {
    ServerFacade server;
    private String currentUser;
    private final String serverUrl;
    private final ChessREPL chessREPL;
    private final ChessBoardPrinter boardPrinter;
    private static ChessState ChessState = ui.ChessState.LOGGED_OUT;
    private final List<GameData> games = new ArrayList<>();

    public ChessClient(String serverUrl, ChessREPL chessREPL){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.chessREPL = chessREPL;
        this.boardPrinter = new ChessBoardPrinter(null);
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
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String observeGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length != 1) {
            System.out.println("Invalid Command");
            return "Usage: observe <INDEX>";
        }

        int index;
        try {
            index = Integer.parseInt(params[0]);
        } catch (Exception e) {
            return "Invalid index: must be a number.";
        }

        if (index < 0 || index >= games.size()) {
            return "Invalid game index.";
        }

        GameData game = games.get(index);
        System.out.println("Observing game '" + game.gameName() + "':\n");
        boardPrinter.updateGame(game.game());
        boardPrinter.printBoard(ChessGame.TeamColor.WHITE, null);
        return "";

    }

    private String joinGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length != 2) {
            System.out.println("Invalid Command");
            return "join <INDEX> [WHITE|BLACK]";
        }

        int index;
        try {
            index = Integer.parseInt(params[0]);
        } catch (Exception e) {
            return "Invalid index: must be a number.";
        }

        if (index < 0 || index >= games.size()) {
            return "Invalid game index.";
        }

        String color = params[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return "Color must be WHITE or BLACK.";
        }

        GameData game = games.get(index);
        if (server.joinGame(game.gameID(), color)) {
            ChessGame.TeamColor teamColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            boardPrinter.updateGame(game.game());
            boardPrinter.printBoard(teamColor, null);
            return "Joined game '" + game.gameName() + "' as " + color + ".\n";
        }
        return "Failed to join game.";    }

    private String listGames() throws Exception {
        assertSignedIn();
        Set<GameData> serverList = server.listGames();

        for (GameData serverGame : serverList) {
            boolean found = false;

            for (int i = 0; i < games.size(); i++) {
                GameData localGame = games.get(i);

                if (localGame.gameID() == serverGame.gameID()) {
                    if (!Objects.equals(localGame.whiteUsername(), serverGame.whiteUsername()) ||
                            !Objects.equals(localGame.blackUsername(), serverGame.blackUsername())) {
                        games.set(i, serverGame);
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                games.add(serverGame); // Add new game to preserve order
            }
        }


        if (games.isEmpty()) {
            return "No games currently available.";
        }

        StringBuilder sb = new StringBuilder("Available Games:\n");
        for (int i = 0; i < games.size(); i++) {
            GameData g = games.get(i);
            String white = g.whiteUsername() != null ? g.whiteUsername() : "open";
            String black = g.blackUsername() != null ? g.blackUsername() : "open";
            sb.append("%d -- %s | White: %s | Black: %s\n".formatted(i, g.gameName(), white, black));
        }
        return sb.toString();
    }

    private String createGame(String[] params) throws Exception {
        assertSignedIn();
        if (params.length != 1) {
            System.out.println("Invalid Command");
            return "create <NAME>";
        }
        String gameName = params[0];
        int id = server.createGame(gameName);

        if (id == -1) {
            return "Failed to create game.";
        }

        // Fetch the complete game list and find the newly created game by ID
        Set<GameData> gameList = server.listGames();
        for (GameData g : gameList) {
            if (g.gameID() == id) {
                games.add(g);  // Append to preserve order
                return "Game '" + gameName + "' created successfully.";
            }
        }

        return "Game created but not found in game list.";
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

    public static ChessState getState() {
        return ChessState;
    }

}
