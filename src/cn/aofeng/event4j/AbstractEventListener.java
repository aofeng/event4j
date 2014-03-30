package cn.aofeng.event4j;

import org.apache.log4j.Logger;

/**
 * 抽象事件监听器
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public abstract class AbstractEventListener<T> implements EventListener<T> {

    protected final Logger logger = Logger.getLogger(this.getClass());
    
    @Override
    public void init() {
        if (logger.isInfoEnabled()) {
            logger.info( String.format("event listener '%s' initialized", this.getClass().getSimpleName()) );
        }
    }

    @Override
    public void destroy() {
        if (logger.isInfoEnabled()) {
            logger.info( String.format("event listener '%s' destroyed", this.getClass().getSimpleName()) );
        }
    }

}
