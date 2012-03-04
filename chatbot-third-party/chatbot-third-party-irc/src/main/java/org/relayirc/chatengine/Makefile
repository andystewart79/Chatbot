#
# GNU make style makefile for package 
#
# This makefiles is designed to be run by the main
# Relay makefile in the Relay root directory.
#
	
CLASSES = \
   ChatOptions.class \
   Channel.class \
   ChannelAdapter.class \
   ChannelEvent.class \
   ChannelListener.class \
   ChannelSearch.class \
   ChannelSearchListener.class \
   ChatEngine.class \
   ChatEngineEvent.class \
   ChatEngineListener.class \
   ChatOptionListener.class \
   IChatApp.class \
   IChatEngine.class \
   IRCConnection.class \
   IRCConnectionAdapter.class \
   IRCConnectionListener.class \
   Server.class \
   ServerList.class \
   User.class \
   IdentServer.class

package: $(CLASSES)

.DEFAULT: package_relay

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $<

