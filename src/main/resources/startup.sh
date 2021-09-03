#!/bin/bash

# Usage: execute.sh [WildFly mode] [configuration file]
#
# The default mode is 'standalone' and default configuration is based on the
# mode. It can be 'standalone.xml' or 'domain.xml'.

JBOSS_HOME=/opt/jboss/wildfly
JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh
JBOSS_MODE=${1:-"standalone"}
JBOSS_CONFIG=${2:-"$JBOSS_MODE.xml"}

#cp -vr /opt/jboss/wildfly/customization/oracle $JBOSS_HOME/modules/system/layers/base
#cp -vr /opt/jboss/wildfly/customization/bin/* $JBOSS_HOME/bin
ls -al $JBOSS_HOME/bin

# Deploy the WAR
cp -vr /opt/jboss/wildfly/customization/notification-sender.jar $JBOSS_HOME/$JBOSS_MODE/deployments/notification-sender.jar

echo "=> Restarting WildFly"
$JBOSS_HOME/bin/$JBOSS_MODE.sh -b 0.0.0.0 -c $JBOSS_CONFIG