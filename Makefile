clean:
	mvn clean

install: clean
	mvn install
	
test: clean
	mvn test

run: install
	java -jar target/hello-distributed-systems-0.1-SNAPSHOT.jar