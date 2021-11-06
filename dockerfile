FROM openjdk:11-jdk
VOLUME /tmp
ADD build/libs/exchange-rate*.jar exchange-rate.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/exchange-rate.jar"]