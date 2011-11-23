#!/usr/bin/env ruby
require "rubygems"
require "json"
puts "Usage SITE RESOURCES WALLTIME NAME ENVPATH"
time = Time.new
mysite = ARGV[0]
resources = ARGV[1]
walltime = ARGV[2]
thename = ARGV[3]
envpath = ARGV[4]
begin

  resu = "/nodes=" + resources+",walltime=" + walltime
  time = time.strftime("%Y-%m-%d %H:%M:%S")
  obj = {:name => thename, :resources => resu ,:reservation => time , :types => ["deploy"] }
    
  jsonobj = obj.to_json
  puts "json es " + jsonobj
  
  codeans = "500"
 
  
  iter = 0 
  
  # Submit a reservation now
  while !(codeans == nil) and  !codeans.eql? "200" and iter < 5 
    iter= iter + 1
    cmnd = "curl -H \"Accept: application/json\" -H \"Content-Type: application/json\" --data '" +jsonobj + "' https://api.grid5000.fr/2.0/grid5000/sites/" + mysite +"/jobs"
    puts "command to launch "+ cmnd
    output =IO.popen(cmnd)do |f|
      f.readlines
    end
    object = JSON.parse output.to_s
    puts "object is " + object.inspect
    codeans = object["code"]
    puts "Code answer is " + codeans.to_s + ", trying again in 3 seconds"
    sleep(3)
  end
  
  #The api throw nil when there are no problems, we wait until the job state is running and resources are actually allocated
  if codeans == nil
    puts "Request successful"
    jobid = object["uid"]
    puts "Job Id is " + jobid.to_s
    
    state = nil
    while !(state.eql? "running") do
      resources = IO.popen("curl https://api.grid5000.fr/2.0/grid5000/sites/"+ mysite +"/jobs/" + jobid.to_s) do |f|
        f.readlines
      end
    
      puts "Resources are " + resources.to_s
      resobj = JSON.parse resources.to_s
      state = resobj["state"]
      puts "State is " + state + ", retrying again in 10 seconds"
      sleep(10)
      
    end
    
    machines = resobj["assigned_nodes"]
    File.open("nodelist","w") do |f|
      machines.each{|elem|
        f.puts elem
      }
    end
    puts "Deploying ; this will take some time"
    
    jsondeploy = {:nodes => machines,:environment => "http://public."+mysite+".grid5000.fr/~jsaray/jsaray_squeeze.env"}
    jsondeploy = jsondeploy.to_json
    puts "Obj json deploy is #{jsondeploy}"
    
    output = IO.popen("curl -H \"Accept: application/json\" -H \"Content-Type: application/json\" --data '"+ jsondeploy +"' https://api.grid5000.fr/2.0/grid5000/sites/"+mysite+"/deployments") do |f|
      f.readlines
    end
    puts "output is #{output}"
    json = JSON.parse output.to_s
    
    
    uid= json["uid"]
    status = json["status"]
    puts "deployment uid is #{uid}, deployment status is #{status}"
    while !(status.eql? "terminated") do
      status = IO.popen("curl https://api.grid5000.fr/2.0/grid5000/sites/"+mysite+"/deployments/"+uid)do |f|
        f.readlines
      end
      puts "Response #{status}"
      json = JSON.parse status.to_s
      status = json["status"]
      puts "in while status is #{status}" 
      sleep(5)
    end
    
    
    
    
    #dep = IO.popen("kadeploy3 -a "+envpath+" -f nodelist -k")do |f|
    #  f.readlines
    #end
    #puts "Finishi Deploying, response " + dep.to_s
    

    
    
  else
    puts "ERROR !!!!!"
  end
  
rescue Exception => e
  puts "There was a problem all jobs will be removed"
  puts "The problem is " + e.message
  puts e.backtrace.join("\n")

    fifi = "curl https://api.grid5000.fr/2.0/grid5000/sites/"+ mysite +"/jobs?user_uid=jsaray"
    jobids = IO.popen(fifi) do |f|
        f.readlines
    end

    jobids = JSON.parse jobids.to_s

    items = jobids["items"]
    items.inspect
    items.each{|item|
      puts "deleting " + item["uid"].to_s
      IO.popen("curl -X DELETE https://api.grid5000.fr/2.0/grid5000/sites/"+mysite+"/jobs/"+item["uid"].to_s) do |f|
        f.readlines
      end
   }
end