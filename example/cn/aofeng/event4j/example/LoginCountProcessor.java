package cn.aofeng.event4j.example;

import cn.aofeng.event4j.AbstractEventListener;
import cn.aofeng.event4j.Event;

/**
 * 登陆数量统计器。
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
