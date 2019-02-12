# hello-distributed-systems
This repository is intended as a "[Hello, World!](https://en.wikipedia.org/wiki/%22Hello,_World!%22_program)" for distributed systems in Java.

If you want to implement a distributed systems algorithm, you will first of all
need a distributed system, which means that you will need 2 or more
processes/machines that can
([probably](https://en.wikipedia.org/wiki/CAP_theorem)) talk to each other.

That means that before you even start, you will need

* 2 or more processes/machines
* a way for them to find each other ([service discovery](https://en.wikipedia.org/wiki/Service_discovery))
* a way for them to talk to each other ([communication protocol](https://en.wikipedia.org/wiki/Communication_protocol))

The aim of this repository is to provide a skeleton project that includes these
services. The rest is up to you!

### Service discovery

[Zeroconf](https://en.wikipedia.org/wiki/Zero-configuration_networking)
(implemented as Apple Bonjour and Avahi) is a reasonably well-known, solid
choice that I've worked with before.

Of the Java implementations, I've opted for
[jmDNS](https://github.com/jmdns/jmdns) (for now), because although there may
be some problems with it (see
[this question on SO](https://stackoverflow.com/questions/1233204/are-there-any-other-java-libraries-for-bonjour-zeroconf-apart-from-jmdns))
I haven't yet found a great replacement.

### Communication

The application supports HTTP `PUT` JSON. The default handler for incoming
messages simply prints them to stdout.

### Build and run

1. Start a first instance with `make run` and make a note of the port number
2. Start a second instance
3. Send data to the first instance e.g. `curl -X PUT http://localhost:5000 -H "Content-Type: application/json" -d '{"hello": "world"}'`
4. The second instance should print the received data
