import ui.ChessREPL;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        System.out.println("Welcome to 240 Chess");
        new ChessREPL(serverUrl).run();
    }
}