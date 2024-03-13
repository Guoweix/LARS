
JAVAC:=javac
FLAGS:=-Xlint:unchecked -Xlint:deprecation

SRC_DIR := ./

JAVA_FILES := $(shell find $(SRC_DIR) -name "*.java")

CLASS_FILES := $(patsubst %.java,%.class,$(JAVA_FILES))

# class_files:
	# echo $(CLASS_FILES)
	
Mars.jar: all
	jar cmf mainclass.txt Mars.jar PseudoOps.txt Config.properties Syscall.properties Settings.properties mainclass.txt MipsXRayOpcode.xml registerDatapath.xml controlDatapath.xml ALUcontrolDatapath.xml  Mars.java Mars.class images mars 


all: $(CLASS_FILES)

%.class: $(SRC_DIR)/%.java
	$(JAVAC) $(FLAGS) $<



run: 
	java -jar Mars.jar

clear : clear.sh
	./clear.sh