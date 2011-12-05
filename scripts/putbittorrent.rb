#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'


iout = IO.popen("cat nodelist").readlines
httprepo = iout[0].slice(/[^\n]*/)
putmachine = iout[1].slice(/[^\n]*/)
getmachines = iout[2 .. iout.length-1]
puts "repo #{httprepo}"
puts "putmachine #{putmachine}"

  %x(taktuk -d-1 -f nodelist broadcast exec { 'killall java' })
  %x(taktuk -d-1 -f nodelist broadcast exec { 'killall bttrack' })
  %x(taktuk -d-1 -f nodelist broadcast exec { 'killall /usr/bin/python' })
  out  = %x(taktuk -d-1 -f nodelist broadcast exec { 'killall btpd' })
  puts "kill btpd #{out}" 
  %x(taktuk -d-1 -f nodelist broadcast exec { 'rm -rf *' })
  %x(taktuk -d-1 -f nodelist broadcast exec { 'rm -rf * .btpd/torrents' })
  IO.popen("scp bitdew-stand-alone-0.2.7.jar "+ httprepo+":") do |f|
    f.readlines
  end
  
  IO.popen("scp lola.avi sbam_standalone.jar bitdew-stand-alone-0.2.7.jar " + putmachine+":") do |f|
    f.readlines
  end
  
  
  IO.popen("taktuk -d-1 -f nodelist broadcast put { /home/jsaray/sbam_standalone.jar } { /home/jsaray/sbam_standalone.jar }") do |f|
	  f.readlines
  end
  
  IO.popen("taktuk -d-1 -f nodelist broadcast put { /home/jsaray/bitdew-stand-alone-0.2.7.jar } { /home/jsaray/bitdew-stand-alone-0.2.7.jar }") do |f|
	  f.readlines
  end
  
  Net::SSH.start(httprepo,"jsaray")do|ssh|
    ssh.exec "nohup bttrack.bittorrent --port 6969 --dfile dfile > /home/jsaray/trackerout 2> /home/jsaray/trackererr &"
    ssh.exec "nohup java -jar bitdew-stand-alone-0.2.7.jar -v serv dc dt dr ds > initout 2> initerr &"
  end
  puts "sleeping ; wait "
  sleep(20)
  uid = ""
  Net::SSH.start(putmachine,"jsaray")do |ssh|
	  puts "executing put "
	  uid = ssh.exec! "nohup java -jar bitdew-stand-alone-0.2.7.jar put --host "+httprepo+" --protocol bittorrent lola.avi > putout 2> puterr"
	  puts "retrieving output to parse uid"
	  IO.popen("scp " + putmachine +":putout .") do|f|
	    f.readlines
	  end
	  puts "parsing uid"
	  uid = IO.readlines("putout")[1]
	  puts "The id line is #{uid}"
	  uid = uid.match(/[[a-fA-F0-9-]*]/)[0]
	  uid = uid[1,uid.length-2]
	  puts "The uid is  #{uid}"
  end


  #puts "begin to execute in a lot of machines"
#  getmachines.each{|machine|
#      machine = machine.slice(/[^\n]*/)
#      Net::SSH.start(machine,"jsaray") do |ssh|
#      puts "machine #{machine}"
#	  ssh.exec "nohup java -jar bitdew-stand-alone-0.2.7.jar get --protocol bittorrent --host "+ httprepo +" "+ uid + " > getout 2> geterr &"
#	puts "done"
#      end
#      sleep(60)
#  }
