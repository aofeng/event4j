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
    
    protected boolean _needClone = true;
    
    protected List<EventListener> _listeners = new ArrayList<EventListener>();
    
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
        return _listeners.add(listener);
    }
    
    /**
     * 移除事件监听器
     * 
     * @param listener 事件监听器
     * @return 移除成功返回true；移除失败返回false
     */
    public boolean removeListener(EventListener listener) {
        return _listeners.remove(listener);
    }

    /**
     * 获取监听器数量
     * 
     * @return 监听器数量
     */
    public int getListenerCount() {
        return _listeners.size();
    }
    
    /**
     * 通知所有注册的事件监听器处理事件
     * 
     * @param event 事件
     */
    public void fire(final Event event) {
        for (final EventListener listener : _listeners) {
            ThreadPool.getInstance().submit(
                    new Task(listener, event, _needClone));
        }
    }

    /**
     * 返回事件监听器列表的历遍器
     * 
     * @return 事件监听器列表的历遍器
     */
    public Iterator<EventListener> iterator() {
        return _listeners.iterator();
    }
    
    /**
     * 获取事件在分派时是否clone一份交给事件监听器
     * 
     * @return 事件在分派时是否clone一份交给事件监听器
     */
    public boolean isNeedClone() {
        return _needClone;
    }
    
    public void setNeedClone(boolean needClone) {
        _needClone = needClone;
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

        private EventListener _listener;
        
        private Event _event;
        
        private boolean _needClone;
        
        public Task(EventListener listener, Event event, boolean needClone) {
            _listener = listener;
            _event = event;
            _needClone = needClone;
        }
        
        @Override
        public void run() {
            try {
                if (_needClone) {
                    _listener.execute(_event.clone());
                } else {
                    _listener.execute(_event);
                }
            } catch (Exception e) {
                logger.error( String.format("execute listener %s occurs error", 
                        this._listener.getClass().getSimpleName()), e);
            }
        }
        
        public String toString() {
            if (null == this._event) {
                return "null";
            }
            
            return this._event.toString();
        }
    } // end of class 'Task'

}
