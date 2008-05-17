package net.sf.okapi.testapp;

import net.sf.okapi.common.filters.myformat.MyFormatInputFilter;
import net.sf.okapi.common.filters.myformat.MyFormatOutputFilter;
import net.sf.okapi.utilities.reverse.ReverseUtility;

public class Main {

    public static void main(String[] args) {
        
        // create input from myformat
        MyFormatInputFilter inputFilter = new MyFormatInputFilter();

        // create a utility that reverses all text
        ReverseUtility reverseUtility = new ReverseUtility();
        inputFilter.setOutput(reverseUtility);
        
        // output to myformat
        MyFormatOutputFilter outputFilter = new MyFormatOutputFilter(System.out);
        reverseUtility.setOutput(outputFilter);
        
        inputFilter.convert();
    }
}
