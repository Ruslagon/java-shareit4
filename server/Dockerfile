FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar app.jar
RUN apk update && \
    apk add --no-cache tzdata
run apk add -U tzdata
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["java","-jar","/app.jar"]
