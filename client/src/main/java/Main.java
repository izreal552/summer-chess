import chess.*;
import ui.PreLoginUI;
import ui.ServerFacade;

public class Main {
    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade();
        PreLoginUI preLoginUI = new PreLoginUI(serverFacade);
        preLoginUI.run();
        System.out.println("Program successfully closed");
    }
}