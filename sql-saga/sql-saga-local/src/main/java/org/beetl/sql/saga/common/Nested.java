package org.beetl.sql.saga.common;

public class Nested {
	int count =0;
	public void  enter(){
		count++;
	}
	public void exit(){
		count--;
		if(count==-1){
			System.out.println("abc");
		}
	}

	public boolean isRoot(){
		return count==0;
	}
}
