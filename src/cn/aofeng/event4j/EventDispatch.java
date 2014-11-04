package cn.aofeng.event4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cn.aofeng.common4j.ILifeCycle;
import cn.aofeng.common4j.reflection.ReflectionUtil;
import cn.aofeng.common4j.xml.DomUtil;
import cn.aofeng.common4j.xml.NodeParser;
import cn.aofeng.threadpool4j.ThreadPool;

/**
 * 事件调度器
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class EventDispatch implements ILifeCycle {

    private final static Logger logger = Logger.getLogger(EventDispatch.class);
    
    protected Map<String, Delegator> eventMap = new HashMap<String, Delegator>();
    
    private String _configFile = "/event4j.xml";
    
    protected AtomicBoolean _isStarted = new AtomicBoolean(false);
    
    private static EventDispatch instance = new EventDispatch();
    
    private EventDispatch() {
    }
    
    public static EventDispatch getInstance() {
        return instance;
    }
    
    /**
     * 根据事件类型分发事件。所有注册监听该事件的监听器将被通知。
     * 
     * @param event 事件
     */
    public void dispatch(Event<?> event) {
        if (logger.isDebugEnabled()) {
            logger.debug("dispatch event:"+event);
        }
        
        Delegator delegator = eventMap.get(event.getEventType());
        if (null != delegator) {
            delegator.fire(event);
        } else {
            logger.warn( String.format("event %s has no listener", event.toString()) );
        }
    }
    
    /**
     * 从配置文件中载入事件类型及其事件监听器
     */
    @SuppressWarnings({"rawtypes" })
    public void init() {
        if (_isStarted.get()) {
            return;
        }
        
        // 先初始化线程池
        ThreadPool.getInstance().init();
        
        // 读取配置文件event4j.xml，生成事件及其监听器集合列表
        Document document = DomUtil.createDocument(_configFile);
        Element rootNode = document.getDocumentElement();
        NodeParser rootParser = new NodeParser(rootNode);
        List<Node> eventNodes = rootParser.getChildNodes();
        for (Node eventNode : eventNodes) {
            NodeParser eventParset = new NodeParser(eventNode);
            String eventType = eventParset.getAttributeValue("type");
            Delegator deletator = createDelegator(eventParset);
            deletator.init();
            eventMap.put(eventType, deletator);
        }
        
        if (logger.isInfoEnabled()) {
            Iterator<Entry<String, Delegator>> delegatorIterator = iterator();
            while (delegatorIterator.hasNext()) {
                Entry<String, Delegator> entry = (Entry<String, Delegator>) delegatorIterator.next();
                logger.info( String.format("event '%s' has listeners:", entry.getKey()) );
                Iterator<EventListener> listenerIterator = entry.getValue().iterator();
                while (listenerIterator.hasNext()) {
                    EventListener eventListener = (EventListener) listenerIterator.next();
                    logger.info("    "+eventListener.getClass().getName());
                }
            }
        }
        
        _isStarted.set(true);
    }

    /**
     * 获取事件及其监听器列表的历遍器
     * 
     * @return 事件及其监听器列表的历遍器
     */
    public Iterator<Entry<String, Delegator>> iterator() {
        return eventMap.entrySet().iterator();
    }
    
    public void setConfigFile(String classpathFile) {
        this._configFile = classpathFile;
    }
    
    private EventListener<?> createListener(String className) {
        return ReflectionUtil.createInstance(className);
    }
    
    private Delegator createDelegator(NodeParser nodeParser) {
        Delegator deletator = new Delegator();
        
        List<Node> listenerNodes = nodeParser.getChildNodes();
        for (Node listenerNode : listenerNodes) {
            EventListener<?> listener = createListener(StringUtils.trim(listenerNode.getTextContent()));
            if (null == listener) {
                logger.warn(String.format("create event listener '%s' fail, please check configure file '%s'", listenerNode.getTextContent(), _configFile));
                continue;
            }

            deletator.addListener(listener);
        }
        
        return deletator;
    }

    @Override
    public void destroy() {
        // 先关闭所有的事件
        for (Iterator<Entry<String, Delegator>> iterator = iterator(); iterator.hasNext();) {
            Entry<String, Delegator> entry = iterator.next();
            entry.getValue().destroy();
        }
        
        // 再关闭线程池
        ThreadPool.getInstance().destroy();
        
        _isStarted.set(false);
    }

}
