/*===========================================================================
 
  SunTimesException.java

  Copyright (c)2022 Kevin Boone, GPL v3.0

===========================================================================*/

package me.kevinboone.suntimes;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SunTimesException extends Exception
  {
  public SunTimesException (String s)
    {
    super (s);
    } 
  }
