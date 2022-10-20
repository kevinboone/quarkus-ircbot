/*===========================================================================
 
  MyRouteBuilder.java

  This class defines the Camel routes for quarkus_suntimes.

  Copyright (c)2022 Kevin Boone, GPL v3.0

===========================================================================*/

package me.kevinboone.apacheintegration.quarkus_ircbot;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;


public class MyRouteBuilder extends RouteBuilder 
  {
  /** 
  */
  public void configure() throws Exception 
    {
    // Please note that I have divided the Camel logic into three
    //   separate routes, just to make it easier to follow and maintain.
    // The top-level route consumes from IRC and, when messages are of
    //   the right type, forwards them to direct:privmsg. This route
    //   checks whether the message really is for this bot (ie., it starts
    //   with the keyword) and, if so, invokes direct:for_me.

    // The IRC consumer endpoint will be of the form:
    //   irc:nick@server:port?channels=...
    // All the placeholders in this from() line allow all these settable
    //   parameters to be defined in configuration.
    // The Camel IRC consumed will subscribe to all the channels listed
    //   in the URI. I don't think it's possible for Camel to change the
    //   subscribed channels at runtime.
    from ("{{irc.proto}}:{{irc.mynick}}@{{irc.server}}:{{irc.port}}?channels={{irc.channels}}") 
      .choice()
         // We only handle the PRIVMSG message type in this application.
         // This message type applies to both private and channel messages.
         // Other possibilies include "JOIN" and "KICK".
        .when (header ("irc.messageType").isEqualToIgnoreCase("PRIVMSG"))
        .to ("direct:privmsg")
      .end();
 
    // Handle messages of the right type, ie., PRIVMSG
    // The logic in this route just works out whether the message sent
    //   is actually for this bot. If so, it invokes direct:for_me; if
    //   not, it does nothing at all.
    from ("direct:privmsg")
      .choice()
        // The message is for me if irc.target is my own nick, or...
        .when (simple ("${header.irc.target} == '{{irc.mynick}}'"))
          .to ("direct://for_me")
      .end()
      .choice()
        // ... if the body starts with my keyword
        .when (simple ("${body} startsWith '{{irc.mykey}} '"))
          .to ("direct://for_me")
      .end();

    // The message is for me. Try to process it using the logic in 
    //   IrcBean. 
    from ("direct:for_me")
      .to ("log://foo?showHeaders=true")
      .bean (new IrcBean(), "makeIrcResponse(${body})")

      // If irc.target starts with a '#', the message was sent to
      //   a channel. In that case, we should respond to the channel.
      // Otherwise, it was send to the bot directly; in that case
      //   we should respond to the specific user (irc.user.nick)
      .choice()
	.when (header ("irc.target").startsWith("#"))
	  .setHeader("irc.sendTo").simple ("${header.irc.target}")
	  .to ("{{irc.proto}}:{{irc.mynick}}@{{irc.server}}:{{irc.port}}")
      .otherwise()
	  .setHeader("irc.sendTo").simple ("${header.irc.user.nick}")
	  .to ("{{irc.proto}}:{{irc.mynick}}@{{irc.server}}:{{irc.port}}")
      .end();
    }
  }


