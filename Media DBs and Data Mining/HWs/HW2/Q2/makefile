
# AUTHORS: Yuang Liu and Christos Faloutsos
# DATE: Jan. 2017

SRC= zorder.c zdist.c zplot.c horder.c hdist.c hplot.c
HFILES= util.h horder.h zorder.h

all: hw2

hw2: plot test-z test-h

compile: $(SRC) $(HFILES)
	gcc -o zdist zdist.c zorder.c -lm
	gcc -o zplot zplot.c zorder.c -lm
	gcc -o hdist hdist.c horder.c -lm
	gcc -o hplot hplot.c horder.c -lm

test-z: compile script-z.bash
	bash script-z.bash

test-h: compile script-h.bash
	bash script-h.bash

plot: compile
	./zplot
	./hplot
	gnuplot < cmd.plt

clean:
	\rm -f zdist zplot hdist hplot *.dat *.png

spotless: clean
