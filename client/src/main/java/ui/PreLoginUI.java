package ui;


import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginUI {
    ServerFacade server;

    public PreLoginUI(ServerFacade server){
        this.server = server;
    }

    public void run(){
        System.out.println("Welcome to 240 Chess");
        preHelp();

        Scanner scanner = new Scanner(System.in);
        boolean login = false;

        while(!login){
            printPrompt("[LOGGED_OUT]");
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
                    preHelp();
            }
        }


    }

    private void printPrompt(String statement) {
        System.out.print("\n" + RESET_TEXT_COLOR + statement + " >>> " + SET_TEXT_COLOR_GREEN);
    }

    private void preHelp() {
        System.out.println("""
                Available commands:
                register <USERNAME> <PASSWORD> <EMAIL> - Create a new account
                login <USERNAME> <PASSWORD> - Login to your account
                help - Show this help message
                quit - Exit the application
                """);
    }
}
