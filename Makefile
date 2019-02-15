runcmd := "mvn spring-boot:run -Dspring-boot.run.arguments=--spring.main.banner-mode=off"
ifdef min_port
	runcmd := "$(runcmd),--minPort=$(min_port)"
endif
ifdef max_port
	runcmd := "$(runcmd),--maxPort=$(max_port)"
endif
ifdef service_name
	runcmd := "$(runcmd),--serviceName=$(service_name)"
endif

clean:
	mvn clean

install: clean
	mvn install
	
test: clean
	mvn test

run: clean
	eval $(runcmd)
