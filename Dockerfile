FROM maven:3.5.3-jdk-8

ENV WORK_PATH  /product/synet
RUN mkdir -p $WORK_PATH  && \
    mkdir -p /product/log
WORKDIR $WORK_PATH

# lightning cloud
COPY synet-eureka-register/target/synet-eureka-register-1.0.0-SNAPSHOT.jar $WORK_PATH
COPY synet-server-gateway/target/synet-server-gateway-1.0.0-SNAPSHOT.jar $WORK_PATH
COPY synet-server-logic/target/synet-server-logic-1.0.0-SNAPSHOT.jar $WORK_PATH

CMD java -jar ${WORK_PATH}/synet-eureka-register-1.0.0-SNAPSHOT.jar & java -jar ${WORK_PATH}/synet-server-gateway-1.0.0-SNAPSHOT.jar & java -jar ${WORK_PATH}/synet-server-logic-1.0.0-SNAPSHOT.jar