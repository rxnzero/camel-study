package com.dhlee.camel.tramsform;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;

//@XmlRootElement(name="user")
@FixedLengthRecord(ignoreTrailingChars = true)
public class FixedFormatUser {
	
	// "00001이동훈    donghoon^lee"
	@DataField(pos = 1, length =5)
	private String id;
	
	@DataField(pos = 2, length = 10,  trim = true)
	private String name;
	
	@DataField(pos = 3, delimiter = "^", trim = true)
    private String firstName;
 
    @DataField(pos = 4, delimiter = "^", trim = true)
    private String lastName;
    
    
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	@Override
	public String toString() {
		return "FixedFormatUser [id=" + id + ", name=" + name + ", firstName=" + firstName + ", lastName=" + lastName
				+ "]";
	} 
    
    
}
