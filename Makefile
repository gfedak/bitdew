CLASSPATH=build:conf:lib/jcommon-1.0.9.jar:lib/jfreechart-1.0.5.jar:lib/activation.jar:lib/smtp.jar:lib/mail.jar:/lib/pop3.jar:lib/mailapi.jar:lib/commons-io-1.3.1.jar:lib/commons-fileupload-1.1.1.jar:lib/commons-codec-1.3.jar:lib/commons-logging-1.1.jar:lib/commons-httpclient-3.1-beta1.jar:lib/junit-4.1.jar:lib/dks.jar:lib/commons-net-1.3.0.jar:lib/commons-collections-3.1.jar:lib/commons-dbcp-1.2.1.jar:lib/commons-pool-1.2.jar:lib/jpox-dbcp-1.1.0-rc-1.jar:lib/jargs.jar:lib/mysql.jar:lib/hsqldb.jar:lib/bcel.jar:lib/jdo.jar:lib/jpox-1.1.0-rc-1.jar:lib/log4j.jar:lib/jetty-6.1.1.jar:lib/jetty-util-6.1.1.jar:lib/servlet-api-2.5-6.1.1.jar:lib/loci.jar:lib/jets3t-0.6.0.jar:build/xtremweb/core/obj/obj/obj.jdo:conf/dbcp.properties:conf/AmazonS3.properties

all : 
	./build.sh dist
docs:
	./build.sh docs

tests: 
	./build.sh tests

jar:
	./build.sh jar
tests-reports:
	./build.sh tests-reports

runtest :
	java -cp  ${CLASSPATH} xtremweb.role.main.XtremWeb obj
httptest:
	java -DPROPERTIES_FILE="conf/client.properties" -cp  ${CLASSPATH} xtremweb.serv.dt.http.HttpTransfer

ftptest:
	java -DPROPERTIES_FILE="conf/client.properties" -cp  ${CLASSPATH} xtremweb.serv.dt.ftp.FtpTransfer

mailtest:
	java -DPROPERTIES_FILE="conf/client.properties" -cp  ${CLASSPATH} xtremweb.serv.dt.mail.MailTransfer

bttest:
	java -DPROPERTIES_FILE="conf/xtremweb.properties.btpd" -cp  ${CLASSPATH} xtremweb.serv.dt.bittorrent.BittorrentTransfer 129.175.7.234

tmtest:
	java -DPROPERTIES_FILE="conf/client.properties" -cp  ${CLASSPATH} xtremweb.api.transman.TestTM Makefile

adtest:
	java -DPROPERTIES_FILE="conf/client.properties" -cp  ${CLASSPATH} xtremweb.api.activedata.test.TestAD
schedtest:
	java  -cp  ${CLASSPATH} xtremweb.serv.ds.DataScheduler

clean :
	./build.sh clean

enhance:
	java -cp ${CLASSPATH} -Dlog4j.configuration=file:conf/log4j.properties org.jpox.enhancer.JPOXEnhancer build/xtremweb/core/obj/obj/package.jdo

run:
	java -cp ${CLASSPATH}  -Djava.security.manager -Djava.security.policy=conf/java.policy -Dlog4j.configuration=file:conf/log4j.properties  xtremweb.role.main.XtremWeb dr dc dt ds

bench:
	java -DPROPERTIES_FILE="conf/client.properties" -cp ${CLASSPATH} xtremweb.role.bench.Bench --oob dummy --loop=1000 --burst=250 --begin=100 --end=101 --warmup

worker:
	java -DPROPERTIES_FILE="conf/client.properties" -cp ${CLASSPATH} xtremweb.role.mw.Worker2 --port=4325 --host=localhost

master:
	java -cp ${CLASSPATH} xtremweb.role.mw.Master2  --port=4325

psm:
	java -cp ${CLASSPATH} xtremweb.role.examples.PublishSearch --master  --port=4325
psw:
	java -cp ${CLASSPATH} xtremweb.role.examples.PublishSearch --port=4325 
	
bcmaster:
	java -cp ${CLASSPATH} xtremweb.role.examples.Broadcast --master  --port=4325 --dir=/Users/ftp/anonymous/pub/incoming/
atam:
	java -cp ${CLASSPATH} xtremweb.role.examples.AllToAll --master  --port=4325 --dir=/Users/ftp/anonymous/pub/incoming/ --workers=2
bcworker:
	java  -DPROPERTIES_FILE="conf/client.properties"  -cp ${CLASSPATH} xtremweb.role.examples.Broadcast --host=localhost  --port=4325 --dir=tmp --myHost=`uname -n`
ataw1:
	java  -DPROPERTIES_FILE="conf/client1.properties"  -cp ${CLASSPATH} xtremweb.role.examples.AllToAll --host=localhost  --port=4325 --dir=tmp1 --myHost=`uname -n`
ataw2:
	java  -DPROPERTIES_FILE="conf/client2.properties"  -cp ${CLASSPATH} xtremweb.role.examples.AllToAll --host=localhost  --port=4325 --dir=tmp2 --myHost=`uname -n`
	
benchmaster:
	java -cp ${CLASSPATH} xtremweb.role.bench.BenchMaster --workers 2

httpserver:
	java -cp ${CLASSPATH} xtremweb.core.http.HttpServer

ddcbench:
	java -cp ${CLASSPATH} xtremweb.role.bench.DDCBench localhost

ibptest:
	java -cp ${CLASSPATH} xtremweb.serv.dt.ibp.ibpTransfer	
	
amazons3test:
	java -cp ${CLASSPATH} xtremweb.serv.dt.amazonS3.AmazonS3Transfer
		
rmiregistry:
	CLASSPATH=${CLASSPATH} rmiregistry 4325&

wc:
	find src -name "*.java" | xargs cat | wc -l 
