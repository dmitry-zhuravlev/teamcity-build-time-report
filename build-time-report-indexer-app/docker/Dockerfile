FROM kurron/docker-oracle-jdk-8

MAINTAINER Dmitry Zhuravlev <dmitry.anatolyevich.zhuravlev@gmail.com>

RUN mkdir /opt/build-time-report-indexer-app

ADD files/build-time-report-indexer-app.jar /opt/build-time-report-indexer-app/build-time-report-indexer-app.jar

EXPOSE 8080

ENTRYPOINT ["/opt/launch-jvm.sh", "-jar", "/opt/build-time-report-indexer-app/build-time-report-indexer-app.jar"]