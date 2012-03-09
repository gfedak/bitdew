#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'

#
# This script deploy a bittorrent download in a cluster. 
# 
# Prerriquisites : you need a text file named 'nodelist'. First line will be the bitdew http and bittorrent repository,
#                  you need bittorrent libraries installed in all nodes, the image jsaray_squeeze.env was used to launch these experiments.
#                  check the properties file putbittorrent.json in properties_file, and change the http repository according to your nodelist file.
# second line is  the node that contains the file to be put via bittorrent and the rest of lines are the downloading workers.
# Parameters : ARGV[0] is the name of the file you want to use for this test.
#              ARGV[1] is the bittorrent implementation that you want to use : btpd, transmission, utorrent
#


version="0.2.8"
filename = ARGV[0]
protocol = ARGV[1]

if filename.eql? "-h" or filename.eql? "--help" then
  puts "usage : ruby puttbittorrent.rb file_name bittorrent_impl"
  puts "A file nodelist must exist with the nodes making part of your experience"
  exit(0)
end


BITDEW_SERVICES_LAUNCHING_TIME = 60
iout = IO.popen("cat nodelist").readlines
httprepo = iout[0].slice(/[^\n]*/)
putmachine = iout[1].slice(/[^\n]*/)
getmachines = iout[2 .. iout.length-1]
puts "Bitdew Http repository : #{httprepo}"
puts "Node that will put the file :  #{putmachine}"

  %x(taktuk -s -f nodelist broadcast exec { 'killall java' })
  %x(taktuk -s -f nodelist broadcast exec { 'killall bttrack' })
  %x(taktuk -s -f nodelist broadcast exec { 'killall /usr/bin/python' })
  out  = %x(taktuk -s -f nodelist broadcast exec { 'killall btpd' })
  puts "kill btpd #{out}" 
  puts "All java and btpd process were killed"
  
  %x(taktuk -s -f nodelist broadcast exec { 'rm -rf *' })
  %x(taktuk -s -f nodelist broadcast exec { 'rm -rf .btpd/torrents/*' })
  puts "File system in all nodes was purged"
  
  IO.popen("scp bitdew-stand-alone-"+version+".jar "+ httprepo+":") do |f|
    f.readlines
  end
  
  puts "Bitdew jar was sent to the http repository"
  
    IO.popen("scp lola.avi " + putmachine+":") do |f|
      f.readlines
    end
  puts "File #{filename} was sent to repository node"
  
  IO.popen("taktuk -s -f nodelist broadcast put { /home/jsaray/sbam_standalone.jar } { /home/jsaray/sbam_standalone.jar }") do |f|
	  f.readlines
  end
  puts "dessd"
  IO.popen("taktuk -s -f nodelist broadcast put { /home/jsaray/bitdew-stand-alone-"+version+".jar } { /home/jsaray/bitdew-stand-alone-"+version+".jar }") do |f|
	  f.readlines
  end
  
  IO.popen("taktuk -s -f nodelist broadcast put { /home/jsaray/propertiestorrent.json } { /home/jsaray/propertiestorrent.json }") do |f|
      f.readlines
  end

  puts "Attempting to connect to http repository to initiate tracker."  
  Net::SSH.start(httprepo,"jsaray")do|ssh|
    puts "Connection to http repository stablished."    
    ssh.exec "nohup bttrack.bittorrent --port 6969 --dfile dfile > /home/jsaray/trackerout 2> /home/jsaray/trackererr &"
    ssh.exec "nohup java -jar bitdew-stand-alone-"+version+".jar -v --file propertiestorrent.json serv dc dt dr ds > initout 2> initerr &"
  end
  puts "Tracker node starting and bitdew services launching on machine #{httprepo}, this operation normally last #{BITDEW_SERVICES_LAUNCHING_TIME} seconds"
  sleep(BITDEW_SERVICES_LAUNCHING_TIME)
  uid = ""
  puts "Attempting to connect to the put node : #{putmachine}"
  Net::SSH.start(putmachine,"jsaray")do |ssh|
	  puts "Executing a bitdew put "
	  uid = ssh.exec! "nohup java -jar bitdew-stand-alone-"+version+".jar --file propertiestorrent.json put --host "+httprepo+" --protocol bittorrent lola.avi > putout 2> puterr"
	  puts "Retrieving the put output result to parse uid"
	  IO.popen("scp " + putmachine +":putout .") do|f|
	    f.readlines
	  end
	  uid = IO.readlines("putout")[0]
	  puts "The id line is #{uid}"
	  uid = uid.match(/[[a-fA-F0-9-]*]/)[0]
	  uid = uid[1,uid.length-2]
	  puts "The uid is  #{uid}"
  end


  puts "Begin to execute a bittorrent download in the rest of nodes "
  
  md5original = IO.popen("md5sum " + filename) do |f|
   f.readlines
  end
  
  getmachines.each{|machine|
    puts "Launching on machine #{machine}"
    machine = machine.slice(/[^\n]*/)
    begin
    Net::SSH.start(machine,"jsaray") do |ssh|
      
      cmd = "nohup java -jar bitdew-stand-alone-"+version+".jar --file propertiestorrent.json get --verbose --protocol bittorrent --host "+ httprepo +" "+ uid + " > getout 2> geterr &"
      puts "launching on machine #{machine}, command " + cmd
	  ssh.exec cmd
	  puts "done"
    end
    rescue
    puts "Connection could not be stablished on #{machine}"
    end
  }
  
  puts "All machines have begun the downloading process, now let's launch a md5sum pooling thread in each one of them "
  timeini = Time.new          
  md5original = md5original[0].split("\s")[0]
  successclients = 0
  array = Array.new
  getmachines.each{|elem|
    elem = elem.slice(/[^\n]*/)
    a = Thread.new {
      Thread.current[:output] = false
      finish = false
      begin
        Net::SSH.start(elem,"jsaray") do |ssh|
          while !finish do
            if !(protocol.eql? "transmission") 
              result = ssh.exec! "md5sum " + uid
             
            else
              result = ssh.exec! "md5sum Downloads/" + filename
            end
            if protocol.eql? "utorrent"
              running = ssh.exec! "ps -fea | grep utserver"
              lolo = running.split("\n")
              Thread.current[:running] = lolo.length.to_s
              if lolo.length < 3
                Thread.current[:entro] =" ENTROOOOO"
                ssh.exec "nohup ./utserver -configfile conf/settings.conf -daemon > utserverout 2> utservererr &"
              end                       
            end
            Thread.current[:machine] = elem
            result = result.split("\s")[0]
            
            Thread.current[:result] = result
            
            if result.eql? md5original then
              finish = true
              Thread.current[:output] = true
              
            end
          end
        end
      rescue => e
        Thread.current[:output] = true
        puts "Cannot create md5 thread in #{elem}"
        puts e.inspect
	puts e.backtrace
      end
    }
    array << a
  }
  totally = 0
  
  puts "Lets pool for download completion each of the pooling threads"
  
  begin
  while !(array.all? {|el| el[:output] }) do
    totally = array.find_all{ |el| el[:output] == true }.length
    array.each{|el|
      puts "md5 in #{el[:machine]} is #{el[:result]} " 
      
    }
    puts "Number of machines " + totally.to_s
    puts "\n\n\n"
    sleep(1)
    totally = array.find_all{ |el| el[:output] == true }.length
    puts "Number of machines " + totally.to_s
  end
  timefi = Time.new
  total = (timefi - timeini).to_s
  puts "Total elapsed time : #{total}"
  rescue
    timefi = Time.new 
    total = (timefi - timeini).to_s
    puts "Total elapsed time : #{total}"
  end 
