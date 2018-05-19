#!/usr/bin/env sh

#TODO: THIS SCRIPT DOESNT WORK YET INSIDE openjdk:8-jdk !!!!

# Script to override the properties-files settings by environment variables and start the run.sh
# Names of the environment variables must be:
# - starting with "APPLICATION_" to replace a setting in application.properties
# Notes:
# - the environment name will be made lowercase and the '_' will be replaced by a '.' to match the .properties setting
# - the setting in the .properties file will be replaced (if exists) or added at the end of the file
# Restrictions:
# - the .properties settings may not have spaces around the '=' sign
# - the value of the environment variable may not contain '@'

waitBeforeStart=${WAIT_BEFORE_START:-0}
echo waiting before start... $waitBeforeStart
sleep $waitBeforeStart

if [[ -z "$APP_HOME" ]]; then
    export APP_HOME=/app.jar
fi
echo > /application.properties

IFS=$'\n'
for VAR in `env`
do
  # adjust application.properties by environment variables APPLICATION_*
  if echo "$VAR" | grep -q "^APPLICATION_"; then
    application_name=`echo "$VAR" | sed -r "s/APPLICATION_([^=]*)=(.*)/\1/g" | tr '[:upper:]' '[:lower:]' | tr _ .`
    env_var=`echo "$VAR" | sed -r "s/([^=]*)=(.*)/\1/g"`
    env_value=`echo "$VAR" | sed -r "s/([^=]*)=(.*)/\2/g"`
    if egrep -q "(^|^#)$application_name=" /application.properties; then
        sed -r -i "s@(^|^#)($application_name)=(.*)@\2=${env_value}@g" /application.properties #note that no config values may contain an '@' char
    else
        sed -r -i "\$a$application_name=${env_value}" /application.properties
    fi
  fi
done

#TEMPORARY!!!! TODO: replace next line with cleaner solution that has application.properties in classpath
#cp $CONFIG_HOME/application.properties /

if [[ -n "$CUSTOM_INIT_SCRIPT" ]] ; then
  eval $CUSTOM_INIT_SCRIPT
fi

APP_PID=0

# see https://medium.com/@gchudnov/trapping-signals-in-docker-containers-7a57fdda7d86#.bh35ir4u5
term_handler() {
  echo 'Stopping Application....'
  if [ $APP_PID -ne 0 ]; then
    kill -s TERM "$APP_PID"
    wait "$APP_PID"
  fi
  echo 'Application stopped.'
  exit
}

# Capture kill requests to stop properly
trap "term_handler" SIGHUP SIGINT SIGTERM
java -cp /application.properties -jar $APP_HOME &
APP_PID=$!

wait "$APP_PID"
