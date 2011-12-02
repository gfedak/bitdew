#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'
require 'thread'
require 'socket'

=begin
This function trigger the deployment of n files, parameters are as follows :
  filename : list of files comma separated
  protocol : the type of bittorrent implementation, currently suppored bt, btpd, utorrent, transmission
  totalstr : a string containing the number of times we will perform the experiment
  outputfilename : the name we want for the output file
=end
def torrentDeploy (filestodownload,protocol,totalstr,outputfilename)
     
    filestodownload = filestodownload.split(",")
    puts "Files are " + filestodownload.to_s + "files size is : #{filestodownload.length}"
    puts "TorrentStress usage : ruby torrentstress.rb <filename> <torrentname> <protocol> <number_of_tries> <output_file>"
    total = totalstr.to_i
    trackerurl=""
      
    File.open(outputfilename,'w') do |fichier|
        iout = IO.popen("cat nodelist")
        iter=0
        allhosts = iout.readlines
        allclients = allhosts[2 .. allhosts.length]
        _firstclient = allhosts[1].slice(/[^\n]*/)
        _trackernode = allhosts[0].slice(/[^\n]*/)
        
        while iter < total do
            puts "Number of iterations  #{total}" 
            puts "Iteration number is #{iter}"
            iter=iter+1
            %x(taktuk -d-1 -f nodelist broadcast exec { 'rm -rf *' })       
            %x(taktuk -d-1 -f nodelist broadcast exec { 'killall btpd' })
            %x(taktuk -d-1 -f nodelist broadcast exec { 'killall /usr/bin/python' })       
            %x(taktuk -d-1 -f nodelist broadcast exec { 'killall utserver' })
            %x(taktuk -d-1 -f nodelist broadcast exec { 'killall screen' })
            %x(taktuk -d-1 -f nodelist broadcast exec { 'killall SCREEN' })
            %x(taktuk -d-1 -f nodelist broadcast exec { 'killall -s SIGKILL java' })
            %x(taktuk -d-1 -f nodelist broadcast exec { 'killall -s SIGKILL transmissioncli' })
            %x(taktuk -d-1 -f nodelist broadcast exec { 'killall -s SIGKILL transmission-daemon' })
            
            puts "Tracker running on " + _trackernode
            puts "Sharing client running on " + _firstclient
            IO.popen("taktuk -d-1 -f nodelist broadcast exec { 'rm -rf * .btpd/torrents' }")do |f|
                f.readlines
            end

          #============== Tracker setup ========================================
          Net::SSH.start(_trackernode,"jsaray") do |ssh|
               #Snark tand utorrent are easier to deploy with snark tracker
                if !protocol.eql? "snark" and !protocol.eql? "utorrent"           
                    ssh.exec "nohup bttrack.bittorrent --port 8000 --dfile dfile > /home/jsaray/trackerout 2> /home/jsaray/trackererr &" 
                end
                #For this purpose snark jar has to be in the tracker node
                if protocol.eql? "snark" or protocol.eql? "utorrent" then
                    %x(taktuk -d-1 -f nodelist broadcast put { /home/jsaray/snark.jar } { /home/jsaray/snark.jar })
                end
                # Snark tracker procedure
                if protocol.eql? "utorrent" or protocol.eql? "snark"
                    IO.popen("scp " + filename + " " + _trackernode + ":")do |f|
                        f.readlines
                    end
                    ssh.exec! "screen -L -dmS sharing java -jar snark.jar --share "+ _trackernode + " " + filename
                    sleep(3)
                   grepa = ssh.exec! "grep 'Torrent available on' screenlog.0"
                   sleep(2)
                   while grepa == nil 
                       grepa = ssh.exec! "grep 'Torrent available on' screenlog.0"
                       puts "en el while grepa es " + grepa.to_s
                       sleep(2)
                   end
                   puts "Tracker output is " + grepa
                   trackerurl = grepa.split("\s")[3]
                   puts "Tracker url is " + trackerurl.to_s
                end               
          end
          # Send files to the seeding node just the first time in order to save time
          puts "tracker initiated, sending files to the seeding node, this can take some seconds ..."
          
          filestodownload.each{|filename|
            IO.popen("scp " + filename + " " + _firstclient + ":") do |f|
              f.readlines
            end
          }
          
          puts "Sending daemon and clients files "
          #Send daemon and client to all client nodes if utorrent
          if protocol.eql? "utorrent"
            allclients.each{|node|
              node = node.slice(/[^\n]*/)
              puts "Node " + node
              IO.popen("scp -r utorrent-server-v3_0/* " + node + ":")do |f|
                f.readlines
              end
              IO.popen("scp -r twistedfall-utorrentctl-0c0ed02/* " + node+":")do |f|
                f.readlines
              end
              puts "Attempting ssh connection"    
              Net::SSH.start(node,"jsaray",:keys => [privatekey], :auth_methods => ["publickey"]    )do |ssh|
                ssh.exec "nohup ./utserver -configfile conf/settings.conf > utserverout 2> utservererr &"
              end
              puts "Releasing ssh connection "
            }
          end
          
          puts "Files sent, now seeding"
          torrents = []
          #Seeding logic, first you send the torrent file and the original file using scp to a node, and then it downloads
          #for the first time, becoming the first seed
          Net::SSH.start(_firstclient,"jsaray") do |ssh|
                            
              filestodownload.each{|filename|
                torrentname =  filename +".torrent"
                torrents << torrentname
                puts "seeding file " + filename
                if !protocol.eql? "snark" or !protocol.eql? "utorrent"
                  com = "btmakemetafile.bittorrent " + filename + " http://" + _trackernode +":8000/announce --target " + torrentname
                  puts "command to exec is " + com
                  ssh.exec! com
                end
                if protocol.eql? "btpd"
                  ssh.exec! "btpd"
                  ssh.exec! "btcli add -d /home/jsaray " + torrentname
                  finish = false
                  while !finish do     
                    lines = ssh.exec! "btcli list"
                    puts "Line is #{lines}"
                    lines = lines.split("\n")
                    cols = lines[1].split("\s")  
                    if cols[3].include? "100.0%"then
                      finish = true
                    end
                  end
                  puts "Seeding finished ."   
                elsif protocol.eql? "bt"
                  puts "protocol is bt"
                  ssh.exec! "nohup btdownloadheadless.bittorrent " + torrentname +" > seedout 2> seederr &"
                  output = nil
                  while output ==  nil do
                    output = ssh.exec! "grep 'Download Succeeded' seedout"
                    puts "File is seeding ... " + output.to_s
                    sleep(1)
                  end
                end
              }
              puts "torrents created "
            
            puts "seeding finished in node " + _firstclient
          end
            
          puts "Torrent list is #{torrents.length}"
          torrents.each{|torrentname|
            #Send the torrent file to all the nodes
            if !protocol.eql? "snark" and !protocol.eql? "utorrent"
              IO.popen("scp " + _firstclient + ":" + torrentname + " .") do |f|
                f.readlines
              end
              IO.popen("taktuk -d-1 -f nodelist broadcast put { /home/jsaray/" + torrentname +" } { /home/jsaray/" + torrentname+ " }")do|f|
                f.readlines
              end
            end
            puts "Downloading torrent #{torrentname}"
            allclients.each { |elem|
              elem = elem.slice(/[^\n]*/)
              if !protocol.eql? "utorrent"
                begin
                  Net::SSH.start(elem,"jsaray") do |ssh|
                    if protocol.eql? "btpd"
                      ssh.exec! "btpd"
                      ssh.exec! "btcli add -d /home/jsaray "+  torrentname + " &"
                    elsif protocol.eql? "bt"
                      puts "downloading using bt"
                      ssh.exec  "nohup btdownloadheadless.bittorrent " + torrentname +" > downout 2> downerr &"
                    elsif protocol.eql? "transmission"
                      ssh.exec! "transmission-daemon --config-dir /var/lib/transmission-daemon/info"
                      ssh.exec "nohup transmissioncli " + torrentname + " > outi > erri &"
                    elsif protocol.eql? "snark"
                      ssh.exec "screen -L -dmS download java -jar snark.jar "+trackerurl    
                    end
                  end
                rescue
                  puts "I cant contact the host #{elem}"
                end                      
              else
                #Special case for utorrent as it needs a python library.
                Net::SSH.start(_firstclient,"jsaray") do |ssh|
                  ssh.exec "python3 utorrentctl.py -H " + elem + ":8080 -U admin -P admin -F --add-url " + trackerurl
                end
              end
            }
          }
          md5s = []
          puts "get out torrent cycle"
          filestodownload.each{|filename|  
            md5original = IO.popen("md5sum " + filename) do |f|
              f.readlines
            end
            md5original = md5original[0].split("\s")[0]
            md5s << {"name"=> filename, "md5"=> md5original}
          }
          puts "md5array is " + md5s.to_s
          array = Array.new
          #Build an independent thread that goes to a machine to query if the download has finished
          allclients.each{|elem|
           
            elem = elem.slice(/[^\n]*/)
            a = Thread.new {
              Thread.current[:output] = false
              finish = false
              begin
                Net::SSH.start(elem,"jsaray") do |ssh|                        
                  filestodownload.each{  |filename|
                   
                    while !finish do
                      if !(protocol.eql? "transmission") 
                        result = ssh.exec! "md5sum " + filename
                      else
                        result = ssh.exec! "md5sum Downloads/" + filename
                      end
                      Thread.current[:machine] = elem
                      result = result.split("\s")[0]
                      puts "result is " + result
                      if result.eql? md5s[filename] then
                        finish = true
                       
                      else
                       
                      end
                    end
                  }
                  Thread.current[:output] = true
                end
              rescue
                Thread.current[:output] = true
                puts "Cannot create md5 thread in #{elem}"
              end
            }
            array << a
          }
            
            totally = 0
            while !(array.all? {|el| el[:output] }) do
              
              totally = array.find_all{ |el| el[:output] == true }.length
              puts "Number of machines " + totally.to_s
              puts "\n\n\n"
              sleep(1)
              totally = array.find_all{ |el| el[:output] == true }.length
              puts "Number of machines " + totally.to_s
            end
            puts "Iteration number " + iter.to_s
            
            
          
          
        end
      end
end
torrentDeploy("blast.linux-x86_64.tar,js_im_bittornado.tgz,onehundred.tar,sts.tar.gz,twohundred.tar,unittest.zip","btpd","1","tinton")