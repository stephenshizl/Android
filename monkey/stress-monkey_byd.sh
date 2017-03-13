#!/bin/sh
while [ 1 ]
do
monkey --throttle 200 -v -v -v --ignore-crashes --ignore-timeouts --ignore-security-exceptions --monitor-native-crashes 999999999
sleep 120
input keyevent 26
done