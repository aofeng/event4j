package cn.aofeng.event4j.example;

/**
 * 生成用户登陆信息。
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class GenerateLoginRecords {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int count = 1000;
        for (int i = 0; i < count; i++) {
            // loginTime`ip`userName`resultCode
            System.out.println( String.format("%d`%s`%s`%d", 
                    System.currentTimeMillis(), 
                    "192.168.56.1", 
                    "account_"+i, 
                    (i % 2 == 0) ? 1 : 0 ) );
        }
    }

}
