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
                    preHelp();
                    break;
                case "login":
                    if(input.length != 3){
                        System.out.println("Invalid login command");
                        preHelp();
                        break;
                    } else if(server.login(input[1], input[2])){
                        System.out.println("Logged in as " + input[1]);
                        login = true;
                        break;
                    } else{
                        System.out.println("Incorrect Username/Password");
                        break;
                    }
                case "register":
                    if(input.length != 4){
                        System.out.println("Invalid register command");
                        preHelp();
                        break;
                    } else if(server.register(input[1], input[2], input[3])){
                        System.out.println("Registered and logged in as " + input[1]);
                        login = true;
                        break;
                    } else{
                        System.out.println("Username already taken");
                        break;
                    }
                default:
                    System.out.println("Invalid command");
                    preHelp();
                    break;
            }
        }


    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT]" + " >>> " + SET_TEXT_COLOR_GREEN);
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
