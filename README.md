# CS-455-HW1

Author: Joshua Burris (all code was written from scratch by me)

Assignment page: [Local PDF](CS455-Spring20-HW1-PC.pdf)

## Building

<code>CS-455-HW1$ make</code>

OR

<code>CS-455-HW1$ gradle clean</code>

<code>CS-455-HW1$ gradle tasks</code>

<code>CS-455-HW1$ gradle assemble</code>

<code>CS-455-HW1$ gradle build</code>

## Executing

### Registry:

<code>CS-455-HW1$ make registry portnum=<registry-port-number\></code>

OR

<code>CS-455-HW1$ java -cp ./build/libs/CS-455-HW1.jar cs455.overlay.node.Registry <registry-port-number\></code>

### Messaging Node:

<code>CS-455-HW1$ make messaging-node hostname=<registry-host-name\> portnum=<registry-port-number\></code>

OR

<code>CS-455-HW1$ java -cp ./build/libs/CS-455-HW1.jar cs455.overlay.node.MessagingNode <registry-host-name\> <registry-port-number\></code>

## Helpful Scripts

<code>CS-455-HW1$ ./start-nodes.sh</code>

