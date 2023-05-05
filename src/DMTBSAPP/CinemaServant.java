package DMTBSAPP;

import DMTBSAPP.CinemaPackage.TicketInfo;
import org.omg.CORBA.Object;
import org.omg.CORBA.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CinemaServant extends CinemaPOA {
    Map<String, Map<String,Integer>> movieTickets;

    ArrayList<String> ticketHistory = new ArrayList<String>();
    private ORB orb;

    public CinemaServant(){

        movieTickets = new HashMap<String, Map<String, Integer>>();
    }

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }
    @Override
    public TicketInfo[] listMovieShowAvailability(String movieName) {
        //return movieTickets.get(movieName);
        TicketInfo[] test = new TicketInfo[movieTickets.get(movieName).size()];

         Map<String, Integer> innerMap = movieTickets.get(movieName);
            int i = 0;
            if(innerMap!=null){
                for (Map.Entry<String, Integer> addressSet : innerMap.entrySet()) {
                    test[i] = new TicketInfo(addressSet.getKey(),addressSet.getValue());
                    i++;

                }
            }


        return test;
    }

    @Override
    public boolean removeMovieSlots(String movieID, String movieName) {


        if (movieTickets.containsKey(movieName) && movieTickets.get(movieName).containsKey(movieID))
        {

            movieTickets.get(movieName).remove(movieID);
            String newMovieId = null;
            Map<String, Integer> innerMap = movieTickets.get(movieName);

            for (Map.Entry<String, Integer> addressSet : innerMap.entrySet()) {

                if(addressSet.getKey() != null){
                    newMovieId = addressSet.getKey();
                    break;
                }

            }
            rebookTicket(movieID,newMovieId);
            return true;
        }


        return false;
    }

    public void rebookTicket(String movieId,String newMovieId){
        ArrayList<String> storeRecord = new ArrayList<String>();
        //System.out.println("new movie id is " );
        for(String history:ticketHistory){
            //System.out.println(history);
            String[] booked = history.split(" ");

            if(movieTickets.get(booked[2]).get(newMovieId) != null){
                for (int i = 0; i < booked.length; i++) {

                    if (booked[1]== movieId) {
                        int currentCapacity = movieTickets.get(booked[2]).get(newMovieId);
                        int newCapacity = currentCapacity - Integer.parseInt(booked[3]);
                        if(newCapacity>=0){
                            //String history = customerID + " " + movieID + " "+movieName + " "+numberOfTickets;
                            movieTickets.get(booked[2]).put(newMovieId,newCapacity);
                            String record = booked[0] + " " + newMovieId + " "+ booked[2] + " "+ booked[3];
                            storeRecord.add(record);
                            System.out.println("ticket rebooked successfully");
                            //return true;
                        }
                        else{
                            System.out.println("Ticket could not be rebooked because it exceeds available tickets");

                        }
                        break;
                    }
                }
            }
            else{
                System.out.println("rebook unsuccessful as no other there are no other available tickets");
                break;
            }


        }
        ticketHistory.addAll(storeRecord);

    }
    @Override
    public boolean addMovieSlots(String movieID, String movieName, int bookingCapacity) {
        if(!movieTickets.containsKey(movieName)){
            movieTickets.put(movieName,new HashMap<String,Integer>());
            movieTickets.get(movieName).put(movieID,bookingCapacity);
        }
        else{
            movieTickets.get(movieName).put(movieID,bookingCapacity);
        }

        if(movieTickets.containsKey(movieName)){
            System.out.println("Movie Successfully created");
            return true;
        }
        else{
            System.out.println("Movie not added");
            return false;
        }
    }

    @Override
    public boolean cancelMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        for (Map.Entry<String, Integer> empMap : movieTickets.get(movieName).entrySet()) {
            // Map<String, Integer> addMap = empMap.getValue();
            System.out.println("movie id is " + movieID + " and key is " + empMap.getKey());
            if(empMap.getKey().equals(movieID)){
                int currentTickets = empMap.getValue();
                int newTickets = currentTickets + numberOfTickets;

                movieTickets.get(movieName).put(empMap.getKey(), newTickets);
                //writeToFile(customerID + "ca");
                return true;
            }

        }
        return false;
    }

    @Override
    public String[] getBookingSchedule(String customerID) {
        return ticketHistory.toArray(new String[0]);
    }

    @Override
    public boolean bookMovieTickets(String customerID, String movieID, String movieName, int numberOfTickets) {
        if(movieTickets.containsKey(movieName) && movieTickets.get(movieName).containsKey(movieID)){
            int currentCapacity = movieTickets.get(movieName).get(movieID);
            int newCapacity = currentCapacity - numberOfTickets;
            if(newCapacity>=0){
                movieTickets.get(movieName).put(movieID,newCapacity);
                String history = customerID + " " + movieID + " "+movieName + " "+numberOfTickets;
                if(!ticketHistory.contains(history)){
                    ticketHistory.add(history);
                }

                System.out.println("ticket booked successfully");
                return true;
            }
            else{
                System.out.println("Requested tickets exceed available tickets");

            }
        }
        else {
            System.out.println("Requested movie does not exist");
        }
        return false;
    }

    @Override
    public boolean exchangeTickets(String customerID, String movieID, String new_movieID, String new_movieName, int numberOfTickets) {
        for (Map.Entry<String, Map<String, Integer>> entry : movieTickets.entrySet()){
            Map<String, Integer> addMap = entry.getValue();

            for (Map.Entry<String, Integer> addressSet : addMap.entrySet()) {

                if((addressSet.getKey() != null && addressSet.getKey().equals(movieID))){
                    if(bookMovieTickets(customerID,new_movieID,new_movieName,numberOfTickets))
                    {
                    System.out.println("Tickets successfully exchanged");
                    return true;}
                }
            }

        }
        return false;
    }

    @Override
    public void createFile(String servername) {
        try {
            File myObj = new File("C:\\Users\\user\\Desktop\\Comp6231A2\\src\\ServerFiles\\"+servername+".txt");
            if (myObj.createNewFile()) {
                //System.out.println("File created: " + myObj.getName());
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void writeToFile(String message, String servername) {
        try {
            FileWriter myWriter = new FileWriter("C:\\Users\\user\\Desktop\\Comp6431A1\\src\\ServerFiles\\"+servername+".txt",true);
            myWriter.write(message);
//            myWriter.write(System.lineSeparator());
//            myWriter.write(System.lineSeparator());
            myWriter.close();
            // System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
