
target=/tmp/fly

all:	
	mvn dependency:copy-dependencies
	mvn package
	-mkdir -pv ${target}/{etc,lib,var}
	-cp -r src/main/resources/lib ${target}/3rd
	-cp target/fly.jar ${target}/
	-cp target/lib/* ${target}/lib
	-cp target/classes/{config.properties,logback.xml} ${target}/etc
	-cp tools/{run.sh,run.bat} ${target}/


clean:
	-rm -r ${target}
	mvn clean



