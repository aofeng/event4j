#event4j入门指南
为了更好地理解event4j，通过实现一个实例来展示event4j的用途：
##用例
有一个日志文件LoginInfoRecords.txt，里面记录着用户的登陆信息，每行共4个字段，用符号“\`”隔开，分别是：登陆时间、来源IP、账号、登陆结果。从日志文件中统计总登陆次数和登陆成功率。

##实现思路
1、实现一个文件读取器，按行读取。每读取一行，产生一个“完成读取一行（`ReadLineComplete`）”的事件。

2、实现一个格式转换器，监听事件“完成读取一行”。完成格式转换后，产生一个“完成格式转换（`LoginInfoCodecComplete`）”的事件。

3、实现两个数据处理器：登陆次数统计器、登陆成功率统计器，它们监听事件“完成格式转换”。
![event4j](http://img2.ph.126.net/mseLTsjjlx5KyhFICGeh2g==/640355572034101652.png)

###编写程序
####1、依赖关系及其配置。
`event4j`依赖`threadpool4j`，需要的jar列表如下：
* common4j-0.1.0.jar
* commons-lang-2.6.jar
* log4j-1.2.16.jar
* threadpool4j-1.0.0.jar

####2、配置threadpool4j。
在应用的CLASSPATH的任意路径（如：应用的classes目录）下新文本文件建threadpool4j.xml，其内容如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<threadpool4j>
    <pool name="default">
        <corePoolSize>30</corePoolSize>
        <maxPoolSize>150</maxPoolSize>
        <!-- 线程空闲存话的时间。单位：秒 -->
        <keepAliveTime>5</keepAliveTime>
        <workQueueSize>100000</workQueueSize>
    </pool>
</threadpool4j>
```

####3、配置event4j。
在应用的CLASSPATH的任意路径（如：应用的classes目录）下新建文本文件event4j.xml的配置文件，其内容如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<events>
    <!-- 
    <event>中的type属性表示事件类型（亦可以称之为事件名称）；
                  clone属性表示在将事件分发至listener时，是否对事件及其数据进行clone，true表示进行clone（默认值），false表示不clone；
                  每个event可以有1个或多个listener。
    <listener>中的threadpool属性表示当前监听器由哪个线程池执行。如果没有该属性，表示交给default线程池执行。
                      value值是事件监听器的完整类名。
     -->
     
    <!-- 事件：完成读取一行 -->
    <event type="ReadLineComplete"  clone="false">
        <listener>cn.aofeng.event4j.example.LoginInfoCodec</listener>
    </event>
    
    <!-- 事件：完成格式转换 -->
    <event type="LoginInfoCodecComplete"  clone="false">
        <listener  threadpool="default">cn.aofeng.event4j.example.LoginCountProcessor</listener>
        <listener>cn.aofeng.event4j.example.LoginSuccessRateProcessor</listener>
    </event>

</events>
```

####4、实现文件读取器。
```java
// 1. 初始化event4j
EventDispatch.getInstance().init();

// 2. 事件分派
BufferedReader reader = null;
String line = "";
try {
    InputStream ins = Event4JExample.class.getResourceAsStream("/cn/aofeng/event4j/example/LoginRecords.txt");
    reader = new BufferedReader(new InputStreamReader(ins));
    do {
        line = reader.readLine();
        if (StringUtils.isBlank(line)) {
            continue;
        }
        Event<String> event = new Event<String>("ReadLineComplete", line);
        EventDispatch.getInstance().dispatch(event);
    } while (null != line);
} catch (Exception e) {
    _logger.error("read file [CLASSPATH/cn/ofeng/event4j/example/LoginRecords.txt] occurs error", e);
} finally {
    try {
        reader.close();
    } catch (IOException e) {
        // nothing
    }
} // end of try catch finally block

// 3. 关闭event4j，释放资源
EventDispatch.getInstance().destroy();
```
其源码可查看文件[Event4JExample.java](https://github.com/aofeng/event4j/blob/master/example/cn/aofeng/event4j/example/Event4JExample.java)


####5、实现格式转换器。
```java
/**
 * 数据转换器：将一行字符串转换成{@link LoginInfo}对象。
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class LoginInfoCodec extends AbstractEventListener<String> {
    
    @Override
    public void execute(Event<String> event) {
        // 1. 校验数据的有效性
        String line = event.getData();
        if (StringUtils.isBlank(line)) {
            return;
        }
        
        // 2. 将行数据转换成Java对象
        String[] datas = line.split("`");
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setLoginTime(Long.parseLong(datas[0]));
        loginInfo.setIp(datas[1]);
        loginInfo.setUserName(datas[2]);
        loginInfo.setResultCode(Integer.parseInt(datas[3]));
        
        //  3. 生成事件并分派
        Event<LoginInfo> loginInfoEvent = new Event<LoginInfo>("LoginInfoCodecComplete", loginInfo);
        EventDispatch.getInstance().dispatch(loginInfoEvent);
    }

}
```
其源码可查看文件[LoginInfoCodec.java](https://github.com/aofeng/event4j/blob/master/example/cn/aofeng/event4j/example/LoginInfoCodec.java)

####6、实现登陆次数统计器。
```java
/**
 * 登陆次数统计器。
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class LoginCountProcessor extends  AbstractEventListener<LoginInfo> {

    private long _count;
    
    @Override
    public void execute(Event<LoginInfo> event) {
        if (null == null) {
            return;
        }
        
        _count ++;
        
        // 后续如何输出数据可自行处理 。。。
    }

}
```
其源码可查看文件[LoginCountProcessor.java](https://github.com/aofeng/event4j/blob/master/example/cn/aofeng/event4j/example/LoginCountProcessor.java)

####7、实现登陆成功率统计器。
```java
/**
 * 登陆成功率统计器。
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class LoginSuccessRateProcessor extends  AbstractEventListener<LoginInfo> {

    private long _total;
    
    private long _success;
    
    @Override
    public void execute(Event<LoginInfo> event) {
        if (null == null) {
            return;
        }
        
        _total ++;
        if (1 == event.getData().getResultCode()) {
            _success ++;
        }
        
        // 后续如何输出数据可自行处理 。。。
    }

}
```
其源码可查看文件[LoginSuccessRateProcessor.java](https://github.com/aofeng/event4j/blob/master/example/cn/aofeng/event4j/example/LoginSuccessRateProcessor.java)
