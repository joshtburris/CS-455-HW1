build:
	gradle clean
	gradle tasks
	gradle assemble
	gradle build

registry:
	java -cp ./build/libs/CS-455-HW1.jar cs455.overlay.node.Registry ${portnum}

messaging-node:
	java -cp ./build/libs/CS-455-HW1.jar cs455.overlay.node.MessagingNode ${hostname} ${portnum}
