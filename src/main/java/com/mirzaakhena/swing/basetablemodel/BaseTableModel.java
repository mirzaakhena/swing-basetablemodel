package com.mirzaakhena.swing.basetablemodel;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Mirza Akhena
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * @author mirzaakhena@gmail.com
 *
 */
@SuppressWarnings("serial")
public abstract class BaseTableModel<T> extends AbstractTableModel {

	private static class Pair {
		public Integer order;
		public Object value;

		public Pair(Integer order, Object value) {
			this.order = order;
			this.value = value;
		}

	}

	private List<T> listData;

	private List<AccessibleObject> listClassMember;

	private String[] columnHeaders;

	@SuppressWarnings("unchecked")
	private Class<T> getClazz() {
		final ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		Class<T> clazz = (Class<T>) type.getActualTypeArguments()[0];
		return clazz;
	}

	public BaseTableModel() {

		listData = new ArrayList<>();
		listClassMember = new ArrayList<>();

		List<Pair> listName = new ArrayList<>();
		Class<T> clazz = getClazz();

		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(TableColumn.class)) {
				listClassMember.add(field);
				TableColumn tc = field.getAnnotation(TableColumn.class);
				listName.add(new Pair(tc.order(), !tc.header().isEmpty() ? tc.header() : field.getName()));
			}
		}

		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(TableColumn.class)) {
				listClassMember.add(method);
				TableColumn tc = method.getAnnotation(TableColumn.class);
				listName.add(new Pair(tc.order(), !tc.header().isEmpty() ? tc.header() : method.getName()));
			}
		}

		Collections.sort(listClassMember, (a, b) -> a.getAnnotation(TableColumn.class).order() - b.getAnnotation(TableColumn.class).order());

		Collections.sort(listName, (a, b) -> a.order - b.order);

		columnHeaders = new String[listName.size()];
		for (int i = 0; i < listName.size(); i++) {
			columnHeaders[i] = listName.get(i).value.toString();
		}
	}

	/**
	 * you can overide this method to allow specific cell to be editable
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public String getColumnName(int column) {
		return columnHeaders[column];
	}

	@Override
	public int getRowCount() {
		return listData.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		Object obj = getItem(rowIndex);

		AccessibleObject ao = listClassMember.get(columnIndex);
		try {
			
			if (ao instanceof Field) {
				return ((Field) ao).get(obj);
				
			} else if (ao instanceof Method) {
				return ((Method) ao).invoke(obj);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";

	}

	@Override
	public int getColumnCount() {
		return columnHeaders.length;
	}

	/**
	 * get Object by index
	 * 
	 * @param index
	 * @return
	 */
	public T getItem(int index) {
		return listData.get(index);
	}

	public List<T> getAllItem() {
		return listData;
	}

	public void setItem(List<T> x) {
		if (x == null) {
			listData.clear();
		} else {
			listData = x;
		}
		fireTableDataChanged();
	}

	public void add(T x) {
		listData.add(x);
		int n = listData.size() - 1;
		fireTableRowsInserted(n, n);
	}

	public void insertOrder(T x, Comparable<T> comparator) {
		int size = listData.size();
		boolean found = false;
		if (size != 0) {
			int n = size - 1;
			for (int i = 0; i < n && !found; i++) {
				if (comparator.compareTo(listData.get(i)) < 0) {
					insert(x, i);
					found = true;
				}
			}
		}
		if (!found) {
			add(x);
		}
	}

	public void insert(T x, int index) {
		listData.add(index, x);
		int n = listData.size() - 1;
		fireTableRowsInserted(n, n);
	}

	public void add(List<T> list) {
		list.addAll(list);
		fireTableDataChanged();
	}

	public void update(T x, int index) {
		listData.set(index, x);
		fireTableRowsUpdated(index, index);
	}

	public void update(int index) {
		fireTableRowsUpdated(index, index);
	}

	public void update(T x) {
		int index = listData.indexOf(x);
		if (index != -1) {
			fireTableRowsUpdated(index, index);
		}
	}

	public void update() {
		fireTableDataChanged();
	}

	public void delete(int index) {
		listData.remove(index);
		fireTableRowsDeleted(index, index);
	}

	public void delete(T x) {
		int index = listData.indexOf(x);
		listData.remove(x);
		fireTableRowsDeleted(index, index);

	}

	public void deleteAll() {
		listData.clear();
		fireTableDataChanged();
	}

}
