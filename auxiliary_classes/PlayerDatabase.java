package Project.auxiliary_classes;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import Project.exception_classes.DuplicateException;
import Project.exception_classes.NotFoundException;

public class PlayerDatabase{
    private static List<Player> players = new CopyOnWriteArrayList<>();
    private static Map<String, List<Player>> countryWisePlayers = new ConcurrentHashMap<>(); 
    private static Map<String, List<Player>> clubWisePlayers = new ConcurrentHashMap<>();
    private static boolean initializedCountryWiseMap = false;
    private static boolean initializedClubWiseMap = false;
    private static boolean playersInitialized = false; 
    private static final List<String> countries = List.of(
        "Islamic Emirate of Afghanistan",
        "Antigua and Barbuda",
        "Australia",
        "Austria",
        "Bahrain",
        "Bangladesh",
        "Barbados",
        "Bermuda",
        "British Virgin Islands",
        "Canada",
        "Dominica",
        "England",
        "Germany",
        "Grenada",
        "Guyana",
        "Hong Kong",
        "India",
        "Ireland",
        "Italy",
        "Jamaica",
        "Kuwait",
        "Malaysia",
        "Montserrat",
        "Nepal",
        "Netherlands",
        "New Zealand",
        "Oman",
        "Pakistan",
        "Papua New Guinea",
        "Qatar",
        "Saint Kitts and Nevis",
        "Saint Lucia",
        "Saint Vincent and the Grenadines",
        "Saudi Arabia",
        "Scotland",
        "Singapore",
        "South Africa",
        "Spain",
        "Sri Lanka",
        "Thailand",
        "Trinidad and Tobago",
        "Uganda",
        "United Arab Emirates",
        "United States of America",
        "West Indies",
        "Zimbabwe");

    private final static String filename = "Project/database_txt_files/players.txt";

    //Functions to read and write in file...................................................

