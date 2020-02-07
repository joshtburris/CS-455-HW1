DIR="$( cd "$( dirname "$0" )" && pwd )"
JAR_PATH="$DIR/conf/:$DIR/out/artifacts/CS_455_HW1_jar/CS-455-HW1.jar"
MACHINE_LIST="$DIR/conf/machine_list"

SCRIPT="java -cp $JAR_PATH cs455.overlay.node.MessagingNode dover 1024"
COMMAND='gnome-terminal --geometry=200x40'

for machine in `cat $MACHINE_LIST`
do
    OPTION='--tab -t "'$machine'" -e "ssh -t '$machine' cd '$DIR'; echo '$SCRIPT'; '$SCRIPT'"'
    COMMAND+=" $OPTION"
done
eval $COMMAND &
