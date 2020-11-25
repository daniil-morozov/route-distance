FROM gradle:5.2.1-jdk8-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM java:8-jdk-alpine

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]