package ui;

import model.GameData;

import java.util.HashSet;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class PostLoginUI {
    ServerFacade server;
    HashSet<GameData> games;

    public PostLoginUI(ServerFacade server){
        this.server = server;
        games = new HashSet<>();
    }


    public void run(){
        boolean login = true;
        postHelp();
        Scanner scanner = new Scanner(System.in);

        while(login){
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


}
