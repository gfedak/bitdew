#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'

# This script deploy a test where one stable node; running bitdew services, starts concurrently 
# N transfers to different nodes.
# Parameters : ARGV[0] number of http transfers you want to execute
#              ARGV[1] number of ftp transfers you want to execute
#              ARGV[2] number of scp transfers you want to execute
#              ARGV[3] number of files per host 
#              ®RGV[4] file name
VERSION = "0.2.8"
NUM_HTTP=ARGV[0]
PROCESSING_TIME=120
if NUM_HTTP.eql? "-h" or NUM_HTTP.eql? "--help"
    puts "Usage ruby mix.rb number_http number_ftp number_scp batch_size file_name"
    exit(0)
end

#%x(uniq $OAR_NODEFILE > nodelist)
#clean the directory where logging is stored
%x(rm -rf filesmix)
#recreate directory
%x(mkdir filesmix)
# MACHINE NAMES FROM 0 TO OFFSET FTP WILL BE HANDLED THROUGH HTTP PROTOCOL
NUM_HTTP=ARGV[0].to_i



# Machine names from OFFSET_FTP to OFFSET_SCP will be handled trhough ftp protocol
NUM_FTP=ARGV[1].to_i
# Machine names from OFFSET_SCP until the end will be handled through SCP protocol
NUM_SCP=ARGV[2].to_i
#NUMBER OF files to be transmitted to each host



BATCH=ARGV[3].to_i
FILE_NAME = ARGV[4]
nextftp = NUM_FTP.to_i + 1
nextscp = NUM_SCP.to_i + 1
total = %x(wc -l nodelist)
total = total.split(" ")[0]
nlines = total.to_i
#nlines : number of lines, number of machines .

hosts = IO.readlines("/home/jsaray/nodelist")

if ((NUM_FTP + NUM_HTTP + NUM_SCP) != hosts.length ) then
   puts "The values of http, ftp and scp must sum the file length"
   exit(0)
end

hostshttp = hosts[1..(NUM_HTTP)]

if NUM_HTTP == 0 then
hostshttp = []
end

hostsftp = hosts[NUM_HTTP+1..(NUM_HTTP+NUM_FTP)]
if NUM_FTP == 0 then
hostsftp = []
end


hostsscp = hosts[(NUM_HTTP+NUM_FTP+1)..(NUM_HTTP+NUM_FTP+NUM_SCP)]
if NUM_SCP == 0 then
hostsscp = []
end

puts "http " + hostshttp.to_s + " ftp " + hostsftp.to_s + " scp " + hostsscp.to_s
# send keys in broadcast to all nodes containing in nodelist

i=0

path = ENV['OAR_NODEFILE']

puts "Cleaning the environment "
# Delete all files in each node
output = %x(taktuk -d-1 -f nodelist broadcast exec { 'rm -rf *'  })
puts "Starting DR in nodes"
killjava = %x(taktuk -d-1 -f nodelist broadcast exec { killall java })
puts "kill java output : " + killjava
%x(taktuk -d-1 -f nodelist broadcast put { /home/jsaray/ftprepo.json } { /home/jsaray/ftprepo.json })
%x(taktuk -d-1 -f nodelist broadcast put { /home/jsaray/scprepo.json } { /home/jsaray/scprepo.json })
puts output
# Recreate logging directory
%x(taktuk -d-1 -f nodelist broadcast exec { 'mkdir filesmix' })
puts "Sending http,scp and ftp files"
# send different repositories to different hosts
IO.popen("taktuk -d-1 -f nodelist broadcast put { bitdew-stand-alone-"+VERSION+".jar } { /home/jsaray/bitdew-stand-alone-"+VERSION+".jar }")do |f|
f.readlines
end


puts "Starting DR in nodes"
# start program in master
alln = IO.readlines("/home/jsaray/nodelist")
 centralnode = alln[0]
 puts " Master node is " + centralnode
 centralnode = centralnode.slice(/[^\n]*/) 
 IO.popen("scp bitdew-stand-alone-"+VERSION+".jar " + centralnode +":") do |f|
    f.readlines
 end 
 Net::SSH.start(centralnode,"jsaray") do |ssh|
      ssh.exec "java -jar bitdew-stand-alone-"+VERSION+".jar serv dr dc dt ds > /home/jsaray/filesmix/masteriniout"+centralnode+" 2> /home/jsaray/filesmix/masterinierr"+centralnode+" &"
 end
    
