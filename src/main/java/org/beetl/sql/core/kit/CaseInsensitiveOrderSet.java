package org.beetl.sql.core.kit;

import java.util.Collection;
import java.util.LinkedHashSet;

public class CaseInsensitiveOrderSet extends LinkedHashSet<String> {

	private static final long serialVersionUID = 9178606903603606032L;

	private final LinkedHashSet<String> lowerSet = new LinkedHashSet<String>();

	@Override
	public boolean contains(Object value) {
		String t = (String)value;
		return lowerSet.contains(t.toLowerCase());
	}

	@Override
	public boolean add(String value) {
		boolean b = lowerSet.add(value.toLowerCase());
		if (!b) {
			super.add(value);
		}
		return b;

	}

	@Override
	public boolean addAll(Collection c) {
		Collection<String> t = (Collection<String>) c;
		for (String s : t) {
			lowerSet.add(s.toLowerCase());
		}
		return super.addAll(c);

	}

}
