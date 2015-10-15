package com.brotherlogic.simplestore.integration;

import com.brotherlogic.simplestore.SimpleStorage;
import com.brotherlogic.simplestore.TestObject;
import com.brotherlogic.simplestore.postgres.PgMapper;
import com.brotherlogic.simplestore.postgres.PgRunner;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FunctionalityTestIT {

  SimpleStorage storage;

  @Before
  public void tearup() throws Exception {
    storage = new SimpleStorage(new PgMapper(new PgRunner(System.getenv("DATABASE_URL"))));
  }

  @Test
  public void storeRetrieveTest() {
    TestObject tester = new TestObject();
    tester.setTestString("Magic");
    tester.testInteger = 45;

    storage.store(tester);
    Map<String, Object> mapper = new TreeMap<String, Object>();
    mapper.put("testString", "Magic");
    TestObject testerRetr = (TestObject) storage.retrieve(TestObject.class, mapper);
    Assert.assertNotNull(testerRetr);
    Assert.assertEquals("Magic", testerRetr.getTestString());
    Assert.assertEquals(new Integer(45), testerRetr.testInteger);
  }
}
