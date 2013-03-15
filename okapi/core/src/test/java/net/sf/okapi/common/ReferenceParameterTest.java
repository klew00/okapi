package net.sf.okapi.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Test;
import static org.junit.Assert.*;

public class ReferenceParameterTest {

	@Test
	public void testDetectionAndAccess ()
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
	{
		DummyParameters params = new DummyParameters();
		Method[] methods = params.getClass().getMethods();
		for ( Method m : methods ) {
			if ( Modifier.isPublic(m.getModifiers() ) && m.isAnnotationPresent(ReferenceParameter.class)) {
				String data = (String)m.invoke(params);
				assertEquals("reference1", data);
				// Test changing the value
				String getMethodName = m.getName();
				String setMethodName = "set"+getMethodName.substring(3);
				Method setMethod = params.getClass().getMethod(setMethodName, String.class);
				setMethod.invoke(params, "NewValue");
				data = (String)m.invoke(params);
				assertEquals("NewValue", data);
			}
		}
	}
	
}
