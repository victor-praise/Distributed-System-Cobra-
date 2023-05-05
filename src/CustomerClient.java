import DMTBSAPP.Cinema;
import DMTBSAPP.CinemaHelper;
import DMTBSAPP.CinemaPackage.TicketInfo;
import DMTBSAPP.CinemaServant;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static DMTBSAPP.CinemaHelper.narrow;

public class CustomerClient {

    private Cinema cinema;
    private String serverName="";
    private int count = 3;
    Map<String, Map<String,Integer>> bookedMovieTickets = new HashMap<String, Map<String, Integer>>();
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String user_name = "";
    public CustomerClient(String username){

        try{
            ORB orb = ORB.init(new String[] { "-ORBInitialPort", "1050" }, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);


            user_name = username;
            if (username.charAt(0) == 'A') {
                serverName = "AtwaterServer";
            } else if (username.charAt(0) == 'O') {
                serverName = "OutremontServer";
            } else if (username.charAt(0) == 'V') {
                serverName = "VerdunServer";
            }
            objRef = ncRef.resolve_str(serverName);
            cinema = CinemaHelper.narrow(objRef);

            cinema.createFile(serverName);
            createFile();
        } catch (Exception e) {
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }

    }

    synchronized void bookMovies( String movieID, String movieName, int numberOfTickets) {
        if( cinema.bookMovieTickets(user_name,movieID,movieName,numberOfTickets)){
            bookedMovieTickets.put(movieName,new HashMap<String,Integer>());
            bookedMovieTickets.get(movieName).put(movieID,numberOfTickets);
            System.out.println("Movie Successfully booked");

            writeToFile("Movie: " + movieName + " successfully booked on the " + formatter.format(date));

            cinema.writeToFile("Movie: " + movieName + " with movie id " + movieID + " successfully booked in " + serverName+ " by user "+user_name +" on the " + formatter.format(date),serverName);
        }
        else{
            System.out.println("Requested tickets exceed available tickets");
            writeToFile("Booking Movie: " + movieName + " failed because of insufficient tickets " + formatter.format(date));

            cinema.writeToFile("Booking of movie: " + movieName + " with movie id " + movieID + " in "+serverName+ " by user "+user_name +" failed because of insufficient tickets on the " + formatter.format(date),serverName);
        }

    }

