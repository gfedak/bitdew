#!/usr/bin/perl -w
#
# -i.orig -p
use strict ;

my $google_analytics = <<"FIN";
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "htt
p://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.
js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-10061360-1");
pageTracker._trackPageview();
} catch(err) {}</script> 
</body>
FIN
 
sub patch
{
  my $dossier=shift;
 
  my $prot_dossier=quotemeta($dossier);
 
  if ( -d $dossier ) {
    foreach (glob ($prot_dossier."/*")) {
      patch ($_) ;
    }
    print "Patch directory ($dossier)\n";
  } else {
      print "Patch file ($dossier)\n";
      open (OLD, "< $dossier") or die "cannot open $dossier : $!";
	  open (NEW, "> tmp.html") or die "cannot open $dossier : $!";
      while (<OLD>) {
	  s/<\/body>/$google_analytics/g;
	  print NEW $_ or die "cannot write : $!";
      }
      close (NEW) or die "cannot close : $!";
      close (OLD)  or die "cannot close : $!";
      rename ("tmp.html", $dossier) or die "cannot rename : $!";
  }
}
 

patch ("doxygen/html");

 
