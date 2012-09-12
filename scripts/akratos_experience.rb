#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'


# You need a nodefile in your cluster with the machines participating in the experience
FILE_NAME = "nodefile"
# You need a folder with your Public Info user files
PUBLIC_INFO_USER_FILES = "multipleusers"
JAR_VERSION = "bitdew-stand-alone-1.1.1.jar"
SLEEP_DELAY = 20

# You need a file containing the properties of the bootstrap node 

BOOTSTRAP_NODE_CONFIGURATION_FILE = "properties_bootstrap.json"

# You need the peer configuration file

PEER_CONFIGURATION_FILE = "properties_peer.json"


# You need the sbam_standalone jar file

SBAM_STANDALONE = "sbam_standalone.jar"

# You need a contacts.xml file whose initial structure is <contacts/>
CONTACTS_XML = "contacts.xml"

# You need a personalinfo.xml file to store the private info
PERSONAL_INFO = "personalinfo.xml"


# data must be created in all peers
puts "creating data dir"
%x(taktuk -s -f nodefile broadcast exec { 'mkdir data' })

#Erasing files 

%x(taktuk -s -f nodefile broadcast exec { 'rm -rf *' })
%x(taktuk -s -f nodefile broadcast exec { 'killall java' })
%x(taktuk -d-1 -f nodefile broadcast put { /home/jsaray/personalinfo.xml } { /home/jsaray/personalinfo.xml })
%x(taktuk -d-1 -f nodefile broadcast put { /home/jsaray/contacts.xml } { /home/jsaray/contacts.xml })
%x(taktuk -d-1 -f nodefile broadcast put { /home/jsaray/bitdew-stand-alone-1.1.1.jar } { /home/jsaray/bitdew-stand-alone-1.1.1.jar })
%x(taktuk -d-1 -f nodefile broadcast put { /home/jsaray/properties_peer.json } { /home/jsaray/properties_peer.json })
puts "Put Akratos in broadcast "

%x(taktuk -d-1 -f nodefile broadcast put { /home/jsaray/sbam_standalone.jar } { /home/jsaray/sbam_standalone.jar })
arr = IO.readlines(FILE_NAME)

# STABLE_NODE is where Bitdew services will be executed

STABLE_NODE = arr[0]
STABLE_NODE = STABLE_NODE.slice(/[^\n]*/)
puts "Stable node is " + STABLE_NODE
allfiles = Dir.entries(PUBLIC_INFO_USER_FILES)
purgedallfiles = []
puts "first cveision " + allfiles.to_s
allfiles.each{|element|
	element = element.slice(/[^\n]*/)
	if element.eql? "." or element.eql? ".." or element.eql? nil or element == nil then
	else
		purgedallfiles << element
	end
}
puts "Purged array : " + purgedallfiles.to_s

i=0
arr[1..arr.length].each {|element|
	element = element.slice(/[^\n]*/)
    cmd = "scp "+ PUBLIC_INFO_USER_FILES + "/" + purgedallfiles[i].to_s + " " +element + ":"
	puts "coman " + cmd
	IO.popen(cmd) do |f|
		f.readlines
	end
	i=i+1;
}

IO.popen("scp " + JAR_VERSION + " " + STABLE_NODE + ":") do |f|
	f.readlines
end
puts "launching"
IO.popen("scp " + BOOTSTRAP_NODE_CONFIGURATION_FILE + " " + STABLE_NODE+ ":")do |f|
	f.readlines
end




Net::SSH.start(STABLE_NODE,"jsaray") do |ssh|
	ssh.exec("nohup java -cp "+JAR_VERSION+":"+SBAM_STANDALONE+" xtremweb.role.cmdline.CommandLineTool --file " +BOOTSTRAP_NODE_CONFIGURATION_FILE+ " serv dc dt dr ds > servout 2> serverr &")
end

sleep (SLEEP_DELAY)

arr[1..arr.length].each{|ele|
	ele = ele.slice(/[^\n]*/)
	Net::SSH.start(ele,"jsaray")do |ssh|
		ssh.exec("nohup java -cp "+JAR_VERSION+":"+SBAM_STANDALONE+" xtremweb.role.cmdline.CommandLineTool --verbose --file " +PEER_CONFIGURATION_FILE + " serv dc dt dr ds > servout 2> serverr &")
	end
}
puts "launched "
puts "sleeping"
sleep(SLEEP_DELAY)
puts "wake up"

i=0
arr[1..arr.length].each {|elem|
	elem = elem.slice(/[^\n]*/)
	puts "New element to connenct to " + elem
	Net::SSH.start(elem,"jsaray")do|ssh|
		puts "Launching services on " + elem
		ssh.exec! "java -cp " + JAR_VERSION + " xtremweb.role.examples.akratos.Akratos fill "+purgedallfiles[i]+" > fillout 2> fillerr"
		puts "Automatically filling file,  I will sleep 5 seconds to guarantee that the command was succesfully executed "		
		sleep(5)		
		puts "Subscribing the peer " + elem + " to akratos "
		ssh.exec "nohup java -cp " + JAR_VERSION + ":" + SBAM_STANDALONE + " xtremweb.role.examples.akratos.Akratos subscribe " +purgedallfiles[i] + " " + STABLE_NODE +  " > subscribeout 2> subscribeerr &"
		puts "Sleeping 10 seconds:"
		#sleep(10)
		puts "subscribe passed"
	end
i = i + 1
}

puts "Printing home directory on each machine : "

output = %x(taktuk -s -f nodefile broadcast exec { 'ls -al /home/jsaray' })

puts output

puts " done "