# start http repository on nodes
    hostshttp.each{|line|
	puts "Starting http repository in "+line
        line = line.slice(/[^\n]*/)
	Net::SSH.start(line,"jsaray")do |ssh| 	      
	ssh.exec "nohup java -jar bitdew-stand-alone-"+VERSION+".jar serv dr > /home/jsaray/filesmix/repoiniout"+line+" 2> /home/jsaray/filesmix/repoinierr"+line+" &"
    	end
	}
puts "Waiting for dr to start"

# start ftp repository on nodes
    hostsftp.each{|line|
        puts "Starting ftp repository in " + line
        line = line.slice(/[^\n]*/)
        Net::SSH.start(line,"jsaray")do |ssh|
          ssh.exec "nohup java -jar bitdew-stand-alone-"+VERSION+".jar --file ftprepo.json serv dr > /home/jsaray/filesmix/repoiniout"+line+" 2> /home/jsaray/filesmix/repoinierr"+line+" &"
        end
    }

# start scp repository on nodes
    hostsscp.each{|line|
        puts "Starting scp repository in "+line
        line = line.slice(/[^\n]*/)
        Net::SSH.start(line,"jsaray") do |ssh|
          ssh.exec "nohup java -jar bitdew-stand-alone-"+VERSION+".jar --file scprepo.json serv dr > /home/jsaray/filesmix/repoiniout"+line+" 2> /home/jsaray/filesmix/repoinierr"+line+" &"
        end
    }

sleep(15)
puts "value alln " + nlines.to_s

puts "Successfully started DX's in all nodes "
puts "Copying needed files to central node "+centralnode

IO.popen("scp nodelist "+centralnode+":")do |f|
f.readlines
end
IO.popen("scp "+FILE_NAME+ " " +centralnode+":")do |f|
f.readlines
end
puts "Files copied, attempting to execute the program "
sleep(15)
# run the Test contained on class datatransfer.TestaDataTransfer from testtm.jar 
Net::SSH.start(centralnode,"jsaray") do |ssh|
     ssh.exec "mkdir results"
     ssh.exec "nohup java -Xmx5012M -cp bitdew-stand-alone-"+VERSION+".jar xtremweb.role.integration.TestDataTransfer nodelist " + NUM_HTTP.to_s + " " + NUM_FTP.to_s + " " + NUM_SCP.to_s  + " " + BATCH.to_s + "  "+FILE_NAME+"> /home/jsaray/filesmix/autotestout"+centralnode+" 2> /home/jsaray/filesmix/autotesterr"+centralnode + " &"     
end

sleep(PROCESSING_TIME)

puts "Checking that all files are there "
# Check that the file was sent to all the hosts
arr = IO.readlines("/home/jsaray/nodelist")
     arr.each{|line|
         line = line.slice(/[^\n]*/)
         Net::SSH.start(line,"jsaray") do |ssh|
           res = ssh.exec! "ls -al"
           puts "in host " + line + "result is " + res
           if res.rindex('no such file') != nil then
		puts "Problem in node " + line + " please verify why the bitdew.mf was not successfuly upload in that node "
           end
         end
     }
Net::SSH.start(centralnode,"jsaray") do |ssh|
    res = ssh.exec! "ls -al results | wc -l"
    filesnet = res.to_i - 3
    expected = (NUM_HTTP*BATCH) + (NUM_FTP*BATCH) + (NUM_SCP*BATCH) 
    puts "The expected amount of files on " + centralnode + " is "+ expected.to_s + " and there were found " + filesnet.to_s
end


puts "Getting all outputs and errors here ... Come Here ! "
# Bring all out and err outputs to frontend
%x(taktuk -d-1 -f nodelist broadcast get { /home/jsaray/filesmix } { /home/jsaray/filesmix/files-'$host' })


