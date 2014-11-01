package cn.aofeng.event4j.example;

import org.apache.commons.lang.StringUtils;

import cn.aofeng.event4j.AbstractEventListener;
import cn.aofeng.event4j.Event;
import cn.aofeng.event4j.EventDispatch;

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
