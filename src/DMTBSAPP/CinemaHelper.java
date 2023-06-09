package DMTBSAPP;


/**
* DMTBSAPP/CinemaHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DMTBS.idl
* Sunday, February 19, 2023 8:49:09 PM EST
*/

abstract public class CinemaHelper
{
  private static String  _id = "IDL:DMTBSAPP/Cinema:1.0";

  public static void insert (org.omg.CORBA.Any a, DMTBSAPP.Cinema that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static DMTBSAPP.Cinema extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (DMTBSAPP.CinemaHelper.id (), "Cinema");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static DMTBSAPP.Cinema read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_CinemaStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, DMTBSAPP.Cinema value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static DMTBSAPP.Cinema narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DMTBSAPP.Cinema)
      return (DMTBSAPP.Cinema)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DMTBSAPP._CinemaStub stub = new DMTBSAPP._CinemaStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static DMTBSAPP.Cinema unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DMTBSAPP.Cinema)
      return (DMTBSAPP.Cinema)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DMTBSAPP._CinemaStub stub = new DMTBSAPP._CinemaStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
