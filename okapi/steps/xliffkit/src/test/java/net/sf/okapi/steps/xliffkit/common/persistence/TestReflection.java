/*===========================================================================
  Copyright (C) 2008-2010 by the Okapi Framework contributors
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

package net.sf.okapi.steps.xliffkit.common.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextUnit;
import net.sf.okapi.steps.xliffkit.common.persistence.beans.okapi.PropertyBean;

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
			
		private void initBean1() {
			bean1 = new PropertyBean();
			bean1.setRefId(111111);
		}
		
		private void initBeans() {
			list = new ArrayList<PropertyBean>();
			PropertyBean b = new PropertyBean();
			b.setRefId(1);
			list.add(b);
			
			b = new PropertyBean();
			b.setRefId(2);
			list.add(b);
		}
	}
	
	private class BaseClasses {
		Map<Object, Object> map;
		Collection<Object> coll;
		Map<IPersistenceBean, Object> map2;
		Map<Object, IPersistenceBean> map3;
		Map<IPersistenceBean, IPersistenceBean> map4;
	}
		
	// DEBUG
	@Test
	public void testFields() throws IllegalArgumentException, IllegalAccessException {
		TestClass testClass1 = new TestClass();
		Field[] fields = null;
		
//		Field[] fields = TestClass.class.getFields();
//		System.out.println(fields.length);
		
		fields = TestClass.class.getDeclaredFields();
		System.out.println(fields.length);
		
//		Class<?>[] classes = TestClass.class.getDeclaredClasses();
//		System.out.println(classes.length);
//		
//		classes = this.getClass().getDeclaredClasses();
//		System.out.println(classes.length);
		
		Field f1 = fields[3];
		System.out.println(f1.getName());
		
		PropertyBean b0 = (PropertyBean) f1.get(testClass1);
		System.out.println(b0);
		testClass1.initBean1();
		
		b0 = (PropertyBean) f1.get(testClass1); // needs to be read again
		System.out.println(b0.getRefId());
		
		PropertyBean bean = new PropertyBean();
		bean.setRefId(1011103);
		f1.set(testClass1, bean);
		System.out.println(testClass1.bean1.getRefId());
		
		Field f2 = fields[5];		
		System.out.println(f2.getName());
		System.out.println(f2.getType());
		TypeVariable<?>[] params = f2.getType().getTypeParameters();
		System.out.println("params: " + params.length);
		
		Field f6 = fields[6];		
		System.out.println(f6.getName());
		System.out.println(f6.getType());
		TypeVariable<?>[] params6 = f6.getType().getTypeParameters();
		System.out.println("params: " + params6.length);
		System.out.println(params6[0].getName());
		System.out.println(params6[0].getClass());
		System.out.println(params6[1].getName());
		//sun.reflect.generics.reflectiveObjects.TypeVariableImpl test = null;
				
		List<?> bb0 = (List<?>) f2.get(testClass1);
		System.out.println(bb0);
		testClass1.initBeans();
		bb0 = (List<?>) f2.get(testClass1);
		System.out.println(bb0);
	}

	// DEBUG
	@Test
	public void testSpeed() throws IllegalArgumentException, IllegalAccessException {
		TestClass testClass1 = new TestClass();
		Field[] fields = TestClass.class.getDeclaredFields();
		
		//---------------------------
		int loops = 100000000;

		testClass1.initBean1();
		
		PropertyBean b0 = testClass1.bean1;
		long start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {			
			long refId = b0.getRefId();
		}
		System.out.println(loops + " regular: " + (System.currentTimeMillis() - start) + " milliseconds."); 
		
		Field f1 = fields[3];
		
		b0 = (PropertyBean) f1.get(testClass1);
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {			
			long refId = b0.getRefId();
		}
		System.out.println(loops + " reflection: " + (System.currentTimeMillis() - start) + " milliseconds.");

		//---------------------------
		loops = 1000000;
		
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {
			long refId = testClass1.bean1.getRefId();
		}
		System.out.println(loops + " regular: " + (System.currentTimeMillis() - start) + " milliseconds."); 
		
		f1 = fields[3];
				
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {
			b0 = (PropertyBean) f1.get(testClass1);
			long refId = b0.getRefId();
		}
		System.out.println(loops + " reflection: " + (System.currentTimeMillis() - start) + " milliseconds.");
		
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {
			TextUnit tu = new TextUnit("tu1");
		}
		System.out.println(loops + " TextUnit creation: " + (System.currentTimeMillis() - start) + " milliseconds.");
		
		start = System.currentTimeMillis();
		for(int i = 0; i < loops; i++) {
			Property tu = new Property("name", "value");
		}
		System.out.println(loops + " Property creation: " + (System.currentTimeMillis() - start) + " milliseconds.");
	}

	// DEBUG
	@Test
	public void testClasses() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		Collection<Object> collection = new ArrayList<Object>();
		
		Map<Integer, IPersistenceBean> map1 = new HashMap<Integer, IPersistenceBean>(); 
		List<IPersistenceBean> list1 = new ArrayList<IPersistenceBean>();
		
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
