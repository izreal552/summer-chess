package ui;

import model.GameData;

import java.util.HashSet;

public class PostLoginUI {
    ServerFacade server;
    HashSet<GameData> games;

    public PostLoginUI(ServerFacade server){
        this.server = server;
        games = new HashSet<>();
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
