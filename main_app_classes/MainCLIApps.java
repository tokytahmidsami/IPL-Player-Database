package Project.main_app_classes;
import java.util.Scanner;
import Project.auxiliary_classes.Player;
import Project.auxiliary_classes.PlayerDatabase;

public class MainCLIApps{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);

        while (true){
            System.out.println();
            System.out.println("Main Menu:");
            System.out.println("(1) Search Players");
            System.out.println("(2) Search Clubs");
            System.out.println("(3) Add Player");
            System.out.println("(4) Exit System");
            System.out.println();
            System.out.print("Choose an option: ");

            String choiceStr = input.nextLine().trim();

            try{
                int choice = Integer.parseInt(choiceStr);

                switch (choice){
                    case 1:
                        searchPlayersMenu(input);
                        break;
                    case 2:
                        searchClubsMenu(input);
                        break;
                    case 3:
                        addPlayerMenu(input);
                        break;
                    case 4:
                        PlayerDatabase.savePlayers();
                        System.out.println("Exiting system...");
                        return;
                    default:
                        System.out.println("Invalid entry. Try again.");
                }
            } catch (Exception e){
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static void searchPlayersMenu(Scanner input){
        while (true){
            System.out.println();
            System.out.println("Player Searching Options:");
            System.out.println("(1) By Player Name");
            System.out.println("(2) By Club and Country");
            System.out.println("(3) By Position");
            System.out.println("(4) By Salary Range");
            System.out.println("(5) Country Wise Player Count");
            System.out.println("(6) Back to Main Menu");
            System.out.println();
            System.out.print("Choose an option: ");
        
            try{
                String choiceString = input.nextLine().trim();
                int choice = Integer.parseInt(choiceString);
        
                switch (choice){
                    case 1:
                        System.out.println();
                        System.out.print("Enter Player Name: ");
                        String name = input.nextLine();
                        Player p = PlayerDatabase.searchByName(name);
                        p.showDetails();
                        break;
        
                    case 2:
                        System.out.println();
                        System.out.print("Input country name: ");
                        String country = input.nextLine();
                        System.out.print("Input club name: ");
                        String club = input.nextLine();
                        var list1 = PlayerDatabase.searchByCountryAndClub(country, club);
                        System.out.println();
                        for (var x : list1) x.showDetails();
                        break;
        
                    case 3:
                        System.out.println();
                        System.out.print("Input position: ");
                        String position = input.nextLine();
                        var list2 = PlayerDatabase.searchByPosition(position);
                        System.out.println();
                        for (var x : list2) x.showDetails();
                        break;
        
                    case 4:
                        System.out.println();
                        System.out.print("Give lower bound: ");
                        int l = Integer.parseInt(input.nextLine());
                        System.out.print("Give upper bound: ");
                        int u = Integer.parseInt(input.nextLine());
                        var list3 = PlayerDatabase.searchBySalaryRange(l, u);
                        System.out.println();
                        for (var x : list3) x.showDetails();
                        break;
        
                    case 5:
                        var list4 = PlayerDatabase.countCountryWisePlayers();
                        System.out.println();
                        for (var entry : list4.entrySet()){
                            System.out.println("Total player from " + entry.getKey() + ": " + entry.getValue());
                        }
                        break;
                        
                    case 6:
                        return;
        
                    default:
                        System.out.println();
                        System.out.println("Invalid entry. Try again.");
                        break;
                }
            } 
            catch (NumberFormatException e){
                System.out.println();
                System.out.println("Invalid input. Please enter a number.");
            } 
            catch (Exception e){
                System.out.println();
                System.out.println(e.getMessage());
            }
        }
    }

    private static void searchClubsMenu(Scanner input){
        while (true){
            System.out.println();
            System.out.println("Club Searching Options:");
            System.out.println("(1) Player(s) with the maximum salary of a club");
            System.out.println("(2) Player(s) with the maximum age of a club");
            System.out.println("(3) Player(s) with the maximum height of a club");
            System.out.println("(4) Total yearly salary of a club");
            System.out.println("(5) Back to Main Menu");
            System.out.println();
            System.out.print("Choose an option: ");
        
            try{
                // Use nextLine and parse to int for better input handling
                String choiceString = input.nextLine().trim();
                int choice = Integer.parseInt(choiceString);
        
                switch (choice){
                    case 1:
                        System.out.println();
                        System.out.print("Enter Club Name: ");
                        String clubMaxSalary = input.nextLine().trim();
                        var maxSalaryList = PlayerDatabase.findMaxSalaryPlayersInClub(clubMaxSalary);
                        for (var player : maxSalaryList) player.showDetails();
                        break;
        
                    case 2:
                        System.out.println();
                        System.out.print("Enter Club Name: ");
                        String clubMaxAge = input.nextLine().trim();
                        var maxAgeList = PlayerDatabase.findMaxAgePlayersInClub(clubMaxAge);
                        for (var player : maxAgeList) player.showDetails();
                        break;
        
                    case 3:
                        System.out.println();
                        System.out.print("Enter Club Name: ");
                        String clubMaxHeight = input.nextLine().trim();
                        var maxHeightList = PlayerDatabase.findMaxHeightPlayersInClub(clubMaxHeight);
                        for (var player : maxHeightList) player.showDetails();
                        break;
        
                    case 4:
                        System.out.println();
                        System.out.print("Enter Club Name: ");
                        String clubTotalSalary = input.nextLine().trim();
                        var totalSalary = PlayerDatabase.countTotalYearlySalaryOfClub(clubTotalSalary);
                        System.out.println();
                        System.out.println("Total yearly salary of " + clubTotalSalary + ": " + totalSalary);
                        break;
        
                    case 5:
                        return;
        
                    default:
                        System.out.println();
                        System.out.println("Invalid entry. Try again.");
                        break;
                }
            } 
            catch (NumberFormatException e){
                System.out.println();
                System.out.println("Invalid input. Please enter a number.");
            } 
            catch (Exception e){
                System.out.println();
                System.out.println(e.getMessage());
            }
        }
    }

    private static void addPlayerMenu(Scanner input){
        try{
            System.out.println();
            System.out.println("Enter Player Details: ");

            System.out.print("Name: ");
            String name = input.nextLine();

            System.out.print("Country: ");
            String country = input.nextLine();

            System.out.print("Age: ");
            if(!input.hasNextInt()){
                throw new Exception("Invalid entry. Try again.");
            }
            int age = input.nextInt();
            input.nextLine();

            System.out.print("Height: ");
            if(!input.hasNextDouble()){
                throw new Exception("Invalid entry. Try again.");
            }
            double height = input.nextDouble();
            input.nextLine();

            System.out.print("Club: ");
            String club = input.nextLine();

            System.out.print("Position: ");
            String position = input.nextLine();

            System.out.print("Joursey Number (If N/A type -1): ");
            if(!input.hasNextInt()){
                throw new Exception("Invalid entry. Try again.");
            }
            int joursey = input.nextInt();
            input.nextLine();

            System.out.print("Weekly Salary: ");
            if(!input.hasNextInt()){
                throw new Exception("Invalid entry. Try again.");
            }
            int weeklySalary = input.nextInt();
            input.nextLine();

            PlayerDatabase.addPlayer(name, country, age, height, club, position, joursey, weeklySalary);
        }
        catch (NumberFormatException e){
            System.out.println();
            System.out.println("Invalid input. Please enter a number.");
        }
        catch(Exception e){
            input.nextLine();
            System.out.println(e.getMessage());
        }
    }
}
