ARG PLATFORM
ARG IMAGE
FROM --platform=${PLATFORM} ${IMAGE}  

WORKDIR /opt/power-meter
COPY target/power-meter*-shaded.jar /opt/power-meter/power-meter.jar

CMD java $JAVA_OPTS -jar /opt/power-meter/power-meter.jar -resource config.yml
