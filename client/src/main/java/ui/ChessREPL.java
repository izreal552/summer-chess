package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessREPL {
    private final ChessClient ChessClient;

    public ChessREPL(String serverUrl) {
        ChessClient = new ChessClient(serverUrl, this);

    }

    public void run() {
        System.out.println("♞ Welcome to Chess!");
        System.out.print(ChessClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = ChessClient.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        String stateLabel = (ui.ChessClient.getState() == ChessState.LOGGED_IN) ? "[LOGGED_IN]" : "[LOGGED_OUT]";
        System.out.print("\n" + RESET_TEXT_COLOR + stateLabel + " >>> " + SET_TEXT_COLOR_GREEN);
    }
}
