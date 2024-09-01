package cn.aofeng.event4j;

import cn.aofeng.common4j.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象事件监听器
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public abstract class AbstractEventListener<T> implements EventListener<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /** 事件监听器运行时指定的线程池的名称 */
    protected String _threadPoolName;
    
    /**
     * 设置运行时的线程池为default。
     */
    public AbstractEventListener() {
        _threadPoolName = DEFAULT_THREAD_POOL_NAME;
    }
    
    /**
     * @param threadPoolName 运行时的线程池名称。
     */
    public AbstractEventListener(String threadPoolName) {
        if (StringUtil.isBlank(threadPoolName)) {
            throw new IllegalArgumentException("invalid thread-pool name:"+threadPoolName);
        }
        
        _threadPoolName = threadPoolName;
    }
    
    public void setThreadPoolName(String threadPoolName) {
        _threadPoolName = threadPoolName;
    }
    
    public String getThreadPoolName() {
        return _threadPoolName;
    }
    
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
