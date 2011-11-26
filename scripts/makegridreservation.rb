require 'rubygems'        # or: export RUBYOPT="-rubygems"
require 'restfully'       # gem install restfully
require 'net/ssh/gateway' # gem install net-ssh-gateway
require 'json'            # gem install json
require 'yaml'
require 'torrentstress.rb'
require 'pp'
LOGGER       = Logger.new(STDERR)
LOGGER.level = Logger::INFO

CONFIG = YAML.load_file(File.expand_path("~/.restfully/api.grid5000.fr.yml"))

PUBLIC_KEY       = Dir[File.expand_path("~/.ssh/*.pub")][0]
fail "No public key available in ~/.ssh !" if PUBLIC_KEY.nil?

PRIVATE_KEY  = File.expand_path("~/.ssh/#{File.basename(PUBLIC_KEY, ".pub")}")
fail "No private key corresponding to the public key available in ~/.ssh !" unless File.file?(PRIVATE_KEY)

LOGGER.info "Using the SSH public key located at: #{PUBLIC_KEY.inspect}"



JOBS         = []
DEPLOYMENTS  = []

TIMEOUT_JOB  = 6*60 # 2 minutes
TIMEOUT_DEPLOYMENT = 30*60 # 15 minutes
allnodes = []
COMMAND = "hostname"

def cleanup!
  #LOGGER.warn "Received cleanup request, killing all jobs and deployments..."
  
  JOBS.each{|job| job.delete}
end

%w{INT TERM}.each do |signal|
  Signal.trap(signal){ 
    cleanup! 
    exit(1)
  }
end
date= ARGV[0]
if date.eql? "--help"
  puts "Usage : ruby makegridreservation.rb <date> <walltime> [<site>,<#nodes><space>]+"
  exit(0)
end
walltime = ARGV[1]
sleepvalue = walltime.split(":")[0].to_i
sleepvalue = sleepvalue*60*60
puts "sleepvalue is #{sleepvalue}"
jobijoba = ARGV[2].split(" ")
arrobjs = []
jobijoba.each{|sitejobs|
  puts "site, job : #{sitejobs}"
  lieu = sitejobs.split(",")[0]
  noeuds = sitejobs.split(",")[1]
  obji =  {:site=>lieu,:nodes=>noeuds}
  arrobjs << obji
}
#arrobjs=[{:site=>"bordeaux",:nodes=>2},{:site=>"grenoble",:nodes=>2},{:site=>"lille",:nodes=>2},{:site=>"lyon",:nodes=>2},{:site=>"nancy",:nodes=>2},{:site=>"orsay",:nodes=>2},{:site=>"rennes",:nodes=>2},{:site=>"sophia",:nodes=>2},{:site=>"toulouse",:nodes=>2}]
begin
  Restfully::Session.new(:base_uri => CONFIG['base_uri'],:username => CONFIG['username'],:password => CONFIG['password'],:logger => LOGGER) do |root, session|
    arrobjs.each do |obj|
      site = root.sites.find{|s| s["uid"].eql? obj[:site]}
      nodes = obj[:nodes]
      puts "nodes for #{site} : #{nodes}" 
      if site.status.find{ |node|
        node['system_state'] == 'free' && node['hardware_state'] == 'alive'
      } then
      
      new_job = site.jobs.submit(:resources => "nodes=#{nodes},walltime=#{walltime}",:command => "sleep #{sleepvalue}",:reservation => date,:types => ["deploy"],:name => "API Main Practical") rescue nil
      pp new_job
      else
        session.logger.info "Skipped #{site['uid']}. Not enough free nodes."
      end
      JOBS << new_job
    end
  end
rescue StandardError => e
  LOGGER.warn "Catched unexpected exception #{e.class.name}: #{e.message} - #{e.backtrace.join("\n")}"
  cleanup!
  exit(1)
end