package com.brotherlogic.simplestore;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TableTest {

  @Test
  public void testConstructor() {
    Table t = new Table("TestName", Mockito.mock(Mapper.class), Mapper.class);
    Assert.assertNotNull(t);
  }

  @Test
  public void testStore() {
    Mapper mMapper = Mockito.mock(Mapper.class);
    Set<String> props = new TreeSet<String>();
    props.add("testString");
    props.add("testInteger");
    Mockito.when(mMapper.getProperties("TestTable")).thenReturn(props);
    Table table = new Table("TestTable", mMapper, TestObject.class);

    Map<String, Object> obj = new TreeMap<String, Object>();
    obj.put("testString", "Test1");
    obj.put("testInteger", new Integer(45));

    Mockito.when(mMapper.store("TestTable", obj)).thenReturn(true);

    boolean result = table.store(obj);
    Assert.assertTrue(result);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testRetrieve() {
    Mapper mMapper = Mockito.mock(Mapper.class);
    TestObject obj = new TestObject();
    obj.setTestString("TestString");
    obj.testInteger = new Integer(45);

    Map<String, Object> objProps = new TreeMap<String, Object>();
    objProps.put("testString", "Test1");
    objProps.put("testInteger", new Integer(45));

    Mockito.when(mMapper.retrieve(Mockito.eq(TestObject.class), Mockito.eq("TestTable"),
        Mockito.eq(objProps), Mockito.any(Map.class))).thenReturn(obj);
    Table table = new Table("TestTable", mMapper, TestObject.class);
    TestObject tObj = (TestObject) table.retrieve(TestObject.class, objProps);
    Assert.assertEquals(obj, tObj);
  }

  @Test
  public void testAdjust() {
    Mapper mMapper = Mockito.mock(Mapper.class);
    Set<String> props = new TreeSet<String>();
    props.add("testString");
    Mockito.when(mMapper.getProperties("TestTable")).thenReturn(props);

    Map<String, Object> objProps = new TreeMap<String, Object>();
    objProps.put("testString", "Test1");
    objProps.put("testInteger", new Integer(45));

    Table table = new Table("TestTable", mMapper, TestObject.class);
    table.adjust(objProps);

    Mockito.verify(mMapper).addColumn("TestTable", "testInteger", Integer.class);
  }
}
