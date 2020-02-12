# CS-455-HW1

Author: Joshua Burris (all code was written from scratch by me)

Assignment page: [Local PDF](CS455-Spring20-HW1-PC.pdf)

## Building

<code>CS-455-HW1$ ./build.sh</code>

OR

<code>CS-455-HW1$ gradle clean</code>

<code>CS-455-HW1$ gradle tasks</code>

<code>CS-455-HW1$ gradle assemble</code>

<code>CS-455-HW1$ gradle build</code>

## Executing

### Registry:

<code>CS-455-HW1$ ./registry.sh <registry-port-number\></code>

OR

<code>CS-455-HW1$ java -cp ./build/libs/CS-455-HW1.jar cs455.overlay.node.Registry <registry-port-number\></code>

### Messaging Node:

<code>CS-455-HW1$ ./messaging-node.sh <registry-host-name\> <registry-port-number\></code>

OR

<code>CS-455-HW1$ java -cp ./build/libs/CS-455-HW1.jar cs455.overlay.node.MessagingNode <registry-host-name\> <registry-port-number\></code>

## Helpful Scripts

<code>CS-455-HW1$ ./start-nodes.sh</code>

This script uses the file "conf/machine_list" as a way of spawning a large number of messaging nodes very quickly. It spawns one node per machine in the list and opens them all in a window of terminals. They all try to connect to the registry simultaneously. This will however fail if the registry didn't receive the port number 1024, which can be fixed in the script or possibly by restarting the registry. This rarely happens, but the registry will inform you of the port number it was assigned so that you can change the script accordingly.
