/*===========================================================================
 
  CityList.java

  Copyright (c)2022 Kevin Boone, GPL v3.0

===========================================================================*/

package me.kevinboone.suntimes;
import java.util.List;
import java.util.Arrays;

public class CityList
  {
  static List<City> listAsList = Arrays.asList (City.list);

  /** Retrieve a city from its name, in the form "Europe:London" or
        "Europe/London". Note that names are stored with a colon as a
        separator, rather than the more conventional forward slash.
        When used in a REST-based web service, we expect the colon to
        be used, because the forward slash has a particular meaning in
        a REST URL. However, in other circumstances, the forward slash
        might be prefered. */
  public static City getCityByName (String name)
      throws SunTimesException
    {
    if (name == null || name.length() == 0)
      throw new SunTimesException ("City name cannot be null or empty");

    name.replace ("/", ":");

    /* TODO: 
       In a real application, we probably wouldn't enumerate the city
       list on every call to getCityByName(). We'd cache the city somewhere,
       and check the cache first. This is a safe thing to do, as the city
       list is unchanging in the life of an application. Better still,
       we could build the city list itself as a HashSet, not just
       an array. However, City is a computer-generated class, and I
       didn't want to put any program logic in it, as that would have
       to be generated as well.  */  
    for (City c : City.list)
      {
      if (c.name.equals (name)) return c;
      }
 
    throw new SunTimesException ("City '" + name + "' not found");
    }

  /** 
        */
  public static City getCityByPartialName (String name)
      throws SunTimesException
    {
    if (name == null || name.length() == 0)
      throw new SunTimesException ("City name cannot be null or empty");

    name.replace ("/", ":");

    for (City c : City.list)
      {
      if (c.name.toUpperCase().indexOf (name.toUpperCase())>= 0) return c;
      }
 
    throw new SunTimesException ("City '" + name + "' not found");
    }


  /** Get the full list of cities as a List<City>. This list was built
      in a static intializer, so this method incurrs no significant
      overhead at runtime. */
  public static List<City> getListAsList ()
    {
    return listAsList; 
    }

  }

