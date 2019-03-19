# SyNet
game server 
整合 spring 和 netty 的游戏服务器


1. 编译项目

mvn clean install -Dmaven.test.skip=true

2.镜像制作

docker build -f Dockerfile -t synet/synet-test:1.0 .