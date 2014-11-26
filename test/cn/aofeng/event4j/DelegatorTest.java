 package cn.aofeng.event4j;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.aofeng.threadpool4j.ThreadPool;

/**
 * {@link Delegator}的单元测试用例 <br/>
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class DelegatorTest {

    private Delegator _deletator;
    
    @BeforeClass
    public static void beforeClass() {
        // 初始化线程池
        ThreadPool.getInstance().init();
    }
    
    @Before
    public void setUp() throws Exception {
        _deletator = new Delegator();
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @AfterClass
    public static void afterClass() {
        // 销毁线程池
        ThreadPool.getInstance().destroy();
    }

    /**
     * 测试用例：注册监听器 <br/>
     * 前置条件：注册一个监听器 <br/>
     * 结果：监听器数量为1
     */
    @Test
    public void testAddListener() {
        _deletator.addListener(createEventListenerMock(1));
        
        assertEquals(1, _deletator.getListenerCount());
    }

    /**
     * 测试用例：移除监听器 <br/>
     * 前置条件：已经有一个监听器，移除该注册器 <br/>
     * 结果：监听器数量为0
     */
    @Test
    public void testRemoveListener() {
        EventListener<DataObj> listener = createEventListenerMock(1);
        _deletator._listeners.add(listener);
        
        _deletator.removeListener(listener);
        assertEquals(0, _deletator.getListenerCount());
    }
    
    /**
     * 测试用例：获取监听器数量 <br/>
     * 前置条件：初始化{@link Delegator} <br/>
     * 结果：监听器数量为0
     */
    @Test
    public void testGetListenerCount() {
        assertEquals(0, _deletator.getListenerCount());
    }
    
    /**
     * 测试用例：通知所有注册的监听器 <br/>
     * 前置条件：注册两个监听器并通知它们 <br/>
     * 结果：两个监听器都被调用1次
     */
    @Test
    public void testFire() throws InterruptedException {
        EventListener<DataObj> mock1 = createEventListenerMock(1);
        EventListener<DataObj> mock2 = createEventListenerMock(1);
        
        _deletator.addListener(mock1);
        _deletator.addListener(mock2);
        _deletator.fire(new Event<DataObj>());
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        EasyMock.verify(mock1, mock2);
    }
    
    /**
     * 测试用例：通知所有注册的监听器 <br/>
     * 前置条件：注册一个监听器并分派事件给它；事件没有配置clone属性 <br/>
     * 结果：监听器都被调用1次，分派事件时事件被clone。
     */
    @Test
    public void testFire4EventDefaultClone() throws InterruptedException {
        EventListener<DataObj> listenerMock = createEventListenerMock(1);
        _deletator.addListener(listenerMock);
        Event<DataObj> eventMock = createEvent(1);
        _deletator.fire(eventMock);
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        EasyMock.verify(listenerMock);
        EasyMock.verify(eventMock);
    }
    
    /**
     * 测试用例：通知所有注册的监听器 <br/>
     * 前置条件：注册一个监听器并分派事件给它；事件设置clone=true <br/>
     * 结果：监听器都被调用1次，分派事件时事件被clone。
     */
    @Test
    public void testFire4EventSetClone() throws InterruptedException {
        _deletator.setNeedClone(true);
        EventListener<DataObj> listenerMock = createEventListenerMock(1);
        _deletator.addListener(listenerMock);
        Event<DataObj> eventMock = createEvent(1);
        _deletator.fire(eventMock);
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        EasyMock.verify(listenerMock);
        EasyMock.verify(eventMock);
    }
    
    /**
     * 测试用例：通知所有注册的监听器 <br/>
     * 前置条件：注册一个监听器并分派事件给它；事件设置clone=false <br/>
     * 结果：监听器都被调用1次，分派事件时事件没有执行clone。
     */
    @Test
    public void testFire4EventNotClone() throws InterruptedException {
        _deletator.setNeedClone(false);
        EventListener<DataObj> listenerMock = createEventListenerMock(1);
        _deletator.addListener(listenerMock);
        Event<DataObj> eventMock = createEvent(1);
        _deletator.fire(eventMock);
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        EasyMock.verify(listenerMock);
        boolean occursError = false;
        try {
            EasyMock.verify(eventMock);
        } catch (AssertionError e) {
            occursError = true;
            assertTrue(e.getMessage().endsWith("Event.clone(): expected: 1, actual: 0"));
        } // end of try-catch
        assertTrue("设置事件不执行clone操作，结果执行了1次clone", occursError);
    }
    
    @SuppressWarnings("unchecked")
    private Event<DataObj> createEvent(int cloneCallTimes) {
        Event<DataObj> mock = EasyMock.createMock(Event.class);
        mock.clone();
        EasyMock.expectLastCall().andReturn( EasyMock.createMock(Event.class))
                .times(cloneCallTimes);
        EasyMock.replay(mock);
        
        return mock;
    }
    
    /**
     * 创建{@link EventListener}的Mock。
     * @param callTimes 事件被调用的次数
     * @return {@link EventListener}的Mock
     */
    @SuppressWarnings("unchecked")
    private EventListener<DataObj> createEventListenerMock(int callTimes) {
        EventListener<DataObj> mock = EasyMock.createMock(EventListener.class);
        mock.execute( EasyMock.anyObject(Event.class) );
        EasyMock.expectLastCall().times(callTimes);
        EasyMock.replay(mock);
        
        return mock;
    }

}
