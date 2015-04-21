package com.seanpenney.universityphotos;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Photo {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String name;
	
	@Persistent
	private String url;
	
	@Persistent
	private long date;
	
	public String getName() {
		return name != null ? name : "";
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url != null ? url : "";
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		this.date = date;
	}
}
