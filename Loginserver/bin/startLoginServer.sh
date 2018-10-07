#!/bin/bash

err=1
until [ $err == 0 ];
do
	java -Xms256m -Xmx512m  -cp './lib/*' com.l2jbr.loginserver.AuthServer
	err=$?
	sleep 10;
done