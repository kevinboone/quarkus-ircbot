/*===========================================================================
 
  SunTimesWrapper.java

  Copyright (c)2022 Kevin Boone, GPL v3.0

===========================================================================*/

package me.kevinboone.apacheintegration.quarkus_ircbot;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.ConfigProvider;
import java.util.Calendar; 
import java.util.TimeZone;
import me.kevinboone.suntimes.*;
import java.text.SimpleDateFormat;

@RegisterForReflection
/**
  This class interfaces between the Camel IRC route, and the mathematics
  in the suntimes package. It takes care of locating a city that matches
  the user's input, invoking the sunrise/set calculation, and formatting
  the results for the city's local timezone.
*/
public class SunTimesWrapper 
  {
  static TimeZone gmtTZ = TimeZone.getTimeZone ("GMT");

  /** Make response returns a string for display to the user. The string
      might be an error message if, for example, the input does not 
      specify a City in the database. At present, only a city can be
      input - I use the term 'request' for the argument because,
      in principle, there could be any kind of input to an IRC bot.
      IRC being what it is, the output is limited to one line of up to 256
      bytes. */
  public static String makeResponse (String request)
    {
    if (request.equalsIgnoreCase ("help"))
      {
      return makeHelpMessage();
      }

    String cityName = request;

    City city;
    try
      {
      city = CityList.getCityByPartialName 
        (cityName.replace ("/",":"));
      } 
    catch (SunTimesException e)
      {
      // Don't carry on if we don't have a valid city.
      return e.getMessage();
      }

    String properCityName = city.name;
    TimeZone localTZ = TimeZone.getTimeZone 
      (properCityName.replace (":", "/"));

    Calendar cal = Calendar.getInstance (gmtTZ);

    int year = cal.get (Calendar.YEAR);  
    // Calendar's month field is zero-based, for some reason 
    int month = cal.get (Calendar.MONTH) + 1;  
    int day = cal.get (Calendar.DAY_OF_MONTH);  

    double longt = city.longt;
    double lat = city.lat;

    SimpleDateFormat df = new SimpleDateFormat("HH:mm z"); 
    df.setTimeZone (localTZ);

    String riseStr; 
    String setStr; 
    try
      {
      Time rise = Calculator.getSunriseTimeUTC_z (year, month, day,
        longt, lat, Calculator.ZENITH);
      riseStr = doFormat (cal, rise, df); 
      } catch (SunTimesException dummy) { riseStr = "No sunrise";  }

    try
      {
      Time set = Calculator.getSunsetTimeUTC_z (year, month, day,
        longt, lat, Calculator.ZENITH);
      setStr = doFormat (cal, set, df); 
      } catch (SunTimesException dummy) { setStr = "No sunset";  }

    return properCityName.replace (":", "/") + 
       ": Sunrise: " + riseStr + ", sunset: " + setStr;
    }

  /** Helper method to tidy up the time formatting. */
  static String doFormat (Calendar cal, Time time, SimpleDateFormat df)
    {
    cal.set (Calendar.HOUR, time.getHour());
    cal.set (Calendar.MINUTE, time.getMinute());
    cal.set (Calendar.AM_PM, Calendar.AM);
    cal.set (Calendar.SECOND, 0);
    return df.format (cal.getTime());
    }

  static String makeHelpMessage ()
    {
    return "Send a city name like 'London', 'new_york', 'kolkata', or " 
       + "a timzone specifier like 'Europe/London'. Names are not "
       + "case-sensitive."; 
    }

  }


