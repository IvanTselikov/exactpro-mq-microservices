#!/bin/bash
while ! nc -z host.docker.internal 15672; do sleep 3; done
sleep 20
java -jar Consumer.jar