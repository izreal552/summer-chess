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




}
