
(add-to-list 'load-path (expand-file-name "~/.emacs.d/site-lisp/muse"))

(require 'muse-mode)     ; load authoring mode
;(require 'muse-build)
(require 'muse-html)     ; load publishing styles I use
(require 'muse-colors)   ; load coloring/font-lock module
(require 'muse-project)
;(require 'muse-pdf)
(require 'muse-latex)
(require 'muse-texinfo)
(require 'muse-publish)
;(require 'muse-autoload)
(require 'muse-wiki)     ; load Wiki support

(custom-set-variables
 '(muse-html-style-sheet "<link rel=\"stylesheet\" type=\"text/css\" charset=\"utf-8\" href=\"stylesheet/site.css\" />")
 '(muse-html-footer "footer.html") 
 '(muse-html-header "header.html")
)