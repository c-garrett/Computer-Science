##################
# Authors: Yuang Liu and Christos Faloutsos
# Date: Jan. 2017
##################

demo:
	@echo ""
	@echo "--- Q1 ---"
	cd Q1; make -s
	@echo ""
	@echo "--- Q2 ---"
	cd Q2; make -s
	@echo ""
	@echo "--- Q3 ---"
	cd Q3; make -s

hw2: hw2q1 hw2q2 hw2q3

hw2q1: 
	@echo ""
	@echo "------- HW2 Q1 ------"
	cd Q1; make -s hw2

hw2q2:
	@echo ""
	@echo "------- HW2 Q2 ------"
	cd Q2; make -s hw2

hw2q3:
	@echo ""
	@echo "------- HW2 Q3 ------"
	cd Q3; make -s hw2

spotless:
	\rm -f *hw2.tar.gz
	cd Q1; make spotless
	cd Q2; make spotless
	cd Q3; make spotless

package: hw2.tar.gz
	mv hw2.tar.gz `whoami`-hw2.tar.gz
	@echo ""
	@echo "--------------------"
	@echo "IMPORTANT: CHECK that your andrew login"
	@echo "     is prepended to the file name hw2.tar.gz"
	@echo "--------------------"

hw2.tar: spotless
	tar cvf hw2.tar Q1 Q2 Q3 makefile 

hw2.tar.gz: hw2.tar
	gzip hw2.tar

