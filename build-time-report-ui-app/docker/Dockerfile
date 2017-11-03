FROM kurron/docker-oracle-jdk-8

MAINTAINER Dmitry Zhuravlev <dmitry.anatolyevich.zhuravlev@gmail.com>

RUN mkdir /opt/build-time-report-ui-app

ADD files/build-time-report-ui-app.jar /opt/build-time-report-ui-app/build-time-report-ui-app.jar

EXPOSE 8080

ENTRYPOINT ["/opt/launch-jvm.sh", "-jar", "/opt/build-time-report-ui-app/build-time-report-ui-app.jar"]