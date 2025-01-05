package Project.main_app_classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import Project.auxiliary_classes.ClientHandler;
import Project.auxiliary_classes.ManagerDatabase;
import Project.auxiliary_classes.PlayerDatabase;
import Project.auxiliary_classes.UserDatabase;

public class Server{
    final static int port = 15083;
    public static void main(String[] args) throws IOException{  
        try{
            Runtime.getRuntime().addShutdownHook(new Thread(() ->{
                PlayerDatabase.savePlayers();
                try{
                    UserDatabase.saveUsers("Project/database_txt_files/users.txt");
                    ManagerDatabase.saveManagers("Project/database_txt_files/managers.txt");
                } catch (IOException e){
                    e.printStackTrace();
                }
            }));

            try (ServerSocket serverSocket = new ServerSocket(port)){
                while (true){
                    Socket client = serverSocket.accept();
                    System.out.println("Client connected");
                    new Thread(new ClientHandler(client)).start();
                }
            }
        } 
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
