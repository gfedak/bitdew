#!/usr/bin/env ruby
require "rubygems"
require 'net/ssh'
version = "0.2.8"
IO.popen("taktuk -d-1 -f nodelist broadcast exec { 'killall java' }")do |f|
    f.readlines
end
puts "Erasing all"
IO.popen("taktuk -d-1 -f nodelist broadcast exec { 'rm -rf *' }")do |f|
   f.readlines
end
puts "Sending bitdew jar file"
IO.popen("taktuk -d-1 -f nodelist broadcast put { /home/jsaray/bitdew-stand-alone-"+version+".jar } { /home/jsaray/bitdew-stand-alone-"+version+".jar }")do |f|
 f.readlines
end

puts "Sending props file"
IO.popen("taktuk -d-1 -f nodelist broadcast put { /home/jsaray/props.json } { /home/jsaray/props.json }")do |f|
    f.readlines
end


number_of_threads = ARGV[0]
filename = ARGV[1]
i = 0
machines = IO.readlines("nodelist")
services = machines[0].slice(/[^\n]*/)

puts "Services machine is "  + services

putmachine = machines[1].slice(/[^\n]*/i)

puts "Put machine is " + putmachine

stable_node = services
IO.popen("taktuk -d-1 -f nodelist broadcast put { /home/jsaray/helloworld } { /home/jsaray/helloworld }") do |f|
f.readlines
end

uid = ""
Net::SSH.start(services,"jsaray") do |ssh|
    ssh.exec "nohup java -jar bitdew-stand-alone-" + version + ".jar --file "+filename+" serv dc dt dr ds > servout 2> serverr & "
end
sleep(120)

Net::SSH.start(putmachine,"jsaray") do |ssh|
    output = ssh.exec! "nohup java -jar bitdew-stand-alone-"+version+".jar --file "+filename+" --host " + services +" put helloworld > putout 2> puterr "
    IO.popen("scp " + putmachine +":putout .") do|f|
	    f.readlines
    end
    uid = IO.readlines("putout")[0]
    puts "The id line is #{uid}"
    uid = uid.match(/[[a-fA-F0-9-]*]/)[0]
    uid = uid[1,uid.length-2]
    puts "The uid is  #{uid}"
end

machines = machines[2 .. machines.length - 1]
machines.each{|machine|
i=0
machine = machine.slice(/[^\n]*/)
puts "Executing on machine " + machine
while i < number_of_threads.to_i do
Net::SSH.start(machine,"jsaray") do |ssh|
#    while i < number_of_threads.to_i do
  puts "while"
        ssh.exec "nohup java -cp bitdew-stand-alone-"+version+".jar xtremweb.role.integration.TestGetMultiple " + stable_node + " "+ uid + " " + i.to_s + " > out"+i.to_s + " 2> err"+i.to_s+" &"
        i = i + 1

#    end
puts "finish"
end
end

}
out = ""
puts "File is downloading on each node"
sleep(60)
machines.each{|machine|
machine = machine.slice(/[^\n]*/)
Net::SSH.start(machine,"jsaray") do |ssh|

out = ssh.exec!("ls -al | grep fofis")
end
puts "Exit on  node "+ machine.to_s  + " " + out.to_s
}
