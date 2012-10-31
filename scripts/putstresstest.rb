#!/usr/bin/env ruby
require "rubygems"
require 'net/ssh'
version = "1.1.2"

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
REMOTE_FOLDER = "/tmp/"
PROPERTIES_FILE = "stresstest.json"

if ARGV[0].eql? "--help" then
   puts "Usage : ruby putstress.rb <number_of_threads_per_core> <file_name_to_get>"
   exit(0)
end


IO.popen("taktuk -d-1 -f nodelist broadcast exec { 'killall java' }")do |f|
    f.readlines
end

puts "Erasing all"

IO.popen("taktuk -d-1 -f nodelist broadcast exec { 'rm -rf " + REMOTE_FOLDER + "*' }")do |f|
   f.readlines
end

puts "Sending bitdew jar file"

IO.popen("taktuk -d-1 -f nodelist broadcast put { "+BASE_FOLDER+"bitdew-stand-alone-"+version+".jar } { "+REMOTE_FOLDER+"bitdew-stand-alone-"+version+".jar }")do |f|
 f.readlines
end

puts "Sending props file"

IO.popen("taktuk -d-1 -f nodelist broadcast put { "+BASE_FOLDER+ PROPERTIES_FILE+" } { "+REMOTE_FOLDER + PROPERTIES_FILE+" }")do |f|
    f.readlines
end

puts "Send the file to put"





number_of_threads = ARGV[0]
fileputgetname = ARGV[1]
IO.popen("taktuk -d-1 -f nodelist broadcast put { "+BASE_FOLDER+ fileputgetname + " } { " + REMOTE_FOLDER + fileputgetname + " }") do |f|
        f.readlines
end
i = 0
machines = IO.readlines("nodelist")
services = machines[0].slice(/[^\n]*/)

puts "Services machine is "  + services


stable_node = services

uid = ""

Net::SSH.start(services,"jsaray") do |ssh|
    ssh.exec "cd /tmp;nohup java -jar " + REMOTE_FOLDER + "bitdew-stand-alone-" + version + ".jar --verbose --file "+REMOTE_FOLDER+PROPERTIES_FILE+" serv dc dt dr ds > servout 2> serverr & "
end
sleep(120)

threadputs = []
machines = machines[1 .. machines.length - 1]
machines.each{|machine|
   
   machine = machine.slice(/[^\n]*/)
   puts "Executing on machine " + machine
   
   threadput = Thread.new {
      index_thread = 0
      while index_thread < number_of_threads.to_i do
          Net::SSH.start(machine,"jsaray") do |ssh|
            ssh.exec "nohup java -cp "+REMOTE_FOLDER+"bitdew-stand-alone-"+version+".jar xtremweb.role.integration.TestPutMultiple " + stable_node + " "+ REMOTE_FOLDER + fileputgetname + " > out"+index_thread.to_s + " 2> err"+index_thread.to_s+" &"
          end
          index_thread = index_thread + 1    
      end  
   }
   threadputs << threadput
   
}
out = ""
puts "File is downloading on each node"
sleep(TIMEOUT.to_i)
puts "after join"
total = 0
numberof =""
md5sun = ""
Net::SSH.start(services,"jsaray") do |ssh|
      
      numberof = ssh.exec!("ls -al "+REMOTE_FOLDER+"/????????-????-????-????-???????????? | wc -l")

      
end

Net::SSH.start(services ,"jsaray") do |ssh|
   md5sun = ssh.exec!("md5sum "+ REMOTE_FOLDER + "/????????-????-????-????-????????????")
   puts "The md5sum of all files is " + md5sun 
end   

puts "number of is " + numberof.to_s
