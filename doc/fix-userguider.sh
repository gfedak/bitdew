cp userguide-header.tex doxygen/latex/userguide.tex
echo "\chapter{BitDew: An Open Source Middleware for Large Scale Data Management}" >> doxygen/latex/userguide.tex
grep group__ doxygen/latex/refman.tex >> doxygen/latex/userguide.tex
echo "\chapter{Example Documentation}"  >> doxygen/latex/userguide.tex
grep java-example doxygen/latex/refman.tex >> doxygen/latex/userguide.tex
echo \chapter{Class Index}

\input{namespaces}
\input{hierarchy}
\input{annotated}
\input{files}
#rm xx00 xx01 doxygen/latex/tmp.toc
