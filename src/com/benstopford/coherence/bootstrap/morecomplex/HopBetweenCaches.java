package com.benstopford.coherence.bootstrap.morecomplex;

import com.benstopford.coherence.bootstrap.structures.framework.TestBase;
import com.tangosol.net.NamedCache;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.processor.AbstractProcessor;

/**
 * BTS, 20-Feb-2008
 */
public class HopBetweenCaches extends TestBase {

    public void testShouldBeAbleToAcceessDifferentCacheFromAnEntryProcessorIfTheyAreInDifferentCacheServices() {
        //*****NB - this is only safe to do if the two caches are in different cache services*****
        //*****NB - note that it will also work in the same cache service with multiple threads but there is the potential for deadlock*****

        NamedCache cache1 = getCache("config/basic-cache-on-different-cache-service.xml","cache1");
        NamedCache cache2 = getBasicCache("cache2");

        cache1.invoke("Key", new EntryProcessor());

        assertEquals("Entry Processors rock!!", cache2.get("Key2"));
    }

    private class EntryProcessor extends AbstractProcessor {
        public Object process(InvocableMap.Entry entry) {

            NamedCache cache2 = getBasicCache("cache2");
            cache2.put("Key2", "Entry Processors rock!!");
            return null;
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}