    synchronized public void bookFromOtherCinema(String cinemaName,String movieID, String movieName, int numberOfTickets) {
        try {


            ORB orb = ORB.init(new String[] { "-ORBInitialPort", "1050" }, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            objRef = ncRef.resolve_str(cinemaName);
            Cinema otherCinema =  CinemaHelper.narrow(objRef);

            String id = String.valueOf(bookedMovieTickets.get(movieName));

            if(!bookedMovieTickets.containsKey(movieName) || (id.charAt(1) == movieID.charAt(0) ) ){
                if(count > 0){
                    count--;
                    //System.out.println("count is " + count);
                    if(otherCinema.bookMovieTickets(user_name,movieID,movieName,numberOfTickets)){
                        bookedMovieTickets.put(movieName,new HashMap<String,Integer>());
                        bookedMovieTickets.get(movieName).put(movieID,numberOfTickets);

                        writeToFile("Movie: " + movieName + " successfully booked from " + cinemaName + " cinema at " + formatter.format(date) );
                        cinema.writeToFile("Movie: " + movieName + " with movie id " + movieID + " successfully booked in " + cinemaName+ " by user "+user_name +" on the " + formatter.format(date),cinemaName);
                        System.out.println("Movie Successfully booked");
                    }
                    else{
                        System.out.println("Requested tickets exceed available tickets");
                        writeToFile("Booking Movie: " + movieName + " failed because of insufficient tickets " + formatter.format(date));

                        cinema.writeToFile("Booking of movie: " + movieName + " with movie id " + movieID + " in "+ cinemaName+ " by user "+user_name +" failed because of insufficient tickets on the " + formatter.format(date),cinemaName);
                    }
                }
                else{
                    System.out.println("Unable to book movie from other cinema as limit has been exceeded");
                    writeToFile("Unable to book movie from other cinema as limit has been exceeded");
                }


            }
            else{
                System.out.println("Cannot book same ticket from a different cinema");
                writeToFile("Booking Movie: " + movieName + " failed because of ticket has already been booked from another cinema " + formatter.format(date));

                cinema.writeToFile("Booking of movie: " + movieName + " with movie id " + movieID + serverName+ " by user "+user_name +" failed because user has booked ticket in other cinema on the " + formatter.format(date),serverName);
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    synchronized public void cancelTickets(String movieID, String movieName, int numberOfTickets){


        if(movieID.charAt(0) != serverName.charAt(0)){
            cancelTicketFromOtherCinema(movieID,movieName,numberOfTickets);

        }
        else if(bookedMovieTickets.get(movieName).containsKey(movieID) && cinema.cancelMovieTickets(user_name,movieID,movieName,numberOfTickets)){

            int oldCapacity = bookedMovieTickets.get(movieName).get(movieID);

            int newCap = oldCapacity - numberOfTickets;
            bookedMovieTickets.get(movieName).put(movieID,newCap);
            writeToFile("Successfully cancelled "+ numberOfTickets  + " ticket(s) for " + movieName + " on " + formatter.format(date));

            cinema.writeToFile( user_name +" Successfully cancelled " + numberOfTickets +" tickets for movie " + movieName + " with movie id " + movieID +" on " +formatter.format(date),serverName);

            System.out.println("Tickets successfully cancelled");
        }
        else {
            System.out.println("Ticket cancellation unsuccessful");
            writeToFile("Failed to cancel "+ numberOfTickets  + " ticket(s) for " + movieName + " on " + formatter.format(date));
            cinema.writeToFile( user_name +" was unable to cancel " + numberOfTickets +" ticket(s) for movie " + movieName + " with movie id " + movieID +" on " +formatter.format(date),serverName);
        }

    }

    synchronized void cancelTicketFromOtherCinema(String movieID, String movieName, int numberOfTickets) {

            try {
                ORB orb = ORB.init(new String[] { "-ORBInitialPort", "1050" }, null);
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
                String server_name = null;
                if (movieID.charAt(0) == 'A') {
                    server_name = "AtwaterServer";
                } else if (movieID.charAt(0) == 'O') {
                    server_name = "OutremontServer";
                } else if (movieID.charAt(0) == 'V') {
                    server_name = "VerdunServer";
                }

                objRef = ncRef.resolve_str(server_name);
                Cinema otherCinema =  CinemaHelper.narrow(objRef);
                if(bookedMovieTickets.get(movieName).containsKey(movieID)&&otherCinema.cancelMovieTickets(user_name,movieID,movieName,numberOfTickets)){
                    int oldCapacity = bookedMovieTickets.get(movieName).get(movieID);
                    int newCap = oldCapacity - numberOfTickets;
                    bookedMovieTickets.get(movieName).put(movieID,newCap);
                    System.out.println("Tickets successfully cancelled");
                    writeToFile("Successfully cancelled "+ numberOfTickets  + " ticket(s) for " + movieName + " on " + formatter.format(date));

                    cinema.writeToFile( user_name +" Successfully cancelled " + numberOfTickets +" tickets for movie " + movieName + " with movie id " + movieID +" on " +formatter.format(date),serverName);
                }
                else{
                    System.out.println("Ticket cancellation unsuccessful");

                    writeToFile("Failed to cancel "+ numberOfTickets  + " ticket(s) for " + movieName + " on " + formatter.format(date));
                    cinema.writeToFile( user_name +" was unable to cancel " + numberOfTickets +" ticket(s) for movie " + movieName + " with movie id " + movieID +" on " +formatter.format(date),serverName);
                }

            }

            catch (Exception e){
                e.printStackTrace();
            }

        }

    synchronized public void getBookingSchedule() {


        for (Map.Entry<String, Map<String, Integer>> empMap : bookedMovieTickets.entrySet()) {
            Map<String, Integer> addMap = empMap.getValue();

            System.out.println("Movie name: " + empMap.getKey());
            // Iterate InnerMap
            for (Map.Entry<String, Integer> addressSet : addMap.entrySet()) {
                System.out.println("Movie id: "+addressSet.getKey() + " :: " + addressSet.getValue() + " tickets");
            }
        }
        writeToFile("Requested booking schedule on " + formatter.format(date));
        cinema.writeToFile(user_name +" requested booking schedule on the " + formatter.format(date),serverName);
    }

    public void exchange(String movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets){
        if(new_movieID.charAt(0) != serverName.charAt(0)){
            exchangeFromOtherCinema(movieName,movieID,new_movieID,new_movieName,numberOfTickets);

        }
        else if(bookedMovieTickets.get(movieName).containsKey(movieID)){
            if(cinema.cancelMovieTickets(user_name,movieID,movieName,numberOfTickets)){
                if(cinema.exchangeTickets(user_name,movieID,new_movieID,new_movieName,numberOfTickets)){
                    bookedMovieTickets.put(new_movieName,new HashMap<String, Integer>());
                    bookedMovieTickets.get(new_movieName).put(new_movieID,numberOfTickets);
                    System.out.println("Tickets have been successfully exchanged");
                    writeToFile("Tickets have been successfully exchanged on " + formatter.format(date));
                    cinema.writeToFile(user_name +" requested Ticket exchange on the " + formatter.format(date),serverName);
                }
                else {
                    writeToFile("Tickets exchange failed on " + formatter.format(date));
                    cinema.writeToFile("requested Ticket exchange failed on the " + formatter.format(date),serverName);
                    System.out.println("Exchange unsuccessful");
                }
            }
            else {
                writeToFile("Tickets exchange failed on " + formatter.format(date));
                cinema.writeToFile("requested Ticket exchange failed on the " + formatter.format(date),serverName);
                System.out.println("Exchange unsuccessful");
            }

        }
        else {
            System.out.println("Exchange unsuccessful as you have not booked previous movie");
        }
    }
    private void exchangeFromOtherCinema(String movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets){
        try {
            ORB orb = ORB.init(new String[] { "-ORBInitialPort", "1050" }, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            String server_name = null;
            if (new_movieID.charAt(0) == 'A') {
                server_name = "AtwaterServer";
            } else if (new_movieID.charAt(0) == 'O') {
                server_name = "OutremontServer";
            } else if (new_movieID.charAt(0) == 'V') {
                server_name = "VerdunServer";
            }
            objRef = ncRef.resolve_str(server_name);
            Cinema otherCinema =  CinemaHelper.narrow(objRef);

            if(bookedMovieTickets.get(movieName).containsKey(movieID)){
                if(otherCinema.cancelMovieTickets(user_name,movieID,movieName,numberOfTickets)){
                    if(otherCinema.exchangeTickets(user_name,movieID,new_movieID,new_movieName,numberOfTickets)){
                        bookedMovieTickets.put(new_movieName,new HashMap<String, Integer>());
                        bookedMovieTickets.get(new_movieName).put(new_movieID,numberOfTickets);
                        System.out.println("Tickets have been successfully exchanged");
                        writeToFile("Tickets have been successfully exchanged on " + formatter.format(date));
                        cinema.writeToFile(user_name +" requested Ticket exchange on the " + formatter.format(date),serverName);
                    }
                    else {
                        System.out.println("Exchange unsuccessful");
                        writeToFile("Tickets exchange failed on " + formatter.format(date));
                        cinema.writeToFile("requested Ticket exchange failed on the " + formatter.format(date),serverName);
                    }
                }
                else{
                    System.out.println("Exchange unsuccessful");
                    writeToFile("Tickets exchange failed on " + formatter.format(date));
                    cinema.writeToFile("requested Ticket exchange failed on the " + formatter.format(date),serverName);
                }

            }
            else {
                System.out.println("Exchange unsuccessful as you have not booked previous movie");
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }




    }
    private void createFile(){
        try {
            File myObj = new File("C:\\Users\\user\\Desktop\\Comp6231A2\\src\\clientCustomerFiles\\"+user_name+".txt");
            if (myObj.createNewFile()) {
                //System.out.println("File created: " + myObj.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    private void writeToFile(String message){
        try {
            FileWriter myWriter = new FileWriter("C:\\Users\\user\\Desktop\\Comp6231A2\\src\\clientCustomerFiles\\"+user_name+".txt",true);
            myWriter.write(message);
            //myWriter.write(System.lineSeparator());
            myWriter.close();
            //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
//    public void availability(){
//        TicketInfo[] trial = cinemaApi.listMovieShowAvailability("Avatar");
//        for (int i = 0; i < trial.length; i++){
//            System.out.println("inside trial"+trial[i]);
//        }
//    }

}
