package com.googlecode.okapi.base.annotation.comments;

import java.util.Date;
import java.util.Locale;

import com.googlecode.okapi.base.annotation.ResourceRange;

public final class Comment {

	private String from;
	private String id;
	private Comment inReplyTo;
	private Date timestamp;
	private String content;
	private Locale locale;

	// optional...
	private ResourceRange annotates;

	
	public Comment createReply(){
		return null;
	}
}
