FROM adoptopenjdk/openjdk11:alpine-jre

ARG JAR_FILE=target/DevOps-0.0.1-SNAPSHOT.jar

WORKDIR /opt/app

COPY ${JAR_FILE} DevOps-0.0.1-SNAPSHOT.jar

COPY entrypoint.sh entrypoint.sh

RUN chmod 755 entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]