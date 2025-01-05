package Project.auxiliary_classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.util.Pair;

public class UserDatabase{
    private static Map<String, Pair<String, Boolean>> users;
    private static boolean usersMapInitialized = false;

    private static void initializeUserMap() throws IOException{
        System.out.println("Inside initializeUserMap");
        if (usersMapInitialized) return;
        users = new ConcurrentHashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader("Project/database_txt_files/users.txt"));
        while (true){
            String line = reader.readLine();
            System.out.println("Line: " + line);
            if (line == null) break;
            String[] tokens = line.split(",");
            users.put(tokens[0], new Pair<>(tokens[1], false));
        }
        reader.close();
        usersMapInitialized = true;
    }

    private static boolean checkUserCredibility(User user) throws IOException{
        initializeUserMap();
        if (!users.containsKey(user.getUserID())){
            return false;
        }
        return users.get(user.getUserID()).getKey().equals(user.getPassword());
    }

    //login and logout section
    public static boolean loginUser(User user) throws IOException{
        System.out.println("Inside loginUser");
        System.out.println("User ID in UDatabase: " + user.getUserID());
        if (checkUserCredibility(user)){
            System.out.println("User is credible");
            if(users.get(user.getUserID()).getValue()){
                System.out.println("User is already logged in");
                return false;
            } 
            users.put(user.getUserID(), new Pair<>(user.getPassword(), true));
            return true;
        }
        else{
            System.out.println("User is not credible");
        }
        return false;
    }

    public static boolean logoutUser(User user) throws IOException{
        initializeUserMap();
        if (users.containsKey(user.getUserID())){
            Pair<String, Boolean> userInfo = users.get(user.getUserID());
            if (userInfo.getValue()){
                users.put(user.getUserID(), new Pair<>(userInfo.getKey(), false));
                return true;
            }
        }
        return false;
    }

    public static boolean isUserLoggedIn(User user) throws IOException{
        initializeUserMap();
        if (users.containsKey(user.getUserID())){
            return users.get(user.getUserID()).getValue();
        }
        return false;
    }

    //signup section
    public static boolean signUpUser(User user) throws IOException{
        initializeUserMap();
        if (users.containsKey(user.getUserID())){
            return false;
        }
        users.put(user.getUserID(), new Pair<>(user.getPassword(), true));
        return true;
    }

    //saving users before quiting
    public static void saveUsers(String path) throws IOException{
        initializeUserMap();
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for (Map.Entry<String, Pair<String, Boolean>> entry : users.entrySet()){
            StringBuffer data = new StringBuffer();
            data.append(entry.getKey()).append(",").append(entry.getValue().getKey()).append("\n");
            writer.write(data.toString());
            logoutUser(new User(entry.getKey(), entry.getValue().getKey()));
        }
        writer.close();
    }
}