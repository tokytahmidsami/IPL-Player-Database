package Project.auxiliary_classes;

import javafx.util.Pair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ManagerDatabase{
    private static Map<String, Pair<String, Boolean>> managers;
    private static boolean managersMapInitialized = false;

    private static void initializeManagerMap() throws IOException{
        System.out.println("Inside initializeManagerMap");
        if (managersMapInitialized) return;
        managers = new ConcurrentHashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader("Project/database_txt_files/managers.txt"));
        System.out.println("Reading from managers.txt");
        String line;
        while ((line = reader.readLine()) != null){
            String[] tokens = line.split(",");
            if (tokens.length == 2){
                managers.put(tokens[0], new Pair<>(tokens[1], false)); // Initialize with logged out status
            }
        }
        reader.close();
        managersMapInitialized = true;
        System.out.println("Exiting initializeManagerMap");
    }

    private static boolean checkManagerCredibility(User manager) throws IOException{
        initializeManagerMap();
        if (!managers.containsKey(manager.getUserID())){
            return false;
        }
        return managers.get(manager.getUserID()).getKey().equals(manager.getPassword());
    }

    public static boolean loginManager(User manager) throws IOException{
        System.out.println("Inside loginUser");
        System.out.println("User ID in UDatabase: " + manager.getUserID());
        if (checkManagerCredibility(manager)){
            System.out.println("User is credible");
            if(managers.get(manager.getUserID()).getValue()){
                System.out.println("User is already logged in");
                return false;
            } 
            managers.put(manager.getUserID(), new Pair<>(manager.getPassword(), true));
            return true;
        }
        else{
            System.out.println("User is not credible");
        }
        return false;
    }

    public static boolean logoutManager(User manager) throws IOException{
        initializeManagerMap();
        if (managers.containsKey(manager.getUserID())){
            Pair<String, Boolean> managerInfo = managers.get(manager.getUserID());
            if (managerInfo.getValue()){
                managers.put(manager.getUserID(), new Pair<>(managerInfo.getKey(), false));
                System.out.println(managers);
                return true;
            }
        }
        System.out.println("Couldn't log out\n" + managers);
        return false;
    }

    public static boolean isManagerLoggedIn(User manager) throws IOException{
        initializeManagerMap();
        if (managers.containsKey(manager.getUserID())){
            return managers.get(manager.getUserID()).getValue();
        }
        return false;
    }

    //signup section
    public static boolean signUpManager(User manager) throws IOException{
        initializeManagerMap();
        if (managers.containsKey(manager.getUserID())){
            return false;
        }
        managers.put(manager.getUserID(), new Pair<>(manager.getPassword(), true));
        System.out.println(managers);
        return true;
    }

    //saving users before quiting
    public static void saveManagers(String path) throws IOException{
        initializeManagerMap();
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for (Map.Entry<String, Pair<String, Boolean>> entry : managers.entrySet()){
            StringBuffer data = new StringBuffer();
            data.append(entry.getKey()).append(",").append(entry.getValue().getKey()).append("\n");
            writer.write(data.toString());
            logoutManager(new User(entry.getKey(), entry.getValue().getKey()));
        }
        writer.close();
    }

    //getters
    public static final List<String> getManagerIDs(){
        System.out.println("Before initializeManagerMap\n" + managers);
        try {
            initializeManagerMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("After initializeManagerMap\n" + managers);
        return managers.keySet().stream().toList();
    }
}