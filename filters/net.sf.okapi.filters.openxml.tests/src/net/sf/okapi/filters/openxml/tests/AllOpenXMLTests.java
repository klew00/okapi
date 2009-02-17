package net.sf.okapi.filters.openxml.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

@RunWith(Suite.class)
@SuiteClasses({OpenXMLSnippetsTest.class})
public class AllOpenXMLTests {

  public static Test suite() {
    return new JUnit4TestAdapter(AllOpenXMLTests.class);
  }

}
/*
@SuiteClasses({OpenXMLSnippetsTest.class, OpenXMLFullFileTest.class, OpenXMLConfigurationTest.class})
public class AllOpenXMLTests {

  public static Test suite() {
    return new JUnit4TestAdapter(AllOpenXMLTests.class);
  }

}
*/