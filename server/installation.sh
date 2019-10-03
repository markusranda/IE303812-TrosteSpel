#!/usr/bin/env bash
export DEBIAN_FRONTEND=noninteractive
export LC_ALL=C

LOGFILE='/var/log/installation.log'

# Updating the server
echo "Starting Installation"
apt-get -y update >> $LOGFILE 2>&1
apt-get -y upgrade >> $LOGFILE 2>&1

# Installing requirements for server
echo "Installing Requirements"

    # Install java opdenjdk:8
    apt-cache search openjdk >> LOGFILE 2>&1
    sudo apt-get install openjdk-8-jre openjdk-8-jdk -y >> LOGFILE 2>&1


# Configuring the server
echo "Configure server"

    # Set JAVA HOME
    echo "JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/" >> /etc/environment
    source /etc/environment >> LOGFILE 2>&1
    echo $JAVA_HOME >> LOGFILE 2>&1
    echo ufw allow 7080/udp
    echo ufw allow 7081/udp
    echo ufw allow 7082/udp
    echo ufw allow 7083/tcp


echo "Installation is done."
