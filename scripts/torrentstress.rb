#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'
require 'thread'
require 'socket'

def torrentDeploy (filename,torrentname,protocol,totalstr,outputfilename)
    
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
            puts "loco iter is #{iter}"
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

          
        
            #First setup the tracker
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
            # One tracker is setup then we proceed to initial seeding
            
            puts "tracker initiated, sending the file to the seeding node, this can take some seconds ..."
            IO.popen("scp " + filename + " " + _firstclient + ":") do |f|
                f.readlines
            end
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
            #Seeding logic, first you send the torrent file and the original file using scp to a node, and then it downloads
            #for the first time, becoming the first seed
            Net::SSH.start(_firstclient,"jsaray") do |ssh|
                
                if !protocol.eql? "snark" or !protocol.eql? "utorrent"
                    ssh.exec! "btmakemetafile.bittorrent " + filename + " http://" + _trackernode +":8000/announce"
                end
                puts "torrent created "
                if protocol.eql? "utorrent" then
    
                        IO.popen("scp -r utorrent-server-v3_0/* " + _firstclient + ":")do |f|
                            f.readlines
                        end
                        IO.popen("scp -r twistedfall-utorrentctl-0c0ed02/* " + _firstclient+":")do |f|
                            f.readlines
                        end
                        
    
                   puts "launching daemon ..."
                   ssh.exec "./utserver -configfile conf/settings.conf -daemon"
                   puts "Daemon launched"
                   comm = ssh.exec! "python3 utorrentctl.py -H " + _firstclient + ":8080 -U admin -P admin -F --add-url " + trackerurl
                   puts "Add torrent response " + comm
                   fini=false
                   alldata=""
                   while (!fini)
                       ssh.exec! "python3 utorrentctl.py -H " + _firstclient + ":8080 -U admin -P admin -l" do |ch,stream,data|
                       alldata= alldata + data
                       end
                       puts "Seeding, response from seeder is " + alldata
                       if alldata.include? "100.0%"
                           fini=true
                       end
                       sleep(1)
                   end
                   
                elsif protocol.eql? "btpd"
                    ssh.exec! "btpd"
                    ssh.exec! "btcli add -d /home/jsaray " + torrentname
                    finish = false
                    while !finish do     
                        lines = ssh.exec! "btcli list"
                        lines = lines.split("\n")
                        cols = lines[1].split("\s")  
                        if cols[3].include? "100.0%"then
                            finish = true
                        end
                    end
                    
                elsif protocol.eql? "transmission"
                    puts "Enter in transmission"
                    ssh.exec! "transmission-remote -a " + torrentname
                    progress = "a"
                    while !(progress.include? "100") do
                        progress = ssh.exec! "transmission-remote -l "
                        prline = progress.split("\n")[1]
                        puts "prline is #{prline}"
                        sleep(3)
                    end
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
               puts "seeding finished in node " + _firstclient
            end
    
            if !protocol.eql? "snark" and !protocol.eql? "utorrent"
                IO.popen("scp " + _firstclient + ":" + torrentname + " .") do |f|
                    f.readlines
                end
                IO.popen("taktuk -d-1 -f nodelist broadcast put { /home/jsaray/" + torrentname +" } { /home/jsaray/" + torrentname+ " }")do|f|
                    f.readlines
                end
            end
            
            timeini = Time.new
           
            puts "The downloading clients will be " + allclients.to_s
            
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
    
            md5original = IO.popen("md5sum " + filename) do |f|
                f.readlines
            end
            
            md5original = md5original[0].split("\s")[0]
            successclients = 0
            array = Array.new
            allclients.each{|elem|
                elem = elem.slice(/[^\n]*/)
                a = Thread.new {
                    Thread.current[:output] = false
                    finish = false
                    begin
                    Net::SSH.start(elem,"jsaray") do |ssh|
                        
                        while !finish do
                        
                            
                            if !(protocol.eql? "transmission") 
                                result = ssh.exec! "md5sum " + filename
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
                    rescue
                        Thread.current[:output] = true
                        puts "Cannot create md5 thread in #{elem}"
                    end
                }
                array << a
            }
            totally = 0
            while totally < allclients.length do
                totally = 0
                array.each {|el|
                puts el[:machine].to_s + " " + el[:result].to_s + " "+ el[:output].to_s+ el[:entro].to_s + " "+ el[:running].to_s
                if el[:output] then
                    totally = totally + 1
                end
                
                }
                puts "Number of machines " + totally.to_s
                puts "\n\n\n"
                sleep(1)
            end
            timefi = Time.new
            toti = (timefi - timeini).to_s
            puts "Total miliseconds " + (timefi - timeini).to_s
            fichier.puts toti + "\n"  
            puts "Iteration number " + iter.to_s       
        end
    end

end