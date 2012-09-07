#!/usr/bin/env ruby
require "rubygems"
require 'net/ssh'
version = "1.1.1"

# 
# 
# This script test the transfer manager when a lot of clients
# perform a GET concurrently.
#
# Parameters : 
# ARGV[0] the number of threads per machine that you want to perform a GET. 
# ARGV[1] file name that you want to put/get
# Prerrequisites : a file named "nodelist" must be in the same directory where is this script. Each file line must be a host name.
# you will also need, a properties file, you can use the one in trunk-1/scripts/configuration_files/stresstest.json 
# The first line will be a stable node where bitdew services will run.
# The second line is the node where the bitdew put will be executed
# The rest of lines will be workers that perform a get command.
# To verify that the test was successful, at the end the script perform a ls on each machine. You must see ARGV[0] files with name file_i
# with the same size than ARGV[1].
# Please not that the total number of files that will appear is (number_of_nodes - 2)*number_of_threads_per_core

TIMEOUT=210
BASE_FOLDER = "/home/jsaray/"
REMOTE_FOLDER = "/home/jsaray/"
PROPERTIES_FILE = "stresstest.json"

if ARGV[0].eql? "--help" then
   puts "Usage : ruby stresstest.rb <number_of_threads_per_core> <file_name_to_get>"
   exit(0)
end


IO.popen("taktuk -d-1 -f nodelist broadcast exec { 'killall java' }")do |f|
    f.readlines
end

puts "Erasing all"

IO.popen("taktuk -d-1 -f nodelist broadcast exec { 'rm -rf *' }")do |f|
   f.readlines
end

puts "Sending bitdew jar file"

IO.popen("taktuk -d-1 -f nodelist broadcast put { "+BASE_FOLDER+"bitdew-stand-alone-"+version+".jar } { "+BASE_FOLDER+"bitdew-stand-alone-"+version+".jar }")do |f|
 f.readlines
end

puts "Sending props file"

IO.popen("taktuk -d-1 -f nodelist broadcast put { "+BASE_FOLDER+ PROPERTIES_FILE+" } { "+REMOTE_FOLDER + PROPERTIES_FILE+" }")do |f|
    f.readlines
end


number_of_threads = ARGV[0]
fileputgetname = ARGV[1]
i = 0
machines = IO.readlines("nodelist")
services = machines[0].slice(/[^\n]*/)

puts "Services machine is "  + services

putmachine = machines[1].slice(/[^\n]*/i)

puts "Put machine is " + putmachine

stable_node = services

#IO.popen("taktuk -d-1 -f nodelist broadcast put { "+BASE_FOLDER+fileputgetname+" } { "+REMOTE_FOLDER+fileputgetname+" }") do |f|
#   f.readlines
#end

IO.popen("scp " + fileputgetname + " " + putmachine +":") do |f|
   f.readlines
end

uid = ""

Net::SSH.start(services,"jsaray") do |ssh|
    ssh.exec "nohup java -jar bitdew-stand-alone-" + version + ".jar --file "+PROPERTIES_FILE+" serv dc dt dr ds > servout 2> serverr & "
end
sleep(120)

Net::SSH.start(putmachine,"jsaray") do |ssh|
    output = ssh.exec! "nohup java -jar bitdew-stand-alone-"+version+".jar --file "+PROPERTIES_FILE+" --host " + services +" put "+fileputgetname+" > putout 2> puterr "
    IO.popen("scp " + putmachine +":putout .") do|f|
	    f.readlines
    end
    uid = IO.readlines("putout")[0]
    puts "The id line is #{uid}"
    uid = uid.match(/\[[a-fA-F0-9-]*\]/)[0]
    uid = uid[1,uid.length-2]
    puts "The uid is  #{uid}"
end
threadgets = []
machines = machines[2 .. machines.length - 1]
machines.each{|machine|
   
   machine = machine.slice(/[^\n]*/)
   puts "Executing on machine " + machine
   
   threadget = Thread.new {
      i=0
      Net::SSH.start(machine,"jsaray") do |ssh|
         while i < number_of_threadsi.to_i do
            ssh.exec "nohup java -cp bitdew-stand-alone-"+version+".jar xtremweb.role.integration.TestGetMultiple " + stable_node + " "+ uid + " " + i.to_s + " > out"+i.to_s + " 2> err"+i.to_s+" &"
	 i = i + 1
         end
      end      
   }
   threadgets << threadget
   
}
out = ""
puts "File is downloading on each node"
sleep(TIMEOUT.to_i)
total = 0
numberof =""
machines.each{|machine|
   machine = machine.slice(/[^\n]*/)
   Net::SSH.start(machine,"jsaray") do |ssh|
      out = ssh.exec!("ls -al | grep getfile")
      numberof = ssh.exec!("ls -al | grep getfile | wc -l")
      puts "number of is " + numberof.to_s
      total = total + numberof.to_s.to_i
   end
   puts "Exit on  node "+ machine.to_s  + " " + out.to_s + " Total file number "+numberof.to_s
   puts "Total Number of files #{total}"
}
