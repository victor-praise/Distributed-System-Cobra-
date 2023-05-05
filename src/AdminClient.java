import DMTBSAPP.Cinema;
import DMTBSAPP.CinemaHelper;
import DMTBSAPP.CinemaPackage.TicketInfo;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminClient {

    private Cinema cinema;
    String user_name = "";
    String serverName = null;
    Map<String, Map<String,Integer>> availability = new HashMap<String, Map<String,Integer>>();
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    public AdminClient(String username){
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

    synchronized void addMovies( String movieID, String movieName, int numberOfTickets) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date oneWeekFromNow = calendar.getTime();
        SimpleDateFormat formatter2 = new SimpleDateFormat("ddMMyy");
        String formatted = movieID.replaceAll("[^0-9]", "");
        Date movieDate = formatter2.parse(formatted);

        if(oneWeekFromNow.after(movieDate)){
            if(cinema.addMovieSlots(movieID,movieName,numberOfTickets)){
                System.out.println("Movie Successfully created");
                writeToFile("Movie " + movieName + " with id: " + movieID + " successfully created on " + formatter.format(date));
                cinema.writeToFile("Admin successfully created movie " + movieName + " with id " + movieID + " on " + formatter.format(date),serverName);
            }
            else{
                System.out.println("Movie not added");
                writeToFile("Movie " + movieName + " with id: " + movieID + " was unable to be added on " + formatter.format(date));
                cinema.writeToFile("Admin failed to create movie " + movieName + " with id " + movieID + "on " + formatter.format(date),serverName);
            }
        }
        else{
            System.out.println("Admin can only create movie one week from current date");
        }
        //System.out.println(date);


    }

    synchronized void listMovieShows(String movieName)  {

        availability.clear();
        availability.put(movieName, new HashMap<String,Integer>());

        TicketInfo[] list = cinema.listMovieShowAvailability(movieName);
        if (list.length!=0) {
            for (TicketInfo ticketInfo : list) {
                availability.get(movieName).put(ticketInfo.movieName, ticketInfo.capacity);
            }
        }

            getAvailabilityFromOtherCinemas(movieName);



        //print list of availability
        if(!availability.isEmpty()){
            for (Map.Entry<String, Map<String, Integer>> empMap : availability.entrySet()) {
                Map<String, Integer> addMap = empMap.getValue();

                System.out.println(empMap.getKey() + ": ");
                // Iterate InnerMap
                for (Map.Entry<String, Integer> addressSet : addMap.entrySet()) {
                    System.out.print(addressSet.getKey() + " " + addressSet.getValue() + ", ");

                }
                System.out.println("");
            }
            writeToFile("Requested for availability of " + movieName + "on " + formatter.format(date));
            cinema.writeToFile("Admin requested for availability of movie " + movieName + " on " + formatter.format(date),serverName);
        }



    }
    synchronized void getAvailabilityFromOtherCinemas(String movieName) {
        try {

            ORB orb = ORB.init(new String[] { "-ORBInitialPort", "1050" }, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            org.omg.CORBA.Object objRef2 = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            //objRef = ncRef.resolve_str(cinemaName);
            Cinema otherCinema;
            Cinema otherCinema2;
            if(serverName != null && serverName.equals("AtwaterServer")){
                objRef = ncRef.resolve_str("VerdunServer");
                objRef2 = ncRef.resolve_str("OutremontServer");
                otherCinema = CinemaHelper.narrow(objRef);
                otherCinema2 = CinemaHelper.narrow(objRef2);

                TicketInfo[] list = otherCinema.listMovieShowAvailability(movieName);
                TicketInfo[] list2 = otherCinema2.listMovieShowAvailability(movieName);
                if(list.length!=0){
                    for (TicketInfo ticketInfo : list) {
                        availability.get(movieName).put(ticketInfo.movieName, ticketInfo.capacity);
                    }
                }

                if(list2.length!=0){
                    for (TicketInfo ticketInfo : list2) {
                        availability.get(movieName).put(ticketInfo.movieName, ticketInfo.capacity);
                    }
                }

            }
            else if(serverName != null && serverName.equals("VerdunServer")){
                objRef = ncRef.resolve_str("AtwaterServer");
                objRef2 = ncRef.resolve_str("OutremontServer");
                otherCinema = CinemaHelper.narrow(objRef);
                otherCinema2 = CinemaHelper.narrow(objRef2);

                TicketInfo[] list = otherCinema.listMovieShowAvailability(movieName);
                TicketInfo[] list2 = otherCinema2.listMovieShowAvailability(movieName);
                if(list.length!=0){
                    for (TicketInfo ticketInfo : list) {
                        availability.get(movieName).put(ticketInfo.movieName, ticketInfo.capacity);
                    }
                }

                if(list2.length!=0){
                    for (TicketInfo ticketInfo : list2) {
                        availability.get(movieName).put(ticketInfo.movieName, ticketInfo.capacity);
                    }
                }
            }
            else if(serverName != null && serverName.equals("OutremontServer")){
                objRef = ncRef.resolve_str("AtwaterServer");
                objRef2 = ncRef.resolve_str("VerdunServer");
                otherCinema = CinemaHelper.narrow(objRef);
                otherCinema2 = CinemaHelper.narrow(objRef2);

                TicketInfo[] list = otherCinema.listMovieShowAvailability(movieName);
                TicketInfo[] list2 = otherCinema2.listMovieShowAvailability(movieName);
                if(list.length!=0){
                    for (TicketInfo ticketInfo : list) {
                        availability.get(movieName).put(ticketInfo.movieName, ticketInfo.capacity);
                    }
                }

                if(list2.length!=0){
                    for (TicketInfo ticketInfo : list2) {
                        availability.get(movieName).put(ticketInfo.movieName, ticketInfo.capacity);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    synchronized void removeMovie(String movieId, String movieName) {
        if(cinema.removeMovieSlots(movieId,movieName)){
            System.out.println("Movie removed successfully");
            writeToFile("Movie " + movieName + " with id " + movieId + " was removed successfully on " + formatter.format(date));
            cinema.writeToFile("Admin successfully removed movie " + movieName + " with id " + movieId + " on " + formatter.format(date), serverName);
        }
        else{
            System.out.println("Unable to remove movie");
            writeToFile("Movie " + movieName + " with id " + movieId + " was unable to be removed on " + formatter.format(date));
            cinema.writeToFile("Admin failed in removing movie " + movieName + " with id " + movieId + " on " + formatter.format(date), serverName);
        }

    }
    synchronized public void getClientHistory() {
        String[] history = cinema.getBookingSchedule(user_name);
        for(String hist: history){
            System.out.println(hist);
        }

    }
    //creating and writing to file
    private void createFile(){
        try {
            File myObj = new File("C:\\Users\\user\\Desktop\\Comp6231A2\\src\\AdminClientFiles\\"+user_name+".txt");
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
            FileWriter myWriter = new FileWriter("C:\\Users\\user\\Desktop\\Comp6231A2\\src\\AdminClientFiles\\"+user_name+".txt",true);
            myWriter.write(message);
            //myWriter.write(System.lineSeparator());
            myWriter.close();
            //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
