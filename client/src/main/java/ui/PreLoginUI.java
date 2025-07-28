package ui;


public class PreLoginUI {
    ServerFacade server;

    public PreLoginUI(ServerFacade server){
        this.server = server;
    }

    public void run(){
        System.out.println("Welcome to 240 Chess");
        preHelp();

    }


    public void preHelp(){
        register();
        login();
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }

    public void register(){
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
    }

    public void login(){
        System.out.println("login <USERNAME> <PASSWORD> - login to play chess");
    }
}
