#
# GNU make style makefile for package 
#
# This makefiles is designed to be run by the main
# Relay makefile in the Relay root directory.
# 

CLASSES = \
   IComparable.class \
   ComparableString.class \
   QuickSort.class \
   RCTest.class

package: $(CLASSES)

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $<

