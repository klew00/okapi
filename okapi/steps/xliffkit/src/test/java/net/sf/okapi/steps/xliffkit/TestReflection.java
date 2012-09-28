/*===========================================================================
  Copyright (C) 2008-2011 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.steps.xliffkit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.lib.persistence.BeanMapper;
import net.sf.okapi.lib.persistence.IPersistenceBean;
import net.sf.okapi.lib.beans.v1.PropertyBean;
import net.sf.okapi.lib.beans.v1.TextUnitBean;
import net.sf.okapi.lib.beans.sessions.OkapiJsonSession;

import org.junit.Test;

@SuppressWarnings("unused")
public class TestReflection {

	private class TestClass {
		String data1;
		int data2;
		Character[] data3;
		PropertyBean bean1;
		PropertyBean bean2;
		List<PropertyBean> list;
		Map<Long, PropertyBean> map;
			
		public void initBean1() {
			bean1 = new PropertyBean();
			bean1.setRefId(111111);
		}
		
		public void initBeans() {
			list = new ArrayList<PropertyBean>();
			PropertyBean b = new PropertyBean();
			b.setRefId(1);
			list.add(b);
			
			b = new PropertyBean();
			b.setRefId(2);
			list.add(b);
		}
	}
	
	private class TestClass2 extends TestClass {
		PropertyBean bean3;
		
		public void initBean3() {
			bean3 = new PropertyBean();
			bean3.setRefId(33333);
		}
	}
	
	private class BaseClasses {
		Map<Object, Object> map;
		Collection<Object> coll;
		Map<IPersistenceBean<?>, Object> map2;
		Map<Object, IPersistenceBean<?>> map3;
		Map<IPersistenceBean<?>, IPersistenceBean<?>> map4;
	}
	
	@Test
	public void dummyTest() {
		// used to prevent maven failure when no unit tests are found
	}
		
	private void log(String str) {
		Logger localLogger = LoggerFactory.getLogger(getClass()); // loggers are cached
		localLogger.debug(str);
	}
	
	private void log(int value) {
		log(String.valueOf(value));
	}
	
	private void log(long value) {
		log(String.valueOf(value));
	}
	
	// DEBUG	@Test
	public void testMethods() {
//		TestClass testClass1 = new TestClass();
//		TestClass testClass2 = new TestClass2();
//		
		Method[] methods = null;
//		methods = TestClass2.class.getDeclaredMethods();
//		System.out.println(methods.length);
//		
//		methods = TestClass.class.getDeclaredMethods();
//		System.out.println(methods.length);
//		
//		methods = TestClass.class.getMethods();
//		System.out.println(methods.length);
		
		methods = TextUnitBean.class.getMethods();
		log(methods.length);
		
//		for (Method method : methods) {
//			String name = method.getName();
//			if (!name.startsWith("get")) continue;
//			
//			Class<?> retClass = method.getReturnType();
//						
//			if (IPersistenceBean.class.isAssignableFrom(retClass) ||
//					Collection.class.isAssignableFrom(retClass) ||
//					Map.class.isAssignableFrom(retClass) ||
//					Array.class.isAssignableFrom(retClass)) 
//				System.out.println(name);
//		}
		
//		System.out.println("-----------");
//		List<Method> getters = BeanMapper.getBeanGetters(TextUnitBean.class);
//		System.out.println(getters.size());
//		for (Method method : getters) {
//			System.out.println(method.getName());
//		}
//		
//		System.out.println("-----------");
//		List<Method> setters = BeanMapper.getBeanSetters(TextUnitBean.class);
//		System.out.println(setters.size());
//		for (Method method : setters) {
//			System.out.println(method.getName());
//		}
	}
	
	// DEBUG	@Test
	public void testFields() throws IllegalArgumentException, IllegalAccessException {
		TestClass testClass1 = new TestClass();
		TestClass testClass2 = new TestClass2();
		
		Field[] fields = null;
		fields = TestClass2.class.getDeclaredFields();
		log(fields.length);
		fields = TestClass2.class.getFields();
		log(fields.length);
		
//		Field[] fields = TestClass.class.getFields();
//		System.out.println(fields.length);
		
		fields = TestClass.class.getDeclaredFields();
		log(fields.length);
		
//		Class<?>[] classes = TestClass.class.getDeclaredClasses();
//		System.out.println(classes.length);
//		
//		classes = this.getClass().getDeclaredClasses();
//		System.out.println(classes.length);
		
		Field f1 = fields[3];
		log(f1.getName());
		
		PropertyBean b0 = (PropertyBean) f1.get(testClass1);
		log(b0.toString());
		testClass1.initBean1();
		
		b0 = (PropertyBean) f1.get(testClass1); // needs to be read again
		log(b0.getRefId());
		
		PropertyBean bean = new PropertyBean();
		bean.setRefId(1011103);
		f1.set(testClass1, bean);
		log(testClass1.bean1.getRefId());
		
		Field f2 = fields[5];		
		log(f2.getName());
		log(f2.getType().toString());
		TypeVariable<?>[] params = f2.getType().getTypeParameters();
		log("params: " + params.length);
		
		Field f6 = fields[6];		
		log(f6.getName());
		log(f6.getType().toString());
		TypeVariable<?>[] params6 = f6.getType().getTypeParameters();
		log("params: " + params6.length);
		log(params6[0].getName());
		log(params6[0].getClass().toString());
		log(params6[1].getName());
		//sun.reflect.generics.reflectiveObjects.TypeVariableImpl test = null;
				
		List<?> bb0 = (List<?>) f2.get(testClass1);
		log(bb0.toString());
		testClass1.initBeans();
		bb0 = (List<?>) f2.get(testClass1);
		log(bb0.toString());
	}

	// DEBUG	@Test
	public void testSpeed() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		TestClass testClass1 = new TestClass();
		Field[] fields = TestClass.class.getDeclaredFields();
		OkapiJsonSession session = new OkapiJsonSession();
		int loops = 0;
		long start = 0;
		
		//---------------------------
		loops = 100000000;

		testClass1.initBean1();
		
		PropertyBean b0 = testClass1.bean1;
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {			
			long refId = b0.getRefId();
		}
		log(loops + " regular: " + (System.currentTimeMillis() - start) + " milliseconds."); 
		
		Field f1 = fields[3];
		
		b0 = (PropertyBean) f1.get(testClass1);
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {			
			long refId = b0.getRefId();
		}
		log(loops + " reflection: " + (System.currentTimeMillis() - start) + " milliseconds.");
		
		//---------------------------
		loops = 1000000;
		
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {
			long refId = testClass1.bean1.getRefId();
		}
		log(loops + " regular: " + (System.currentTimeMillis() - start) + " milliseconds."); 
		
		f1 = fields[3];
				
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {
			b0 = (PropertyBean) f1.get(testClass1);
			long refId = b0.getRefId();
		}
		log(loops + " reflection: " + (System.currentTimeMillis() - start) + " milliseconds.");
		
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {
			ITextUnit tu = new TextUnit("tu1");
		}
		log("----" + loops + " TextUnit creation: " + (System.currentTimeMillis() - start) + " milliseconds.");
		
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {
			Property tu = new Property("name", "value");
		}
		log(loops + " Property creation: " + (System.currentTimeMillis() - start) + " milliseconds.");
		
		//---------------------------
		loops = 10000;
		
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {
			session.registerBean(ITextUnit.class, TextUnitBean.class);
		}
		log(loops + " registerBean(): " + (System.currentTimeMillis() - start) + " milliseconds.");
		
//		//---------------------------
//		loops = 1000000;
//		List<Method> getters = BeanMapper.getBeanGetters(TextUnitBean.class);
//		TextUnit tu = new TextUnit("tu1");
//		TextUnitBean tub = new TextUnitBean();
//		start = System.currentTimeMillis();		
//		for(int i = 0; i < loops; i++) {
//			for (Method method : getters) {
//				Object res = method.invoke(tub, (Object[]) null);
//			}
//		}
//		System.out.println("----" + loops + " TextUnit reflection: " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

	// DEBUG	@Test
	public void testClasses() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		Collection<Object> collection = new ArrayList<Object>();
		
		Map<Integer, IPersistenceBean<?>> map1 = new HashMap<Integer, IPersistenceBean<?>>(); 
		List<IPersistenceBean<?>> list1 = new ArrayList<IPersistenceBean<?>>();
		
		assertTrue(map1.getClass().isAssignableFrom(map.getClass()));
		assertTrue(map.getClass().isAssignableFrom(map1.getClass()));
		
		assertTrue(list1.getClass().isAssignableFrom(collection.getClass()));
		assertTrue(collection.getClass().isAssignableFrom(list1.getClass()));
		
		assertFalse(collection.getClass().isAssignableFrom(map1.getClass()));
		
		Field[] fields = BaseClasses.class.getDeclaredFields();
		Class<?> m1 = fields[0].getType();
		Class<?> c1 = fields[1].getType();
		
		assertTrue(m1.isAssignableFrom(map1.getClass()));
		assertFalse(map1.getClass().isAssignableFrom(m1));
		
		assertTrue(c1.isAssignableFrom(list1.getClass()));
		assertFalse(list1.getClass().isAssignableFrom(c1));

		Class<?> m2 = fields[2].getType();		
		Class<?> m3 = fields[3].getType();
		Class<?> m4 = fields[4].getType();
	}
}
