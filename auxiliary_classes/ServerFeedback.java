package Project.auxiliary_classes;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ServerFeedback implements Serializable {
    private final boolean success;
    private final List<Player> players;
    private final List<String> results;
    private final Map<String, List<Player>> playersByCountry;
    private final long totalYearlySalary;

    public ServerFeedback(boolean success, List<Player> players, Map<String, List<Player>> playersByCountry) {
        this.success = success;
        this.players = players;
        this.playersByCountry = playersByCountry;
        this.totalYearlySalary = 0;
        this.results = null;
    }

    public ServerFeedback(boolean success, List<Player> players, Map<String, List<Player>> playersByCountry, long totalYearlySalari){
        this.success = success;
        this.players = players;
        this.playersByCountry = playersByCountry;
        this.totalYearlySalary = totalYearlySalari;
        this.results = null;
    }

    public ServerFeedback(boolean success, List<Player> players, Map<String, List<Player>> playersByCountry, List<String> results) {
        this.success = success;
        this.players = players;
        this.playersByCountry = playersByCountry;
        this.totalYearlySalary = 0;
        this.results = results;
    }

    public boolean getSuccess() {
        return success;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<String, List<Player>> getPlayersByCountry() {
        return playersByCountry;
    }

    public long getTotalYearlySalary() {
        return totalYearlySalary;
    }

    public List<String> getResults(){
        return results;
    }

    public static ServerFeedback receiveFeedback(Socket clientSocket) throws Exception {
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ServerFeedback feedback = (ServerFeedback) in.readObject();
        return feedback;
    }

    public static void sendFeedback(Socket clientSocket, ServerFeedback feedback) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(feedback);
    }
}