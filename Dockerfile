FROM openjdk:17.0.2-slim-bullseye

# 镜像信息
LABEL kangert <kangert@qq.com>

#设置中文字符集
ENV LANG zh_CN.UTF-8
ENV LANGUAGE zh_CN.UTF-8

# 复制jar包
COPY ./kspider-web/build/libs/kspider-web-0.0.1-SNAPSHOT.jar /opt/app.jar

# 暴露端口
EXPOSE 8086

# 定义变量接收器
ARG envType
ARG JVM_ARGS

# 组装启动脚本
ENV APP_START_CMD="java ${JVM_ARGS} -Dspring.profiles.active=${envType} -Dfile.encoding=UTF-8 -jar /opt/app.jar"

# 打印命令
RUN echo ${APP_START_CMD}

# 设置启动脚本
ENTRYPOINT ${APP_START_CMD}