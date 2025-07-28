package ui;


public class PreLoginUI {
    ServerFacade server;

    public PreLoginUI(ServerFacade server){
        this.server = server;
    }

    public void run(){
        System.out.println("Welcome to 240 Chess. Type 'help' to get started.");

    }


    public void preHelp(){
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }
}
