package com.squid.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Box")
public class BoxData {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private long width;
	private long heigth;
	private long length;
	
	public BoxData() {}
	
	public BoxData(int i, int j, int k, int l) {
		id = i;
		width = j;
		heigth = k;
		length = l;
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public long getWidth() {
		return width;
	}
	public void setWidth(long width) {
		this.width = width;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}

	public long getHeigth() {
		return heigth;
	}

	public void setHeigth(long heigth) {
		this.heigth = heigth;
	}
}
