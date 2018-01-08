set terminal png
set xtics 1
set ytics 1
unset xtics
unset ytics
unset key
unset border
set pointsize 1

set output "zcurve.png"
plot "zpoint.dat" using 1:2 with linespoints pointtype 0 
pause 0 "   FYI: gnuplot is done with zcurve - see file zcurve.png"

set output "hcurve.png"
plot "hpoint.dat" using 1:2 with linespoints pointtype 0
pause 0 "   FYI: gnuplot is done with hcurve - see file hcurve.png"
