package DMTBSAPP;

/**
* DMTBSAPP/CinemaHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DMTBS.idl
* Sunday, February 19, 2023 8:49:09 PM EST
*/

public final class CinemaHolder implements org.omg.CORBA.portable.Streamable
{
  public DMTBSAPP.Cinema value = null;

  public CinemaHolder ()
  {
  }

  public CinemaHolder (DMTBSAPP.Cinema initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = DMTBSAPP.CinemaHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    DMTBSAPP.CinemaHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return DMTBSAPP.CinemaHelper.type ();
  }

}
