/*===========================================================================
 
  IrcBean.java

  Copyright (c)2022 Kevin Boone, GPL v3.0

===========================================================================*/

package me.kevinboone.apacheintegration.quarkus_ircbot;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.ConfigProvider;

@RegisterForReflection
/**
IRCBean is invoked by the Camel route when new input arrives for the
IRC bot. This input may have been directed to the bot itself, or to
a channel to which it is subscribed. This class is generic -- there
is nothing specific to the application itself. The actual
application-specific formatting is in the SunTimesWrapper class.
These two classes could easily be combined, since they are so simple.
However, I wanted to separate the general IRC logic from the
application-specific log for clarity.
*/
public class IrcBean 
  {
  private String makeResponse (String request)
    {
    return SunTimesWrapper.makeResponse (request); 
    }

  /** Makes a response from a raw IRC message body. This may need to
        to be processed to remove some IRC-specific stuff, to reveal
        the actual command/request string. The real work is done
        by makeResponse(). */
  public String makeIrcResponse (String body)
    {
    /* If we receive a message sent to a channel, rather than a 
         a private message to this bot, then the message will begin
         with the keyword that invoked the bot. It should not be
         possible for execution to get to this point without the
         keyword. 
       If the message was sent directly to the bot, it may -- or may 
         not -- begin with the keyword. In either case, we must strip off 
         the keyword, and the space that follows
         it, to reveal the actual command or request. */
    String myKey = ConfigProvider.getConfig().getValue
      ("irc.mykey", String.class);
    if (body.startsWith (myKey))
      body = body.substring (myKey.length()).trim();
    return makeResponse (body);
    }
  }




