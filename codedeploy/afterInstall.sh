#!/bin/bash

sudo systemctl stop webapp.service

#Remove last build
sudo rm -rf /home/ec2-user/*.jar