FROM maven:3.5-jdk-8-alpine

ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

COPY . /usr/src/app
RUN mvn -B -Dmaven.javadoc.skip=true -T 1C -f /usr/src/app/pom.xml clean package

FROM openjdk:8-jre-alpine
COPY --from=0 /usr/src/app/jocds-cli/target/jocds-cli-*.jar /app.jar
ENTRYPOINT ["/usr/bin/java", "-jar", "-Dspring.profiles.active=default", "/app.jar"]