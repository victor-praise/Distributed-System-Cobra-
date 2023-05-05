import DMTBSAPP.*;
import org.omg.CORBA.Object;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.Properties;
public class VerdunServer {

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

            NameComponent path[] = ncref.to_name("VerdunServer");
            ncref.rebind(path,href);

            System.out.println("Verdun Server is running now ... ");

            for(;;) {
                orb.run();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
