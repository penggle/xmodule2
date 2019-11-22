FROM anapsix/alpine-java:8_jdk

MAINTAINER PengPeng

ENV TZ Asia/Shanghai

RUN echo "http://mirrors.aliyun.com/alpine/v3.6/main/" > /etc/apk/repositories \
        && apk --no-cache add tzdata \
        && ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime \
        && echo "${TZ}" > /etc/timezone

ARG JAR_FILE

WORKDIR /

ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]