package cn.aofeng.event4j;

import cn.aofeng.common4j.ILifeCycle;


/**
 * 处理指定事件的事件监听器。事件监听器必须是无状态的，
 * 因为监听器被初始化（调用默认的构造方法然后执行init方法进行初始化）后会一直持有这个实例处理事件。
 * 如果处理过程中涉及到共享资源，需要考虑线程安全。
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public interface EventListener<T> extends ILifeCycle {
    
    /**
     * 处理事件
     * 
     * @param event 事件及附带的数据
     */
    public void execute(Event<T> event);

}
