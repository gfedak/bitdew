#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'
version="1.1.2"

# This SCRIPT splits equally files on a given folder in your localhost to N different hosts
# You need properties_bootstrap.json file, it has configuration properties related to bootstrap
# You need properties_peer.json file, it has configuration properties related to each peer, follow the instructions on
# BitDew web site to understand the content of both files.
# You need a nodelist file, the first line will be the bootstrap node, the rest peers
# Finally you need a folder with files that you want to share. The first argument you give to this script is the folder
# name, second argument is the machine number you want to share the files and the third argument how many files per machine.

#allhosts will store the Grid500 OAR_NODEFILE content 
iout = IO.popen("uniq nodelist")
allhosts = iout.readlines
#the folder that contains all songs
path = ARGV[0]
if path.eql? "-h" or path.eql? "--help" then
	puts "Usage : ruby p2p.rb <folderpath> <#hosts> <#filesperhost>"
	exit(0)
end
#number of peers different from the bootstrap node from OAR_NODEFILE, effectively making part of the experiment, offset <= NUMBER_OF_LINES(OAR_NODEFILE)
offset = ARGV[1].to_i
#how many files will be send to a host
filesperhost = ARGV[2].to_i

if File.exists? "/home/jsaray/nodelist" then
else
 raise "The file nodelist was not correctly created, please try again"
end

puts "allhosts name is " + allhosts.class.name  
nodes = IO.readlines("/home/jsaray/nodelist")
centralnode = nodes[0] 


arr = nodes[1,offset]
puts "centralnode is " + centralnode
killjava = %x(taktuk -d-1 -f nodelist broadcast exec { killall java })
output = %x(taktuk -d-1 -f nodelist broadcast exec { 'rm -rf *'  })
IO.popen("taktuk -d-1 -f nodelist broadcast exec { mkdir " + path + " }")do |f|
    f.readlines
end
#puts mkdir
#the executable code is sent to different peers
#%x(taktuk -d-1 -f nodelist broadcast put { /home/jsaray/bitdewp2pn.jar } { /home/jsaray/bitdewp2pn.jar })
%x(taktuk -d-1 -f nodelist broadcast put { /home/jsaray/sbam_standalone.jar } { /home/jsaray/sbam_standalone.jar })
IO.popen("taktuk -d-1 -f nodelist broadcast put { /home/jsaray/bitdew-stand-alone-"+version+".jar } { /home/jsaray/bitdew-stand-alone-"+version+".jar }")do |f|
  f.readlines
end
%x(taktuk -d-1 -f nodelist broadcast put { /home/jsaray/properties_bootstrap.json } { /home/jsaray/properties_bootstrap.json })
centralnode = centralnode.slice(/[^\n]*/)
#services are started on central node, special attention is dc, which contains a ddc and the DHT 
Net::SSH.start(centralnode,"jsaray") do |ssh|
  ssh.exec "java -cp sbam_standalone.jar:bitdew-stand-alone-"+version+".jar xtremweb.role.cmdline.CommandLineTool --file properties_bootstrap.json serv dc dt ds dr > /home/jsaray/servout"+centralnode+" 2> /home/jsaray/serverr"+centralnode+" &"
end
puts "Waiting for services launching"
sleep(7)
%x(taktuk -d-1 -f nodelist broadcast put { /home/jsaray/properties_peer.json } { /home/jsaray/properties_peer.json })
allfiles = Dir.entries(path)
puts "allfiles content " + allfiles.to_s
puts "allfiles size is " + allfiles.length.to_s
puts "allfiles sub 5 is " + allfiles.to_s
puts "arr content is " + arr.to_s
posi = 2
#the files on directory are sent in blocks of signaled by variable filesperhost
arr.each{|line|   
    line = line.slice(/[^\n]*/)
    

    Net::SSH.start(line,"jsaray") do |ssh|
        ssh.exec "nohup java -cp sbam_standalone.jar:bitdew-stand-alone-"+version+".jar xtremweb.role.cmdline.CommandLineTool -v --file properties_peer.json serv dc dt dr ds > /home/jsaray/outserv" + line + " 2> /home/jsaray/errserv" + line + " &"
    end
    sleep(3)


    allfiles[posi..posi+filesperhost-1].each{|file|        
        puts "sending file " + file +" to host " + line
	IO.popen("scp \"" +path+"/"+ file +  "\" " + line+":" + path + "/")do|f|
            f.readlines
        end
    }
  posi = posi + filesperhost 
  puts "posini is " + posi.to_s + " posfi is " + posi.to_s
}
#The program P2PSongs is executed in all peers
arr = nodes[1,(nodes.length-1)]
     arr.each{|line|
         line = line.slice(/[^\n]*/)
         puts "Put on line " + line
         Net::SSH.start(line,"jsaray")do |ssh|
         ssh.exec "nohup java -cp sbam_standalone.jar:bitdew-stand-alone-"+version+".jar xtremweb.role.examples.P2PSongs properties_peer.json " + centralnode + " /home/jsaray/songs > /home/jsaray/out"+line+" 2> /home/jsaray/err"+line+" &"
          end
}
puts "Waiting for Put called in all nodes"
sleep(30)
