/*===========================================================================
  Copyright (C) 2009 by the Okapi Framework contributors
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import net.sf.okapi.steps.xliffkit.common.persistence.AddressBookProtos.AddressBook;
import net.sf.okapi.steps.xliffkit.common.persistence.AddressBookProtos.Person;


public class TestProtocolBuffers {

	private static final String fileName = "test2.txt";
	
	@Test
	public void test() {
		
	}
	
	// DEBUG @Test
	public void testWrite() throws IOException, URISyntaxException {
		
		AddressBook.Builder addressBook = AddressBook.newBuilder();

		Person.Builder person = Person.newBuilder();
		person.setId(1);
		person.setName("John Woo");
		person.setEmail("john.woo@example.com");
		Person.PhoneNumber.Builder phoneNumber =
	        Person.PhoneNumber.newBuilder().setNumber("555-5551");
		phoneNumber.setType(Person.PhoneType.MOBILE);
		person.addPhone(phoneNumber);
		phoneNumber =
	        Person.PhoneNumber.newBuilder().setNumber("555-5552");
		phoneNumber.setType(Person.PhoneType.WORK);
		person.addPhone(phoneNumber);
		
	    addressBook.addPerson(person.build());
	    
	    person = Person.newBuilder();
		person.setId(2);
		person.setName("Joan Woo");
		person.setEmail("joan.woo@example.com");
		phoneNumber =
	        Person.PhoneNumber.newBuilder().setNumber("555-5553");
		phoneNumber.setType(Person.PhoneType.MOBILE);
		person.addPhone(phoneNumber);
		phoneNumber =
	        Person.PhoneNumber.newBuilder().setNumber("555-5554");
		phoneNumber.setType(Person.PhoneType.HOME);
		person.addPhone(phoneNumber);
		
	    addressBook.addPerson(person.build());

	    FileOutputStream output = new FileOutputStream(new File(this.getClass().getResource(fileName).toURI()));
	    addressBook.build().writeTo(output);
	    output.close();
	}

	// DEBUG @Test
	public void testRead() throws FileNotFoundException, IOException, URISyntaxException {

	    AddressBook addressBook =
	      AddressBook.parseFrom(new FileInputStream(new File(this.getClass().getResource(fileName).toURI())));

	    for (Person person: addressBook.getPersonList()) {
	      System.out.println("Person ID: " + person.getId());
	      System.out.println("  Name: " + person.getName());
	      if (person.hasEmail()) {
	        System.out.println("  E-mail address: " + person.getEmail());
	      }

      for (Person.PhoneNumber phoneNumber : person.getPhoneList()) {
        switch (phoneNumber.getType()) {
          case MOBILE:
            System.out.print("  Mobile: ");
            break;
          case HOME:
            System.out.print("  Home: ");
            break;
          case WORK:
            System.out.print("  Work: ");
            break;
        }
        System.out.println(phoneNumber.getNumber());
      	}
	    }
	}
}
