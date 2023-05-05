import java.text.ParseException;

public class BookingService {
    public static void main(final String[] args) throws ParseException {




        //Atwater Admin
        AdminClient adminAtwater = new AdminClient("ATWA2345");

        //Verdun Admin
        AdminClient adminVerdun = new AdminClient("VERA2346");

        //Outremont Admin
        AdminClient adminOutremont = new AdminClient("OUTA2347");

        //Atwater Users
        CustomerClient atwaterUser1 = new CustomerClient("ATWC2345");
        CustomerClient atwaterUser2 = new CustomerClient("ATWC2346");
        CustomerClient atwaterUser3 = new CustomerClient("ATWC2347");



        adminAtwater.addMovies("ATWM160223","Avatar",20);
        adminAtwater.addMovies("ATWM170223","Avatar",35);
        adminAtwater.addMovies("ATWE150223","Titanic",25);
        adminAtwater.addMovies("ATWE180223","Avengers",28);

        adminVerdun.addMovies("VERA160223","Avatar",25);
        adminVerdun.addMovies("VERM150223","Titanic",25);
        adminVerdun.addMovies("VERE140223","Avengers",20);
        adminVerdun.addMovies("VERE140223","Titanic",20);

        adminOutremont.addMovies("OUTE050323","Avatar",30);
        adminOutremont.addMovies("OUTM040323","Titanic",35);
        adminOutremont.addMovies("OUTM060323","Avengers",35);



        //client booking movie
        atwaterUser1.bookMovies("ATWM160223","Avatar", 10);
//
//        //testing ticket limit
        //atwaterUser2.bookMovies("ATWM160223","Avatar", 10);
        //atwaterUser3.bookMovies("ATWM160223","Avatar", 10);

       // testing cancelling ticket
       // atwaterUser2.cancelTickets("ATWM160223","Avatar", 10);
        //atwaterUser3.bookMovies("ATWM160223","Avatar", 10);

        //testing book from other cinema
        atwaterUser1.bookFromOtherCinema("VerdunServer","VERM150223","Titanic",15);

        //        //testing booking from another cinema limit
//        atwaterUser1.bookFromOtherCinema("Verdun","VERE140223","Avengers",15);
//        atwaterUser1.bookFromOtherCinema("Verdun","VERE140223","Titanic",5);
//        atwaterUser1.bookFromOtherCinema("Verdun","VERE140223","Titanic",10);

                //testing admin show availability
        adminAtwater.listMovieShows("Avatar");
      //adminOutremont.listMovieShows("Titanic");

      //testing exchange tickets
        atwaterUser1.exchange("Avatar","ATWM160223","ATWE180223","Avengers",30);
        //adminAtwater.listMovieShows("Titanic");
        adminAtwater.listMovieShows("Avatar");
        adminAtwater.listMovieShows("Avengers");

        //testing exchange from other cinema
        //atwaterUser1.exchange("Titanic","VERM150223","VERE140223","Avengers",15);

        //adminAtwater.listMovieShows("Titanic");
        //adminAtwater.listMovieShows("Avengers");
       // atwaterUser1.getBookingSchedule();


        //testing movie cancellation
//        adminAtwater.removeMovie("ATWM160223","Avatar");
    }

}
