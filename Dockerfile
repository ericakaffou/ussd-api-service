FROM openjdk:27-ea-jdk-slim-trixie
RUN mkdir /jar
COPY target/*.jar /jar/
EXPOSE 9091

