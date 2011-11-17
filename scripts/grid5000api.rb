#!/usr/bin/env ruby
require "rubygems"
require "json"
puts "Usage SITE RESOURCES WALLTIME NAME "
time = Time.new
mysite = ARGV[0]
resources = ARGV[1]
walltime = ARGV[2]
thename = ARGV[3]
begin

  resu = "/nodes=" + resources+",walltime=" + walltime
  time = time.strftime("%Y-%m-%d %H:%M:%S")
  obj = {:name => thename, :resources => resu ,:reservation => time , :types => ["deploy"] }
    
  jsonobj = obj.to_json
  puts "json es " + jsonobj
  
  codeans = "500"
 
  
  iter = 0 
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
    dep = IO.popen("kadeploy3 -a images/jsaray_squeeze.env -f nodelist -k")do |f|
      f.readlines
    end
    puts "Finishi Deploying, response " + dep
    puts "Deployment response is " + response.to_s
  else
    puts "Joder tio que has hecho una cosa gorda"
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