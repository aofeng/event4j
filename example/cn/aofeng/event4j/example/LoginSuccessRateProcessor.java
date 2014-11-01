package cn.aofeng.event4j.example;

import cn.aofeng.event4j.AbstractEventListener;
import cn.aofeng.event4j.Event;

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
