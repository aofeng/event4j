package cn.aofeng.event4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import cn.aofeng.common4j.ILifeCycle;
import cn.aofeng.threadpool4j.ThreadPool;

/**
 * 接受事件委托人（中介）
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
@SuppressWarnings("rawtypes")
public class Delegator implements ILifeCycle {

    private final static Logger logger = Logger.getLogger(Delegator.class);
    
    protected List<EventListener> listeners = new ArrayList<EventListener>();
    
    public Delegator() {
        
    }
    
    @Override
    public void init() {
        for (Iterator iterator = iterator(); iterator.hasNext();) {
            EventListener listener = (EventListener) iterator.next();
            listener.init();
        }
    }
    
    /**
     * 注册事件监听器
     * 
     * @param listener 事件监听器
     * @return 注册成功返回true；注册失败返回false
     */
    public boolean addListener(EventListener listener) {
        return listeners.add(listener);
    }
    
    /**
     * 移除事件监听器
     * 
     * @param listener 事件监听器
     * @return 移除成功返回true；移除失败返回false
     */
    public boolean removeListener(EventListener listener) {
        return listeners.remove(listener);
    }

    /**
     * 获取监听器数量
     * 
     * @return 监听器数量
     */
    public int getListenerCount() {
        return listeners.size();
    }
    
    /**
     * 通知所有注册的事件监听器处理事件
     * 
     * @param event 事件
     */
    public void fire(final Event event) {
        for (final EventListener listener : listeners) {
            ThreadPool.getInstance().submit(
                    new Task(listener, event));
        }
    }

    /**
     * 返回事件监听器列表的历遍器
     * 
     * @return 事件监听器列表的历遍器
     */
    public Iterator<EventListener> iterator() {
        return listeners.iterator();
    }
    
    @Override
    public void destroy() {
        for (Iterator iterator = iterator(); iterator.hasNext();) {
            EventListener listener = (EventListener) iterator.next();
            listener.destroy();
        }
    }
    
    /**
     * 线程池任务。
     * 
     * @author <a href="mailto:aofengblog@163.com">聂勇</a>
     */
    @SuppressWarnings("unchecked")
    private static class Task implements Runnable {

        private EventListener listener;
        
        private Event event;
        
        public Task(EventListener listener, Event event) {
            this.listener = listener;
            this.event = event;
        }
        
        @Override
        public void run() {
            try {
                this.listener.execute(event.clone());
            } catch (Exception e) {
                logger.error( String.format("execute listener %s occurs error", 
                        this.listener.getClass().getSimpleName()), e);
            }
        }
        
        public String toString() {
            if (null == this.event) {
                return "null";
            }
            
            return this.event.toString();
        }
    } // end of class 'Task'

}
