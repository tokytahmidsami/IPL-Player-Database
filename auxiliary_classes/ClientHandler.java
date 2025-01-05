package Project.auxiliary_classes;

import java.net.Socket;
import java.util.List;

import Project.exception_classes.DuplicateException;

public class ClientHandler implements Runnable{
    Socket client;

    public ClientHandler(Socket client){
        System.out.println("Client handler created");
        this.client = client;
    }

    @Override
    public void run(){
        try{
            while(true){
                ServerRequest request = ServerRequest.receiveRequest(client);
                switch (request.getRequestID()){
                    case RequestID.userloginRequest:
                    case RequestID.managerLoginRequest:
                        ServerFeedback.sendFeedback(client, processLogin(request));
                        break;
                    case RequestID.usersignUpRequest:
                    case RequestID.managerRegisterRequest:
                        ServerFeedback.sendFeedback(client, processSignUp(request));
                        break;
                    case RequestID.userlogoutRequest:
                    case RequestID.managerLogoutRequest:
                        ServerFeedback.sendFeedback(client, processLogout(request));
                        break;
                    case RequestID.searchPlayerRequest:
                        ServerFeedback.sendFeedback(client, searchPlayers(request));
                        break;
                    case RequestID.countryWisePlayerCountRequest:
                        ServerFeedback.sendFeedback(client, countryWiseCount(request));
                        break;
                    case RequestID.searchClubRequest:
                        ServerFeedback.sendFeedback(client, searchClub(request));
                        break;
                    case RequestID.clubYearlySalaryRequest:
                        ServerFeedback.sendFeedback(client, getTotalYearlySalary(request));
                        break;
                    case RequestID.getManagerIDs:
                        ServerFeedback.sendFeedback(client, getManagerIDs(request));
                        break;
                    case RequestID.getAllCountriesRequest:
                        ServerFeedback.sendFeedback(client, getAllCountries(request));
                        break;
                    case RequestID.addPlayerRequest:
                        ServerFeedback.sendFeedback(client, addPlayer(request));
                        break;      
                    case RequestID.getPlayersOnSaleRequest:
                        ServerFeedback.sendFeedback(client, getPlayersOnSale(request));
                        break;     
                    case RequestID.sellPlayerRequest:
                        ServerFeedback.sendFeedback(client, sellPlayer(request));
                        break;
                    case RequestID.cancelSalePlayerRequest:
                        ServerFeedback.sendFeedback(client, cancelSale(request));
                        break;
                    case RequestID.buyPlayerRequest:
                        ServerFeedback.sendFeedback(client, buyPlayer(request));
                        break;
                    default:
                        break;
                }
            }
        } 
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //auxiliary methods
    ServerFeedback processLogin(ServerRequest request) throws Exception{
        System.out.println("User login request received");
        System.out.println("User ID in client handler : " + request.getUser().getUserID());
        if(request.getUser() instanceof Manager)
            return new ServerFeedback(ManagerDatabase.loginManager(request.getUser()), null, null);
        return new ServerFeedback(UserDatabase.loginUser(request.getUser()), null, null);
    }

    ServerFeedback processSignUp(ServerRequest request) throws Exception{
        System.out.println("User signup request received");
        if(request.getUser() instanceof Manager)
            return new ServerFeedback(ManagerDatabase.signUpManager(request.getUser()), null, null);
        return new ServerFeedback(UserDatabase.signUpUser(request.getUser()), null, null);
    }

    ServerFeedback processLogout(ServerRequest request) throws Exception{
        System.out.println("User logout request received");
        if(request.getUser() instanceof Manager)
            return new ServerFeedback(ManagerDatabase.logoutManager(request.getUser()), null, null);
        return new ServerFeedback(UserDatabase.logoutUser(request.getUser()), null, null);
    }
    
    ServerFeedback searchPlayers(ServerRequest request) throws Exception{
        System.out.println("Search player request received");
        if(UserDatabase.isUserLoggedIn(request.getUser()) == false && ManagerDatabase.isManagerLoggedIn(request.getUser()) == false)
            return new ServerFeedback(false, null, null);
        return new ServerFeedback(true, PlayerDatabase.searchPlayers(request.getArgs()), null);
    }

    ServerFeedback countryWiseCount(ServerRequest request) throws Exception{
        System.out.println("Country wise player count request received");
        if(UserDatabase.isUserLoggedIn(request.getUser()) == false && ManagerDatabase.isManagerLoggedIn(request.getUser()) == false)
            return new ServerFeedback(false, null, null);
        return new ServerFeedback(true, null, PlayerDatabase.getCountryWisePlayers());
    }

    ServerFeedback searchClub(ServerRequest request) throws Exception{
        System.out.println("Search club request received");
        if(UserDatabase.isUserLoggedIn(request.getUser()) == false && ManagerDatabase.isManagerLoggedIn(request.getUser()) == false)
            return new ServerFeedback(false, null, null);
        List<Player> result = null;
        String club = request.getArgs().get(2);
        String searchCriteria = request.getArgs().get(10);
        if(searchCriteria.equals("Any"))
            result = PlayerDatabase.getClubPlayers(club);
        else if(searchCriteria.equalsIgnoreCase("max age"))
            result = PlayerDatabase.findMaxAgePlayersInClub(club);
        else if(searchCriteria.equalsIgnoreCase("max weekly salary"))
            result = PlayerDatabase.findMaxSalaryPlayersInClub(club);
        else if(searchCriteria.equalsIgnoreCase("max height"))
            result = PlayerDatabase.findMaxHeightPlayersInClub(club);
        else{
            System.out.println("Invalid search criteria");
            return new ServerFeedback(false, result, null);
        }
        return new ServerFeedback(true, result, null);
    }

    ServerFeedback getTotalYearlySalary(ServerRequest request) throws Exception{
        System.out.println("Total yearly salary request received");
        if(UserDatabase.isUserLoggedIn(request.getUser()) == false && ManagerDatabase.isManagerLoggedIn(request.getUser()) == false)
            return new ServerFeedback(false, null, null);
        String club = request.getArgs().get(2);
        return new ServerFeedback(true, null, null, PlayerDatabase.countTotalYearlySalaryOfClub(club));
    }

    ServerFeedback getManagerIDs(ServerRequest request) throws Exception{
        System.out.println("Get all manager IDs request.");
        List<String> results = ManagerDatabase.getManagerIDs();
        return new ServerFeedback(true, null, null, results);
    }

    ServerFeedback getAllCountries(ServerRequest request){
        System.out.println("Get all countries request.");
        List<String> results = PlayerDatabase.getAllCountryList();
        return new ServerFeedback(true, null, null, results);
    }

    ServerFeedback addPlayer(ServerRequest request) throws Exception{
        System.out.println("add player request.");
        if(request.getUser().getUserID().equals(request.getArgs().get(2))){
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
            */
            try{
                PlayerDatabase.addPlayer(request.getArgs().get(0), //name 
                                     request.getArgs().get(1), //country
                                     Integer.parseInt(request.getArgs().get(6)), //age
                                     Double.parseDouble(request.getArgs().get(5)), //height
                                     request.getArgs().get(2), //club
                                     request.getArgs().get(3), //position
                                     Integer.parseInt(request.getArgs().get(7)),
                                     Integer.parseInt(request.getArgs().get(4))); //joursey
                return new ServerFeedback(true, null, null);
            }
            catch(DuplicateException e){
                return new ServerFeedback(false, null, null);
            }
        }
        else return new ServerFeedback(false, null, null, 0);
    }

    ServerFeedback getPlayersOnSale(ServerRequest request){
        System.out.println("Get players on sale request.");
        return new ServerFeedback(true, PlayerDatabase.getPlayersOnSale(), null);
    }

    ServerFeedback sellPlayer(ServerRequest request) throws Exception{
        System.out.println("Processing sell player request.");
        return new ServerFeedback(PlayerDatabase.addPlayerToMarket(request.getArgs()), null, null);
    }

    ServerFeedback cancelSale(ServerRequest request) throws Exception{
        System.out.println("Cancelling sale...");
        return new ServerFeedback(PlayerDatabase.removePlayerFromTheMarket(request.getArgs()), null, null);
    }

    ServerFeedback buyPlayer(ServerRequest request) throws Exception{
        System.out.println("Processing buy player request.");
        return new ServerFeedback(PlayerDatabase.buyPlayer(request.getArgs()), null, null);
    }
}
