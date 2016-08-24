编译event4j
===
1、获取event4j源码。
---
```shell
git clone https://github.com/aofeng/event4j
```

2、编译源码生成jar。
---
进入项目根目录，执行ant脚本：
```shell
ant
```
会生成一个dist目录，下面有两个文件。如：
> event4j-1.0.0-src.jar    源码jar
>
> event4j-1.0.0.jar        用于发布的二进制jar
