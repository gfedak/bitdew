#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'
require 'thread'
filename = ARGV[0]
torrentname = ARGV[1]
protocol = ARGV[2]
total = ARGV[3]
total = total.to_i
outputfilename = ARGV[4]
File.open(outputfilename,'w') do |fichier|

    iter=0
    while iter < total do
        iter=iter+1
        
        iout = IO.popen("cat nodelist")
        allhosts = iout.readlines
        allclients = allhosts[2 .. allhosts.length]
        _trackernode = allhosts[0].slice(/[^\n]*/)
        _firstclient = allhosts[1].slice(/[^\n]*/)
        puts "Tracker running on " + _trackernode
        puts "Sharing client running on " + _firstclient
        IO.popen("taktuk -d-1 -f nodelist broadcast exec { 'rm -rf * .btpd/torrents' }")do |f|
            f.readlines
        end
        %x(taktuk -d-1 -f nodelist broadcast exec { 'rm -rf *' })
        %x(taktuk -d-1 -f nodelist broadcast exec { 'killall btpd' })
        %x(taktuk -d-1 -f nodelist broadcast exec { 'killall /usr/bin/python' })
        
        Net::SSH.start(_trackernode,"jsaray") do |ssh|
            ssh.exec "nohup bttrack.bittorrent --port 8000 --dfile dfile > /home/jsaray/trackerout 2> /home/jsaray/trackererr &" 
        end
        puts "tracker initiated"
        IO.popen("scp " + filename + " " + _firstclient + ":") do |f|
            f.readlines
        end
        puts "File " + filename + "copied to client " + _firstclient
        Net::SSH.start(_firstclient,"jsaray") do |ssh|
           ssh.exec! "btmakemetafile.bittorrent " + filename + " http://" + _trackernode +":8000/announce"
           puts "torrent created "
           if protocol.eql? "btpd"
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
        
        IO.popen("scp " + _firstclient + ":" + torrentname + " .") do |f|
            f.readlines
        end
        IO.popen("taktuk -d-1 -f nodelist broadcast put { /home/jsaray/" + torrentname +" } { /home/jsaray/" + torrentname+ " }")do|f|
            f.readlines
        end
        timeini = Time.new
        
        puts "The downloading clients will be " + allclients.to_s
        
        allclients.each { |elem|
           elem = elem.slice(/[^\n]*/)
           Net::SSH.start(elem,"jsaray") do |ssh|
                if protocol.eql? "btpd"
                    ssh.exec! "btpd"
                    ssh.exec! "btcli add -d /home/jsaray "+  torrentname + " &"
                elsif protocol.eql? "bt"
                    puts "downloading using bt"
                    ssh.exec  "nohup btdownloadheadless.bittorrent " + torrentname +" > downout 2> downerr &"
                end
           end
        }

        md5original = IO.popen("md5sum " + filename) do |f|
            f.readlines
        end
        
        md5original = md5original[0].split("\s")[0]
        
        array = Array.new
        allclients.each{|elem|
            elem = elem.slice(/[^\n]*/)
            a = Thread.new {
                Thread.current[:output] = false
                finish = false
                while !finish do
                    Net::SSH.start(elem,"jsaray") do |ssh|
                    result = ssh.exec! "md5sum " + filename
                    Thread.current[:machine] = elem
                    result = result.split("\s")[0]
                    Thread.current[:result] = result
                        if result.eql? md5original then
                            finish = true
                            Thread.current[:output] = true
                        end
                    end
                end
            }
            array << a
        }
        total = 0
        while total < allclients.length do
            total = 0
            array.each {|el|
            puts el[:machine].to_s + " " + el[:result].to_s + " "+ el[:output].to_s
            if el[:output] then
                total = total + 1
            end
            
            }
            puts "Number of machines " + total.to_s
            puts "\n\n\n"
        end
        timefi = Time.new
        toti = (timefi - timeini).to_s
        puts "Total miliseconds " + (timefi - timeini).to_s
        puts "Iteration number " + iter.to_s
        fichier.puts toti + "\n"
    
        
    end
end