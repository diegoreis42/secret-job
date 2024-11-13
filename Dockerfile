FROM maven:3.6.3-jdk-14

ADD . /usr/src/axreng
WORKDIR /usr/src/axreng
EXPOSE 4567
EXPOSE 5000
ENV JAVA_TOOL_OPTIONS="-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.port=5000 \
-Dcom.sun.management.jmxremote.rmi.port=5000 \
-Dcom.sun.management.jmxremote.host=0.0.0.0 \
-Djava.rmi.server.hostname=0.0.0.0"


ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]