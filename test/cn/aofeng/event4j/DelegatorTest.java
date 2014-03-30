 package cn.aofeng.event4j;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.aofeng.threadpool4j.ThreadPool;

/**
 * {@link Delegator}的单元测试用例 <br/>
 * 
 * @author <a href="mailto:aofengblog@163.com">聂勇</a>
 */
public class DelegatorTest {

    private Delegator deletator;
    
    @Before
    public void setUp() throws Exception {
        deletator = new Delegator();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 测试用例：注册监听器 <br/>
     * 前置条件：注册一个监听器 <br/>
     * 结果：监听器数量为1
     */
    @Test
    public void testAddListener() {
        deletator.addListener(createEventListenerMock(1));
        
        assertEquals(1, deletator.getListenerCount());
    }

    /**
     * 测试用例：移除监听器 <br/>
     * 前置条件：已经有一个监听器，移除该注册器 <br/>
     * 结果：监听器数量为0
     */
    @Test
    public void testRemoveListener() {
        EventListener<DataObj> listener = createEventListenerMock(1);
        deletator.listeners.add(listener);
        
        deletator.removeListener(listener);
        assertEquals(0, deletator.getListenerCount());
    }
    
    /**
     * 测试用例：获取监听器数量 <br/>
     * 前置条件：初始化{@link Delegator} <br/>
     * 结果：监听器数量为0
     */
    @Test
    public void testGetListenerCount() {
        assertEquals(0, deletator.getListenerCount());
    }
    
    /**
     * 测试用例：通知所有注册的监听器 <br/>
     * 前置条件：注册两个监听器并通知它们 <br/>
     * 结果：两个监听器都被调用1次
     */
    @Test
    public void testFire() throws InterruptedException {
        // 初始化线程池
        ThreadPool.getInstance().init();
        
        EventListener<DataObj> mock1 = createEventListenerMock(1);
        EventListener<DataObj> mock2 = createEventListenerMock(1);
        
        deletator.addListener(mock1);
        deletator.addListener(mock2);
        deletator.fire(new Event<DataObj>());
        
        Thread.sleep(1*1000); // 数据是异步处理，需等待一会儿才能校验数据
        
        EasyMock.verify(mock1, mock2);
    }

    @SuppressWarnings("unchecked")
    private EventListener<DataObj> createEventListenerMock(int callTimes) {
        EventListener<DataObj> mock = EasyMock.createMock(EventListener.class);
        mock.execute( EasyMock.anyObject(Event.class) );
        EasyMock.expectLastCall().times(callTimes);
        EasyMock.replay(mock);
        return mock;
    }

}
