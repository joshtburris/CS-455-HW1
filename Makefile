HOSTNAME=$(shell hostname)

all:
	gradle tasks
	gradle assemble
	gradle build

registry:
	javac ./cs455/overlay/node/Registry.java
	clear
	java cs455.overlay.node.Registry 1024

messaging-node:
	javac ./cs455/overlay/node/MessagingNode.java
	clear
	java cs455.overlay.node.MessagingNode ${HOSTNAME} 1024
