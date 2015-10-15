package com.brotherlogic.simplestore;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class ConverterTest {

  @Test
  public void GetPropertiesTest() throws IllegalAccessException {
    TestObjectConvert obj = new TestObjectConvert();
    obj.testString = "TestingString";
    obj.testNumber = new Integer(45);

    Map<String, Object> props = new Converter().getProperties(obj);

    Assert.assertNotNull(props);
    Assert.assertEquals("TestingString", props.get("teststring"));
    Assert.assertEquals(new Integer(45), props.get("testnumber"));
  }

  @Test
  public void GetPropertiesTestPrivate() throws IllegalAccessException {
    TestObject obj = new TestObject();
    obj.setTestString("TestingString");
    obj.testInteger = new Integer(45);

    Map<String, Object> props = new Converter().getProperties(obj);

    Assert.assertNotNull(props);
    Assert.assertEquals("TestingString", props.get("teststring"));
    Assert.assertEquals(new Integer(45), props.get("testinteger"));
  }

  @Test
  public void GetNullPropertiesTest() {
    Map<String, Class<?>> props = new Converter().getClasses(TestObjectConvert.class);

    Assert.assertNotNull(props);
    Assert.assertEquals(String.class, props.get("teststring"));
    Assert.assertEquals(Integer.class, props.get("testnumber"));
  }
}
