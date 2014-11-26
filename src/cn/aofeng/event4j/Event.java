package cn.aofeng.event4j;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 事件及其数据。
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class Event<T> implements Cloneable, Serializable {

    private static final long serialVersionUID = 1525441397618531878L;

    private String eventType;
    
    private T data;
    
    public Event() {
        
    }
    
    /**
     * 
     * @param eventType 事件类型。
     * @param data 事件附带的数据。如果是非原型数据类型，必须实现clone方法并且implements Cloneable接口
     */
    public Event(String eventType, T data) {
        this.eventType = eventType;
        this.data = data;
    }

    public String getEventType() {
        return eventType;
    }

    /**
     * 设置事件类型
     * 
     * @param eventType 事件类型。
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * 获取事件附带的数据
     * 
     * @return 事件附带的数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置事件附带的数据
     * @param data 事件附带的数据。如果是非原型数据类型，必须实现clone方法并且implements Cloneable接口
     */
    public void setData(T data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Event<T> clone() {
        Event<T> event = new Event<T>();
        event.setEventType(this.eventType);
        event.setData((T) ObjectUtils.clone(this.data));
        
        return event;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Event)) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        Event<T> rhs = (Event<T>) object;
        return new EqualsBuilder()
                .append(this.eventType, rhs.eventType)
                .append(this.data, rhs.data)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1714526201, 1858252307)
                .append(this.eventType)
                .append(this.data)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Event [eventType=" + eventType + ", data=" + data + "]";
    }

}
