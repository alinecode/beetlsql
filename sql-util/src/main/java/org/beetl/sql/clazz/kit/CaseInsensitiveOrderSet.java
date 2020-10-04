package org.beetl.sql.clazz.kit;

import java.util.Collection;
import java.util.LinkedHashSet;

public class CaseInsensitiveOrderSet<T> extends LinkedHashSet<String> {

	private static final long serialVersionUID = 9178606903603606032L;

	private final LinkedHashSet<String> lowerSet = new LinkedHashSet<String>();
	
	String first = null;

	//Override
	public boolean contains(Object value) {
		
		String t = (String)value;
		return lowerSet.contains(t.toLowerCase());
	}

	//Override
	public boolean add(String value) {
		if(value==null){
			throw new NullPointerException(value);
		}
		boolean b = lowerSet.add(value.toLowerCase());
		if (b) {
			super.add(value);
		}
		
		if(first==null){
			first = value;
		}
		return b;

	}

	//Override
	public boolean addAll(Collection c) {
		Collection<String> t = (Collection<String>) c;
		for (String s : t) {
			lowerSet.add(s.toLowerCase());
		}
		return super.addAll(c);

	}

	public String getFirst() {
		return first;
	}
	
	
}
