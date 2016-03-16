# Java makefile
# credit: https://www.devin.com/cruft/javamakefile.html
JAVAC=javac
sources = $(wildcard *.java)
classes = $(sources:.java=.class)
CP = -cp lib/*:.

all: $(classes) 

clean :
	rm -f *.class

%.class : %.java
	$(JAVAC) $< $(CP)
