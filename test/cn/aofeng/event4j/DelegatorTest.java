 package cn.aofeng.event4j;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.aofeng.common4j.ILifeCycle;
import cn.aofeng.threadpool4j.ThreadPool;
import cn.aofeng.threadpool4j.ThreadPoolImpl;

/**
 * {@link Delegator}的单元测试用例 <br/>
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class DelegatorTest {

    private Delegator _deletator;
    
    private static  ILifeCycle _threadpool;
    
    @BeforeClass
    public static void beforeClass() {
        // 初始化线程池
        _threadpool = new ThreadPoolImpl();
        _threadpool.init();
    }
    
    @Before
    public void setUp() throws Exception {
        _deletator = new Delegator();
        _deletator._threadPool = (ThreadPool) _threadpool;
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @AfterClass
    public static void afterClass() {
        // 销毁线程池
        _threadpool.destroy();
    }

    /**
     * 测试用例：注册监听器 <br/>
     * 前置条件：注册一个监听器 <br/>
     * 结果：监听器数量为1
     */
    @Test
    public void testAddListener() {
        _deletator.addListener(createEventListener());
        
        assertEquals(1, _deletator.getListenerCount());
    }

    /**
     * 测试用例：移除监听器 <br/>
     * 前置条件：已经有一个监听器，移除该注册器 <br/>
     * 结果：监听器数量为0
     */
    @Test
    public void testRemoveListener() {
        EventListener<DataObj> listener = createEventListener();
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
        EventListener<DataObj> mock1 = createEventListenerMock(1, "default");
        EventListener<DataObj> mock2 = createEventListenerMock(1, "default");
        
        _deletator.addListener(mock1);
        _deletator.addListener(mock2);
        _deletator.fire(new Event<DataObj>());
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        verify(mock1, mock2);
    }
    
    /**
     * 测试用例：通知所有注册的监听器 <br/>
     * 前置条件：注册一个监听器并分派事件给它；事件没有配置clone属性 <br/>
     * 结果：监听器都被调用1次，分派事件时事件被clone。
     */
    @Test
    public void testFire4EventDefaultClone() throws InterruptedException {
        EventListener<DataObj> listenerMock = createEventListenerMock(1, "default");
        _deletator.addListener(listenerMock);
        Event<DataObj> eventMock = createEventMock(1);
        _deletator.fire(eventMock);
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        verify(listenerMock);
        verify(eventMock);
    }
    
    /**
     * 测试用例：通知所有注册的监听器 <br/>
     * 前置条件：注册一个监听器并分派事件给它；事件设置clone=true <br/>
     * 结果：监听器都被调用1次，分派事件时事件被clone。
     */
    @Test
    public void testFire4EventSetClone() throws InterruptedException {
        _deletator.setNeedClone(true);
        EventListener<DataObj> listenerMock = createEventListenerMock(1, "default");
        _deletator.addListener(listenerMock);
        Event<DataObj> eventMock = createEventMock(1);
        _deletator.fire(eventMock);
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        verify(listenerMock);
        verify(eventMock);
    }
    
    /**
     * 测试用例：通知所有注册的监听器 <br/>
     * 前置条件：注册一个监听器并分派事件给它；事件设置clone=false <br/>
     * 结果：监听器都被调用1次，分派事件时事件没有执行clone。
     */
    @Test
    public void testFire4EventNotClone() throws InterruptedException {
        _deletator.setNeedClone(false);
        EventListener<DataObj> listenerMock = createEventListenerMock(1, "default");
        _deletator.addListener(listenerMock);
        Event<DataObj> eventMock = createEventMock(1);
        _deletator.fire(eventMock);
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        verify(listenerMock);
        boolean occursError = false;
        try {
            verify(eventMock);
        } catch (AssertionError e) {
            occursError = true;
            assertTrue(e.getMessage().endsWith("Event.clone(): expected: 1, actual: 0"));
        } // end of try-catch
        assertTrue("设置事件不执行clone操作，结果执行了1次clone", occursError);
    }
    
    /**
     * 测试用例：通知所有注册的监听器 <br/>
     * 前置条件：注册一个监听器并分派事件给它；监听器有指定线程池为"other" <br/>
     * 结果：监听器都被调用1次，在名称为"other"的线程池中执行。
     */
    @Test
    public void testFire4ThreadPool() throws InterruptedException {
        _deletator._threadPool = createThreadPoolMock("other", 1);
        
        EventListener<DataObj> listener = createEventListener();
        listener.setThreadPoolName("other");
        _deletator.addListener(listener);
        Event<DataObj> event = createEvent();
        _deletator.fire(event);
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        verify(_deletator._threadPool);
    }
    
    private Event<DataObj> createEvent() {
        return new Event<DataObj>("EventType", new DataObj());
    }
    
    private EventListener<DataObj> createEventListener() {
        return new AbstractEventListener<DataObj>() {
            @Override
            public void execute(Event<DataObj> event) {
            }
        };
    }
    
    @SuppressWarnings("unchecked")
    private Event<DataObj> createEventMock(int cloneCallTimes) {
        Event<DataObj> mock = createMock(Event.class);
        mock.clone();
        expectLastCall().andReturn( createMock(Event.class))
                .times(cloneCallTimes);
        replay(mock);
        
        return mock;
    }
    
    /**
     * 创建{@link EventListener}的Mock。
     * @param callTimes 事件被调用的次数
     * @return {@link EventListener}的Mock
     */
    @SuppressWarnings("unchecked")
    private EventListener<DataObj> createEventListenerMock(int callTimes, String threadPoolName) {
        EventListener<DataObj> mock = createMock(EventListener.class);
        expect(mock.getThreadPoolName())
            .andReturn(threadPoolName)
            .times(callTimes);
        mock.execute(anyObject(Event.class));
        expectLastCall().times(callTimes);
        replay(mock);
        
        return mock;
    }
    
    private ThreadPool createThreadPoolMock(String threadPoolName, int callTimes) {
        ThreadPool mock = createMock(ThreadPool.class);
        mock.submit( anyObject(Runnable.class), eq(threadPoolName));
        expectLastCall().andReturn(createMock(Future.class)).times(callTimes);
        replay(mock);
        
        return mock;
    }

}
