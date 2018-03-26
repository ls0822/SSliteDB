package org.sslite.plugin.db.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

public class DBValues {
	private List<String> orderColums;	
	private List<ContentValues> values;
	
	public DBValues () {
		orderColums = new ArrayList<String>();
		values = new ArrayList<ContentValues>();
	}
	
	public void addColums(String columName) {
		if (orderColums != null) {
			orderColums.add(columName);
		}
	}
	
	public void addValue(ContentValues value ) {
		if (values != null) {
			values.add(value);
		}
	}
	
	
	
	/**
	 * @return the orderColums
	 */
	public List<String> getOrderColums() {
		return orderColums;
	}

	/**
	 * @return the values
	 */
	public List<ContentValues> getValues() {
		return values;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (orderColums != null && orderColums.size() > 0) {
			orderColums.clear();
		}
		if (values != null && values.size() > 0) {
			values.clear();
		}
	}
}
