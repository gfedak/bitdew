#!/usr/bin/env ruby
require 'rubygems'
require 'net/ssh'
require 'restfully'       # gem install restfully
require 'net/ssh/gateway' # gem install net-ssh-gateway
require 'json'            # gem install json
require 'yaml'

LOGGER       = Logger.new(STDERR)
LOGGER.level = Logger::INFO

CONFIG = YAML.load_file(File.expand_path("~/.restfully/api.grid5000.fr.yml"))

PUBLIC_KEY       = Dir[File.expand_path("~/.ssh/*.pub")][0]
fail "No public key available in ~/.ssh !" if PUBLIC_KEY.nil?

PRIVATE_KEY  = File.expand_path("~/.ssh/#{File.basename(PUBLIC_KEY, ".pub")}")
fail "No private key corresponding to the public key available in ~/.ssh !" unless File.file?(PRIVATE_KEY)

LOGGER.info "Using the SSH public key located at: #{PUBLIC_KEY.inspect}"
JOBS=[]
DEPLOYMENTS  = []
TIMEOUT_JOB  = 6*60 # 2 minutes
TIMEOUT_DEPLOYMENT = 30*60 # 15 minutes
arrhash = [{:site=>"grenoble",:uid=>1256528},{:site=>"lille",:uid=>1125976},{:site=>"lyon",:uid=>566906},{:site=>"sophia",:uid=>486571}]
allnodes = []
Restfully::Session.new(
    :base_uri => CONFIG['base_uri'],
    :username => CONFIG['username'],
    :password => CONFIG['password'],
    :logger => LOGGER
  ) do |root, session|
arrhash.each{|obj|
    puts "Deploying on #{obj[:site]} job #{obj[:uid]}"
    
    root.sites.find{|s| s["uid"].eql? obj[:site]}.jobs.each{|job|
        if job["uid"].eql? obj[:uid]
            puts "entro"
            JOBS << job    
        end
    }
}
puts "jobs is #{JOBS}"
    if JOBS.empty?
      session.logger.warn "No jobs, exiting..."
      exit(0)
    end
    
    begin
      Timeout.timeout(TIMEOUT_JOB) do
        until JOBS.all?{|job|
          job.reload['state'] == 'running'
        } do
            session.logger.info "Some jobs are not running. Waiting before checking again..."
            sleep TIMEOUT_JOB/30
          end
        end
    rescue Timeout::Error => e
      session.logger.warn "One of the jobs is still not running, it will be discarded."
    end
    
     JOBS.each do |job|
      next if job.reload['state'] != 'running'
      
      new_deployment = job.parent.deployments.submit(
        :environment => "http://public.grenoble.grid5000.fr/~jsaray/jsaray_squeeze.env",
        :nodes => job['assigned_nodes'],
        :key => File.read(PUBLIC_KEY)
      ) rescue nil
      puts "New deployment is " + new_deployment.to_s
      DEPLOYMENTS.push(new_deployment) unless new_deployment.nil?
    end
    
    if DEPLOYMENTS.empty?
      session.logger.warn "No deployments, exiting..."
      
      exit(0)
    end
    
    begin
      Timeout.timeout(TIMEOUT_DEPLOYMENT) do
        until DEPLOYMENTS.all?{ |deployment|
          deployment.reload['status'] != 'processing'
        } do
          session.logger.info "Some deployments are not terminated. Waiting before checking again..."
          sleep TIMEOUT_DEPLOYMENT/30
          end
      end
    rescue Timeout::Error => e
      session.logger.warn "One of the deployments is still not terminated, it will be discarded."
    end
    
    
  
    DEPLOYMENTS.each do |deployment|
      next if deployment.reload['status'] != 'terminated'
        deployment["nodes"].each do |host|
          allnodes << host
           end
    end
    
    File.open("nodelist","w")do|f|
        allnodes.each{|elem|
          f.puts elem
        }
        puts "Array content is " + allnodes.to_s     
    end 
end