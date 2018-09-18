package com.iis.fm.domain;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.iis.fm.utils.ExtField;



public class Friends extends BaseDomain {

	public static int objectTypeId = 21;
	public static String objectTypeStr = "Friends";
	
	@ExtField(primaryKey = true)
	private int id;
	private String emails;
	
	public Friends() {
	}

	public Friends(int id,  String Emails) {
		this.id = id;
		this.emails = Emails;
	}

	

	public int getEmailId() {
		return this.id;
	}

	public int setEmailId(int id) {
		 this.id=id;
	}
	
	public String getEmails() {
		return this.emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	
}
