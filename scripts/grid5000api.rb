#!/usr/bin/env ruby
require "rubygems"
require "json"

date = ARGV[0]
if(date.eql? "--help")
puts "\n\n\n"
puts " grid50000.rb makes a reservation on grid5000 using grid5000 API"
puts " 		Usage DATE SITE RESOURCES WALLTIME NAME ENVPATH "
puts "		DATE reservation date, put now if you want the resources now"
puts "		SITE choose a site (grenoble, lyon , orsay etc)"
puts "		RESOURCES a number, how many machines do you want ?"
puts "		WALLTIME for how long ?"
puts "		NAME job name"
puts "		ENVPATH path to a .env file to perfom a deploy"
exit(0);
end

if date.eql? "now" or date.eql? "NOW" then
time = Time.new
time = time.strftime("%Y-%m-%d %H:%M:%S")
else
  time = date
end

mysite = ARGV[1]
resources = ARGV[2]
walltime = ARGV[3]
thename = ARGV[4]
envpath = ARGV[5]
begin

  resu = "/nodes=" + resources+",walltime=" + walltime
  
  obj = {:name => thename, :resources => resu ,:reservation => time , :types => ["deploy"] }
    
  jsonobj = obj.to_json
  puts "json es " + jsonobj
  
  codeans = "500"
  object = {}
  
  iter = 0 
  if (date.eql? "now" or date.eql? "NOW")
    puts "Is a job currently submitted"
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
    
  else
    puts "Is a job previously submitted"
    getj = "curl https://api.grid5000.fr/2.0/grid5000/sites/"+mysite+"/jobs?user_uid=jsaray"
    rep = IO.popen(getj)do|f|
      f.readlines
    end  
    object = JSON.parse rep.to_s
    object = object["items"][0]
    jobid = object["uid"]
    puts "in else jobid is #{jobid}"
    state = object["state"]
    puts "in else state is #{state}"
    codeans=nil
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
    
    jsondeploy = {:nodes => machines,:environment => "http://public.grenoble.grid5000.fr/~jsaray/jsaray_squeeze.env"}
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