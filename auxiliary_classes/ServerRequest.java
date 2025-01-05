package Project.auxiliary_classes;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;

public class ServerRequest implements Serializable{
    private int requestID;
    private List<String> args;
    /*
     * the args will follow this pattern:
     * args[0] = playername
     * args[1] = country
     * args[2] = club
     * args[3] = position
     * args[4] = salary
     * args[5] = player height
     * args[6] = player age
     * args[7] = player jersey number
     * args[8] = lower limit of salary
     * args[9] = upper limit of salary
     * args[10] = club search criteria
     * arg[11] = player price
     * arg[12] = buyer's club
     */
    private User user;

    public ServerRequest(User user, int request, List<String> args){
        this.user = user;
        this.requestID = request;
        this.args = args;
    }

    public User getUser(){
        return user;
    }

    public int getRequestID(){
        return requestID;
    }

    public List<String> getArgs(){
        return args;
    }

    public static void sendRequest(Socket clientSocket, ServerRequest request) throws Exception{
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(request);
    }

    public static ServerRequest receiveRequest(Socket clientSocket) throws Exception{
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        return (ServerRequest) in.readObject();
    }
}
