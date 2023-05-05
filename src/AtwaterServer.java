import DMTBSAPP.Cinema;
import DMTBSAPP.CinemaHelper;
import DMTBSAPP.CinemaServant;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class AtwaterServer {
    public static void main(String[] args) {
        try {
            ORB orb = ORB.init(args,null);
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();
            CinemaServant servant = new CinemaServant();

            servant.setORB(orb);

            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(servant);
            Cinema href = CinemaHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncref = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncref.to_name("AtwaterServer");
            ncref.rebind(path,href);

            System.out.println("Atwater Server is running now ... ");

            for(;;) {
                orb.run();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
