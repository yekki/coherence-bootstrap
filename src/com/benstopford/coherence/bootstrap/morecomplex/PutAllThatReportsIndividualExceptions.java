package com.benstopford.coherence.bootstrap.morecomplex;

import com.benstopford.coherence.bootstrap.structures.framework.TestBase;
import com.benstopford.coherence.bootstrap.structures.PutAllWithErrorReporting;
import com.tangosol.net.*;

import java.util.Map;
import java.util.TreeMap;

public class PutAllThatReportsIndividualExceptions extends TestBase {

    public void testShouldPut() throws InterruptedException {
        String cacheName = "regular-cache";
        String configPath = "config/basic-invocation-service-pof-1.xml";
        NamedCache cache = getCache(configPath, cacheName);

        InvocationService service = (InvocationService) new DefaultConfigurableCacheFactory(configPath).ensureService("MyInvocationService1");

        TreeMap hashMap = new TreeMap();
        hashMap.put(1, 2);
        hashMap.put(2, 3);
        hashMap.put(3, 4);
        hashMap.put(4, 5);

        PutAllWithErrorReporting invoker = new PutAllWithErrorReporting(
                service,
                (DistributedCacheService) cache.getCacheService(),
                cache.getCacheName(),
                configPath
        );
        invoker.putAll(hashMap);

        Thread.sleep(1000);
        assertEquals(4, cache.size());
        assertEquals(2, cache.get(1));
        assertEquals(3, cache.get(2));
        assertEquals(4, cache.get(3));
        assertEquals(5, cache.get(4));
    }

    public void testShouldReportFailures() {

        String cacheName = "break-me";
        String configPath = "config/basic-invocation-service-pof-1.xml";
        NamedCache cache = new DefaultConfigurableCacheFactory(configPath).ensureCache(cacheName, getClass().getClassLoader());

        InvocationService service = (InvocationService) new DefaultConfigurableCacheFactory(configPath).ensureService("MyInvocationService1");
        Map entries = new TreeMap();
        entries.put(1, 2);
        entries.put(2, 3);
        entries.put(3, 4);
        entries.put(4, 5);

        PutAllWithErrorReporting invoker = new PutAllWithErrorReporting(
                service,
                (DistributedCacheService) cache.getCacheService(),
                cache.getCacheName(),
                configPath
        );

        Map<Object, Throwable> keyToThrowableMap = invoker.putAll(entries);

        System.out.println("test ran and return result is " + keyToThrowableMap);
        System.out.println("test ran and return result size is " + keyToThrowableMap.size());

        assertEquals(4, keyToThrowableMap.size());
        assertTrue(keyToThrowableMap.get(1).getMessage().contains("Forced Error"));
        assertTrue(keyToThrowableMap.get(2).getMessage().contains("Forced Error"));
        assertTrue(keyToThrowableMap.get(3).getMessage().contains("Forced Error"));
        assertTrue(keyToThrowableMap.get(4).getMessage().contains("Forced Error"));
    }


    protected void setUp() throws Exception {
        super.setUp();
        setDefaultProperties();
        startOutOfProcess("config/basic-invocation-service-pof-1.xml");
        startOutOfProcess("config/basic-invocation-service-pof-1.xml");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}