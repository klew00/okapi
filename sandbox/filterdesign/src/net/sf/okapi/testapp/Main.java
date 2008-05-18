package net.sf.okapi.testapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.okapi.common.filters.myformat.MyFormatInputFilter;
import net.sf.okapi.common.filters.myformat.MyFormatOutputFilter;
import net.sf.okapi.common.filters.myformat2.MyFormat2InputFilter;
import net.sf.okapi.common.filters.myformat2.MyFormat2OutputFilter;
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

        //=== Test with file and custom resources
        try {
        	String inputPath = "test.txt";
        	InputStream input = new BufferedInputStream(new FileInputStream(inputPath));
        	MyFormat2InputFilter inputFilter2 = new MyFormat2InputFilter(input);
        	inputFilter2.setOutput(reverseUtility);
			OutputStream output = new BufferedOutputStream(new FileOutputStream(inputPath+".out"));
            MyFormat2OutputFilter outputFilter2 = new MyFormat2OutputFilter(output);
            reverseUtility.setOutput(outputFilter2);
        	inputFilter2.convert();
        }
        catch ( Exception e ) {
        	System.err.println(e.getLocalizedMessage());
        }
    }
}
