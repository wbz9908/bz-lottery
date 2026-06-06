FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

ARG JAR_FILE
COPY ${JAR_FILE} /app/app.jar

ENV JAVA_OPTS=""

EXPOSE 9008

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
