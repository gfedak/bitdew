all: index.html example.html stylesheet/site.css footer.html header.html
SITE=stylesheet/site.css footer.html header.html

%.html: %.muse 
	emacs --no-site-file -q -batch -l muse-makefile.el -f muse-batch-publish-files html --output-dir=. $?

%.texi: %.muse
	emacs --no-site-file -q -batch -l muse-makefile.el -f muse-batch-publish-files texi --output-dir=. $?

%.pdf: %.muse
	emacs --no-site-file -q -batch -l muse-makefile.el -f muse-batch-publish-files pdf --output-dir=. $?
