cp doxygen/latex/userguide.toc doxygen/latex/tmp.toc
csplit  doxygen/latex/tmp.toc "/Class Documentation/"
cp xx00 doxygen/latex/userguide.toc
head -n1 xx01 >>  doxygen/latex/userguide.toc
#rm xx00 xx01 doxygen/latex/tmp.toc