    private static void loadPlayers(){
        if(playersInitialized) return;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = br.readLine()) != null){
                String[] details = line.split(",");
                String name = details[0];
                String country = details[1];  
                int age = Integer.parseInt(details[2]);
                double height = Double.parseDouble(details[3]);
                String club = details[4];
                String position = details[5];
                int joursey = details[6].isEmpty() ? -1 : Integer.parseInt(details[6]);
                int weeklySalary = Integer.parseInt(details[7]);
                players.add(new Player(name, country, age, height, club, position, joursey, weeklySalary));
            }
            playersInitialized = true;
            initializeClubWisePlayers();
            initializeCountryWisePlayers();
        } 
        catch (IOException exc){
            System.out.println("Error loading players: " + exc.getMessage());
        }
    }

    public static void savePlayers(){
        if(players.isEmpty()) loadPlayers();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))){
            for (Player player : players){
                bw.write(String.format("%s,%s,%d,%.2f,%s,%s,%s,%d\n",
                        player.getName(), player.getCountry(), player.getAge(), player.getHeight(),
                        player.getClub(), player.getPosition(),
                        player.getNumber() == -1 ? "" : player.getNumber(),
                        player.getWeeklySalary()));
            }
        } catch (IOException e){
            System.out.println("Error saving players: " + e.getMessage());
        }
    }

    //Getter functions.......................................................................
    public static List<Player> getPlayers(){
        loadPlayers();
        return players;
    }

    public static final List<String> getAllCountryList(){
        return countries;
    }

    public static final Map<String, List<Player>> getCountryWisePlayers(){
        initializeCountryWisePlayers();
        return countryWisePlayers;
    }

    public static final Map<String, List<Player>> getClubWisePlayers(){
        initializeClubWisePlayers();
        return clubWisePlayers;
    }

    public static final List<Player> getClubPlayers(String club){
        initializeClubWisePlayers();
        return clubWisePlayers.get(club);
    }

    public static final List<Player> getPlayersOnSale(){
        loadPlayers();
        List<Player> result = new ArrayList<>();
        for(Player p: players)
            if(p.getOnSaleStatus()) result.add(p);
        return result;
    }

    //Maps initializer functions..............................................................

    private static void initializeCountryWisePlayers(){
        if(initializedCountryWiseMap) return;
        countryWisePlayers.clear();
        for(Player player: players){
            String country = player.getCountry();
            countryWisePlayers.putIfAbsent(country, new CopyOnWriteArrayList<Player>());
            countryWisePlayers.get(country).add(player);
        }
        initializedCountryWiseMap = true;
    }

    private static void initializeClubWisePlayers(){
        if(initializedClubWiseMap) return;
        clubWisePlayers.clear();
        for(Player player: players){
            String club = player.getClub();
            clubWisePlayers.putIfAbsent(club, new CopyOnWriteArrayList<Player>());
            clubWisePlayers.get(club).add(player);
        }
        initializedClubWiseMap = true;
    } 

    //Menu Option 0 functions...........................................................
    public static List<Player> searchPlayers(List<String> args){
        if(players.isEmpty()) loadPlayers();
        if(args.size() < 10){
            return null;
        }
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
        */
        List<Player> result = new ArrayList<>();
        for(Player p: players){
            boolean matchesCriteria = true;
            if(!args.get(0).isEmpty() && !p.getName().equalsIgnoreCase(args.get(0)))
                matchesCriteria = false;
            if(!args.get(1).isEmpty() && (!args.get(1).equalsIgnoreCase("any") && !p.getCountry().equalsIgnoreCase(args.get(1))))
                matchesCriteria = false;
            if(!args.get(2).isEmpty() && (!args.get(2).equalsIgnoreCase("any") && !p.getClub().equalsIgnoreCase(args.get(2))))
                matchesCriteria = false;
            if(!args.get(3).isEmpty() && (!args.get(3).equalsIgnoreCase("any") && !p.getPosition().equalsIgnoreCase(args.get(3))))
                matchesCriteria = false;
            if(!args.get(4).isEmpty() && p.getWeeklySalary() != Integer.parseInt(args.get(4)))
                matchesCriteria = false;
            if(!args.get(5).isEmpty() && p.getHeight() != Double.parseDouble(args.get(5)))
                matchesCriteria = false;
            if(!args.get(6).isEmpty() && p.getAge() != Integer.parseInt(args.get(6)))
                matchesCriteria = false;
            if(!args.get(7).isEmpty() && p.getNumber() != Integer.parseInt(args.get(7)))
                matchesCriteria = false;
            if(!args.get(8).isEmpty() && p.getWeeklySalary() < Integer.parseInt(args.get(8)))
                matchesCriteria = false;
            if(!args.get(9).isEmpty() && p.getWeeklySalary() > Integer.parseInt(args.get(9)))
                matchesCriteria = false;
            if(matchesCriteria)
                result.add(p);
        }
        return result;
    }

    //Menu Option 1 functions...........................................................

    public static Player searchByName(String name) throws NotFoundException{
        loadPlayers();
        Player result = null;
        for (Player player : players){
            if (player.getName().equalsIgnoreCase(name)){
                result = new Player(player);
                break;
            }
        }
        if(result == null)
            throw new NotFoundException("No such player with this name");
        return result;
    }

    public static List<Player> searchByCountryAndClub(String country, String club) throws NotFoundException{
        if(!initializedCountryWiseMap)
            initializeCountryWisePlayers();
        List<Player> result = new ArrayList<>();
        String foundCountry = "";
        var it = countryWisePlayers.keySet().iterator();
        while(it.hasNext()){
            String c = it.next();
            if(c.equalsIgnoreCase(country)){
                foundCountry = c;
                break;
            }
        }
        if(!foundCountry.isEmpty()){
            List<Player> LP = countryWisePlayers.get(foundCountry);
            String any = "ANY";
            for(int i = 0; i < LP.size(); i++){
                String playerClub = LP.get(i).getClub();
                if(club.equalsIgnoreCase(any) || playerClub.equalsIgnoreCase(club))
                    result.add(LP.get(i));
            }
        }
        if(result.size() == 0)
            throw new NotFoundException("No such player with this country and club");
        return result;
    }

    public static List<Player> searchByPosition(String position) throws NotFoundException{
        loadPlayers();
        List<Player> result = new ArrayList<>();
        for(Player player: players){
            if(player.getPosition().equalsIgnoreCase(position))
                result.add(player);
        }
        if(result.size() == 0)
            throw new NotFoundException("No such player with this position");
        return result;
    }

    public static List<Player> searchBySalaryRange(int lowerBound, int upperBound) throws NotFoundException{
        loadPlayers();
        List<Player> result = new ArrayList<>();
        for(Player player: players){
            int playerSalary = player.getWeeklySalary();
            if(lowerBound <= playerSalary && playerSalary <= upperBound)
                result.add(player);
        }
        if(result.size() == 0)
            throw new NotFoundException("No such player with this weekly salary range");
        return result;
    } 

    public static List<Player> searchByHeight(String comp, double h) throws NotFoundException{
        loadPlayers();
        List<Player> result = new ArrayList<>();
        for(Player player: players){
            double playerHeight = player.getHeight();
            if(comp.equalsIgnoreCase(">=") && playerHeight >= h)
                result.add(player);
            else if(comp.equalsIgnoreCase("==") && playerHeight == h)
                result.add(player);
            else if(comp.equalsIgnoreCase("<=") && playerHeight <= h)
                result.add(player);
        }
        if(result.size() == 0)
            throw new NotFoundException("No such player with this height constrains");
        return result;
    }

    public static Map<String, Integer> countCountryWisePlayers(){
        if(!initializedCountryWiseMap )
            initializeCountryWisePlayers();
        Map<String, Integer> result = new HashMap<>();
        var it = countryWisePlayers.entrySet().iterator();
        while(it.hasNext()){
            var entry = it.next();
            result.put(entry.getKey(), entry.getValue().size());
        }
        return result;
    }

    //Menu Option 2 functions.......................................................

    public static List<Player> findMaxSalaryPlayersInClub(String club) throws NotFoundException{
        if(!initializedClubWiseMap )
            initializeClubWisePlayers();
        List<Player> result = new ArrayList<>();
        String foundClub = "";
        var it = clubWisePlayers.keySet().iterator();
        while(it.hasNext()){
            String c = it.next();
            if(c.equalsIgnoreCase(club)){
                foundClub = c;
                break;
            }
        }
        if(!foundClub.isEmpty()){
            List<Player> LP = clubWisePlayers.get(foundClub);
            result.add(LP.get(0));
            for(int i = 1; i < LP.size(); i++){
                int salary = LP.get(i).getWeeklySalary();
                int maxSalary = result.get(0).getWeeklySalary();
                if(maxSalary < salary){
                    result.clear();
                    result.add(LP.get(i));
                }
                else if(maxSalary == salary)
                    result.add(LP.get(i));
            }
        }
        else
            throw new NotFoundException("No such club with this name");
        return result;
    }

    public static List<Player> findMaxAgePlayersInClub(String club) throws NotFoundException{
        if(!initializedClubWiseMap )
            initializeClubWisePlayers();
        List<Player> result = new ArrayList<>();
        String foundClub = "";
        var it = clubWisePlayers.keySet().iterator();
        while(it.hasNext()){
            String c = it.next();
            if(c.equalsIgnoreCase(club)){
                foundClub = c;
                break;
            }
        }
        if(!foundClub.isEmpty()){
            List<Player> LP = clubWisePlayers.get(foundClub);
            result.add(LP.get(0));
            for(int i = 1; i < LP.size(); i++){
                int age = LP.get(i).getAge();
                int maxAge = result.get(0).getAge();
                if(maxAge < age){
                    result.clear();
                    result.add(LP.get(i));
                }
                else if(maxAge == age)
                    result.add(LP.get(i));
            }
        }
        else
            throw new NotFoundException("No such club with this name");
        return result;
    }

    public static List<Player> findMaxHeightPlayersInClub(String club) throws NotFoundException{
        if(!initializedClubWiseMap )
            initializeClubWisePlayers();
        List<Player> result = new ArrayList<>();
        String foundClub = "";
        var it = clubWisePlayers.keySet().iterator();
        while(it.hasNext()){
            String c = it.next();
            if(c.equalsIgnoreCase(club)){
                foundClub = c;
                break;
            }
        }
        if(!foundClub.isEmpty()){
            List<Player> LP = clubWisePlayers.get(foundClub);
            result.add(LP.get(0));
            for(int i = 1; i < LP.size(); i++){
                double height = LP.get(i).getHeight();
                double maxHeight = result.get(0).getHeight();
                if(maxHeight < height){
                    result.clear();
                    result.add(LP.get(i));
                }
                else if(maxHeight == height)
                    result.add(LP.get(i));
            }
        }
        else
            throw new NotFoundException("No such club with this name");
        return result;
    }

    private static long countTotalWeeklySalaryOfClub(String club) throws NotFoundException{
        if(!initializedClubWiseMap )
            initializeClubWisePlayers();
        long result = -1;
        String foundClub = "";
        var it = clubWisePlayers.keySet().iterator();
        while(it.hasNext()){
            String c = it.next();
            if(c.equalsIgnoreCase(club)){
                foundClub = c;
                break;
            }
        }
        if(!foundClub.isEmpty()){
            List<Player> LP = clubWisePlayers.get(foundClub);
            result = 0;
            for(int i = 0; i < LP.size(); i++)
                result += LP.get(i).getWeeklySalary();
        }
        else
            throw new NotFoundException("No such club with this name");
        return result;
    }

    public static long countTotalYearlySalaryOfClub(String club) throws NotFoundException{
        return 52 * countTotalWeeklySalaryOfClub(club);
    }

    //Menu Option 3 functions....................................................

    public static void addPlayer(Player player) throws DuplicateException{
        loadPlayers();
        for (Player p : players){
            if (p.getName().equalsIgnoreCase(player.getName()))
                throw new DuplicateException("Player with this name already exists");
        }
        players.add(player);
        initializedClubWiseMap = false;
        initializedCountryWiseMap = false;
        initializeClubWisePlayers();
        initializeCountryWisePlayers();
    }

    public static void addPlayer(String name, String country, int age, double height, String club, 
                          String position, int joursey, int weeklySalary) 
                          throws DuplicateException{
        loadPlayers();
        for (Player p : players){
            if (p.getName().equalsIgnoreCase(name))
                throw new DuplicateException("Player with this name already exists");
        }
        players.add(new Player(name, country, age, height, club, position, joursey, weeklySalary));
        initializedClubWiseMap = false;
        initializedCountryWiseMap = false;
        initializeClubWisePlayers();
        initializeCountryWisePlayers();
    }

    //Trading functions....................................................................
    public static boolean addPlayerToMarket(List<String> args){
        loadPlayers();
        initializeClubWisePlayers();
        if(!clubWisePlayers.containsKey(args.get(2)))
            return false;
        for(Player p: clubWisePlayers.get(args.get(2)))
            if(p.getName().equals(args.get(0))){
                if(p.getOnSaleStatus()) return false;
                p.setOnSaleStatus(true);
                p.setPrice(Integer.parseInt(args.get(10)));
                return true;
            }
        return false;
    }

    public static boolean removePlayerFromTheMarket(List<String> args){
        loadPlayers();
        if(!clubWisePlayers.containsKey(args.get(2)))
            return false;
        for(Player p: clubWisePlayers.get(args.get(2)))
            if(p.getName().equals(args.get(0))){
                if(!p.getOnSaleStatus()) return false;
                p.setOnSaleStatus(false);
                p.setPrice(0);
                return true;
            }
        return false;
    }

    public static boolean buyPlayer(List<String> args) throws NotFoundException{
        loadPlayers();
        if(!clubWisePlayers.containsKey(args.get(2)))
            return false;
        if(!clubWisePlayers.containsKey(args.get(11))) 
            //if buyer isn't listed in clubs, list the club
            clubWisePlayers.put(args.get(11), new CopyOnWriteArrayList<>());
        for(Player p: clubWisePlayers.get(args.get(2)))
            if(p.getName().equals(args.get(0))){
                if(!p.getOnSaleStatus()) return false;
                p.setOnSaleStatus(false);
                p.setPrice(0);
                p.setClub(args.get(11));
                clubWisePlayers.get(args.get(11)).add(p);
                clubWisePlayers.get(args.get(2)).remove(p);
                return true;
            }
        return false;
    }
}