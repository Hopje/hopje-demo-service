FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/*.jar /app.jar
ADD ./entrypoint.sh /entrypoint.sh

RUN mkdir /var/log/java && mkdir /data && sh -c 'touch /app.jar' && chmod a+x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

#Replace ENTRYPOINT with setting below in order to test the container without automatically starting the rxp-component
#ENTRYPOINT ["tail","-f", "/dev/null"]
