PACKAGE= kdtree1_1
MAIN= ./main
CFLAGS=-g -pedantic -DNDEBUG # -DDEBUG # -DNDEBUG
SRC= kdtree.c vector.c
OBJ= kdtree.o vector.o
SRCALL= $(MAIN).c $(SRC)
ALLH= dfn.h kdtree.h vector.h
CC= gcc
SCRIPTS= test1.txt test2.txt test3.txt
COPYRIGHT= COPYRIGHT
REV=
.IGNORE:
# .SILENT:
LPR= lpr

all: hw2

demo: $(MAIN) input.script
	echo "**** DEMO ***"
	$(MAIN) -d 4 < input.script

hw2: $(MAIN)
	echo "**** test1 *** "
	$(MAIN) -d 2 < test1.txt
	# echo "**** test2 *** "
	# $(MAIN) -d 2 < test2.txt
	# echo "**** test3 *** "
	# $(MAIN) -d 3 < test3.txt
	# echo "**** test finished *** "

largedemo: $(MAIN) inp-large.script
	echo " ***large demo***"
	$(MAIN) -d 2 < inp-large.script

$(MAIN): $(MAIN).o $(OBJ)
	$(CC) $(CFLAGS) -o $(MAIN) $(MAIN).o $(OBJ) -lm

p$(MAIN): $(MAIN).o $(OBJ)
	purify $(CC) $(CFLAGS) -o $(MAIN) $(MAIN).o $(OBJ) -lm

vector.o: dfn.h vector.h

kdtree.o: dfn.h kdtree.h vector.h

$(MAIN).o: dfn.h kdtree.h vector.h kdtree.c

checkout:
	co -l $(REV) $(SRCALL) $(ALLH) README 

checkin:
	ci -l $(REV)  -f $(SRCALL) $(ALLH) README

cleanup:
	rm -f $(OBJ) $(MAIN).o core
	rm -f *~ *.bak

clean: cleanup

spotless: clean
	rm -f $(MAIN)

$(PACKAGE).tar: $(SRCALL) $(ALLH) README makefile \
	$(SCRIPTS) $(COPYRIGHT)
	tar cvfh $(PACKAGE).tar $(SRCALL) $(ALLH) \
	$(SCRIPTS) README makefile $(COPYRIGHT)

printout: $(SRCALL) $(ALLH)
	$(LPR) $(SRCALL) $(ALLH)