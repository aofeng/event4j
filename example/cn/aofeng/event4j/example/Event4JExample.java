package cn.aofeng.event4j.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.aofeng.event4j.Event;
import cn.aofeng.event4j.EventDispatch;

/**
 * event4j(事件框架)使用示例代码。
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class Event4JExample {
    
    private static Logger _logger = Logger.getLogger(Event4JExample.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
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
    }

}
