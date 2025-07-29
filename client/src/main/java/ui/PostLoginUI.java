package ui;

import model.GameData;

import java.util.*;

import static ui.EscapeSequences.*;

public class PostLoginUI {
    ServerFacade server;
    List<GameData> games;

    public PostLoginUI(ServerFacade server){
        this.server = server;
        this.games = new ArrayList<>();
    }


    public void run(){
        boolean login = true;
        postHelp();
        Scanner scanner = new Scanner(System.in);

        while(login){
            printPrompt();
            String[] input = scanner.nextLine().trim().split(" ");
            if (input.length == 0 || input[0].isBlank()) {
                System.out.println("No command entered.");
                continue;
            }
            String command = input[0].toLowerCase();
            switch(command){
                case "quit":
                    return;
                case "help":
                    postHelp();
                    break;
                case "logout":
                    server.logout();
                    login = false;
                    break;
                case "list":
                    refreshGames();
                    listGames();
                case "create":
                    if (input.length != 2) {
                        System.out.println("Invalid Command");
                        System.out.println("create <NAME> - a game");
                    } else {
                        server.createGame(input[1]);
                        System.out.println("Created game " + input[1]);
                    }
                    break;
                case "join":
                    if(input.length != 3){
                        System.out.println("Invalid Command");
                        System.out.println("join <ID> [WHITE|BLACK] - a game");
                    }
                    int index;
                    try {
                        index = Integer.parseInt(input[1]);
                    } catch (Exception e) {
                        System.out.println("Invalid game index: not a valid number.");
                        System.out.println("Note: Use the LIST_ID (first column) not the gameID");
                        listGames();
                        continue;
                    }


                case "observe":
                default:
                    System.out.println("Invalid command");
                    postHelp();
                    break;
            }


        }


    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN]" + " >>> " + SET_TEXT_COLOR_GREEN);
    }

    private void postHelp() {
        System.out.println("""
            Available commands:
            create <NAME> - a game
            list - games
            join <ID> [WHITE|BLACK] - a game
            observe <ID> - a game
            logout - when you are done
            help - with possible commands
            quit - playing chess
            """);
    }

    private void refreshGames() {
        games.clear();
        HashSet<GameData> set = server.listGames();
        games.addAll(set);
        if (games.isEmpty()) {
            System.out.println("There are currently no games.");
        }
    }

    private void listGames() {
        for (int index = 0; index < games.size(); index++) {
            GameData g = games.get(index);
            String white = g.whiteUsername() != null ? g.whiteUsername() : "open";
            String black = g.blackUsername() != null ? g.blackUsername() : "open";
            System.out.printf("ID: %d -- Game: %s | White: %s | Black: %s%n", index, g.gameName(), white, black);
        }
    }

}
