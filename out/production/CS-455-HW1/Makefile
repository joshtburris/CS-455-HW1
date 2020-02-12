HOSTNAME=$(shell hostname)

all:
	gradle clean
	gradle tasks
	gradle assemble
	gradle build
	gradle registryJar
	gradle messagingNodeJar

registry:
	#javac ./cs455/overlay/node/Registry.java
	clear
	java cs455.overlay.node.Registry 1024

messaging-node:
	#javac ./cs455/overlay/node/MessagingNode.java
	clear
	java cs455.overlay.node.MessagingNode ${HOSTNAME} 1024
