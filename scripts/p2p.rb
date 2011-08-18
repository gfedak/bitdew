#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'
#%x(uniq $OAR_NODEFILE > nodelist)

path = ARGV[0]
nodes = IO.readlines("/home/jsaray/nodelist")
centralnode = nodes[0] 
term = nodes[1]
term = term.slice(/[^\n]*/)
arr = nodes[1,(nodes.length-1)]
puts "centralnode is " + centralnode
killjava = %x(taktuk -d-1 -f nodelist broadcast exec { killall java })
output = %x(taktuk -d-1 -f nodelist broadcast exec { 'rm -rf *'  })

mkdir = %x(taktuk -d-1 -f nodelist broadcast exec { mkdir songs })
puts mkdir
%x(taktuk -d-1 -f nodelist broadcast put { /home/jsaray/bitdewp2pn.jar } { /home/jsaray/bitdewp2pn.jar })
%x(taktuk -d-1 -f nodelist broadcast put { /home/jsaray/bitdew-stand-alone-0.2.6.jar } { /home/jsaray/bitdew-stand-alone-0.2.6.jar })
centralnode = centralnode.slice(/[^\n]*/)

Net::SSH.start(centralnode,"jsaray") do |ssh|
  ssh.exec "java -cp bitdewp2pn.jar:bitdew-stand-alone-0.2.6.jar xtremweb.role.cmdline.CommandLineTool serv dc dt ds dr > /home/jsaray/servout"+centralnode+" 2> /home/jsaray/serverr"+centralnode+" &"
end
puts "Waiting for services launching"
sleep(7)

allfiles = Dir.entries(path)
puts "allfiles size is " + allfiles.length.to_s
puts "allfiles sub 5 is " + allfiles.to_s

posi = 2
arr.each{|line|   
    line = line.slice(/[^\n]*/)
Net::SSH.start(line,"jsaray")do |ssh|
        ssh.exec "java -cp bitdewp2pn.jar:bitdew-stand-alone-0.2.6.jar xtremweb.role.cmdline.CommandLineTool serv dt dr ds dc > /home/jsaray/repout 2> /home/jsaray/repoerr &"
        end
    sleep(3)
    allfiles[posi..posi+4].each{|file|        
        puts "sending file " + file +" to host " + line
	IO.popen("scp \"" +path+"/"+ file +  "\" " + line+":songs/")do|f|
            f.readlines
        end
    }
  posi = posi + 5
  puts "posini is " + posi.to_s + " posfi is " + posi.to_s
}
arr = nodes[1,(nodes.length-1)]
     arr.each{|line|
         line = line.slice(/[^\n]*/)
         puts "Put on line " + line
         Net::SSH.start(line,"jsaray")do |ssh|
         ssh.exec "nohup java -cp bitdew-stand-alone-0.2.6.jar:bitdewp2pn.jar xtremweb.role.examples.P2PSongs " + centralnode + " /home/jsaray/songs > /home/jsaray/out"+line+" 2> /home/jsaray/err"+line+" &"
          end
}
puts "Waiting for Put called in all nodes"
sleep(15)





#Net::SSH.start(centralnode,"jsaray") do |ssh|
#   ssh.exec "java -cp bitdew-stand-alone-0.2.6.jar:bitdewp2pn.jar xtremweb.role.examples.P2PSongs "+centralnode+ " get " + term + " "+ centralnode +" > /home/jsaray/getout"+centralnode+" 2> /home/jsaray/geterr"+centralnode+" &"
#end


#get = IO.readlines("/home/jsaray/nodelist")

#arr.each{|line|
#              line = line.slice(/[^\n]*/)
#              Net::SSH.start(line,"jsaray")do |ssh|
#              ssh.exec "nohup java -cp bitdew-stand-alone-0.2.6.jar:bitdewp2pn.jar fr.PutOnDHT " + centralnode + "# get " + line + " > /home/jsaray/getout"+line+" 2> /home/jsaray/geterr"+line+" &"
#           end
#}

