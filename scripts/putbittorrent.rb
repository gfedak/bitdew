#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'

%x(taktuk -d-1 -f nodelist broadcast exec { 'killall java' })
%x(taktuk -d-1 -f nodelist broadcast exec { 'killall bttrack' })
%x(taktuk -d-1 -f nodelist broadcast exec { 'killall /usr/bin/python' })
out  = %x(taktuk -d-1 -f nodelist broadcast exec { 'killall btpd' })
puts "kill btpd #{out}" 
%x(taktuk -d-1 -f nodelist broadcast exec { 'rm -rf *' })
%x(taktuk -d-1 -f nodelist broadcast exec { 'rm -rf * .btpd/torrents' })
iout = IO.popen("cat nodelist").readlines
httprepo = iout[0].slice(/[^\n]*/)
putmachine = iout[1].slice(/[^\n]*/)
getmachine = iout[2].slice(/[^\n]*/)
puts "repo #{httprepo}"
puts "putmachine #{putmachine}"
IO.popen("scp bitdew-stand-alone-0.2.7.jar "+ httprepo+":") do |f|
  f.readlines
end

IO.popen("scp lola.avi sbam_standalone.jar bitdew-stand-alone-0.2.7.jar " + putmachine+":") do |f|
  f.readlines
end

IO.popen("scp -r conf " + putmachine+":") do |f|
  f.readlines
end

IO.popen("scp sbam_standalone.jar bitdew-stand-alone-0.2.7.jar "+getmachine+":") do |f|
	f.readlines
end

Net::SSH.start(httprepo,"jsaray")do|ssh|
  ssh.exec "nohup bttrack.bittorrent --port 6969 --dfile dfile > /home/jsaray/trackerout 2> /home/jsaray/trackererr &"
  ssh.exec "screen -L -dms repo java -jar bitdew-stand-alone-0.2.7.jar serv dc dt dr ds"
end



