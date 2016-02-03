#! /bin/bash

source /etc/profile

dir=/home/qgaea/datayes/bdb/theme-index-0.1.0
cd $dir


if [ -n "$JAVA_HOME" ]; then
    for java in "$JAVA_HOME"/bin/amd64/java "$JAVA_HOME"/bin/java; do
        if [ -x "$java" ]; then
            JAVA="$java"
            break
        fi
    done
else
    JAVA=java
    echo 'please set JAVA_HOME'
    exit
fi


CLASSPATH=.:etc_batch

for i in lib/*.jar
do
  CLASSPATH=$CLASSPATH:$i
done

CLASSNAME="com.datayes.bdb.theme.index.batch.Bootstrap"

DEBUG='-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044'
#updateDay=`date +%Y%m%d`
#echo $updateDay
nohup $JAVA -classpath $CLASSPATH -Dconfig.dir=$dir/etc_batch $CLASSNAME dump > logs/batch-startup.log 2>&1 &
#endDatetime=`date`
#echo $endDatetiem >> logs/everyday_task.log
