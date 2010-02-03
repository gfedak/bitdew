all: doxygen latex

doxygen:
	echo "doxygen here"

latex: doxygen/latex/refman.tex
	echo "patching latex dir"
	perl patch-latex.pl
	touch doxygen/latex/refman.tex
	make -k doxygen/latex
