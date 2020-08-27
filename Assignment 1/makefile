JCC = javac
JCR = java
JFLAGS = -g

all:
	mkdir -p bin
	$(JCC) $(JFLAGS) src/*.java  -d bin
	$(JCR) -Xmx256m -cp  bin TerrainClassifierHub

clean:
	$(RM) bin/*.class
