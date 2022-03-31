#!/bin/bash

# Stoping tomcat
sudo systemctl stop webapp.service
rm -rf /home/ec2-user/webservice-target/*.jar