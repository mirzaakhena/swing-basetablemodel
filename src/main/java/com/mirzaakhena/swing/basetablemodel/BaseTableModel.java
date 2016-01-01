package com.mirzaakhena.swing.basetablemodel;

import java.lang.annotation.Annotation;
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

	private List<T> list;

	private String[] columnHeaders;

	@SuppressWarnings("unchecked")
	private Class<T> getClazz() {
		final ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		Class<T> clazz = (Class<T>) type.getActualTypeArguments()[0];
		return clazz;
	}

	/**
	 * You must overide this constructor to provide the table column name
	 * 
	 * @param columnHeader
	 */
	public BaseTableModel() {

		list = new ArrayList<>();

		List<Pair> listName = new ArrayList<>();
		Class<T> clazz = getClazz();

		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(TableColumn.class)) {
				Annotation annotation = field.getAnnotation(TableColumn.class);
				TableColumn tc = (TableColumn) annotation;
				listName.add(new Pair(tc.order(), !tc.header().isEmpty() ? tc.header() : field.getName()));
			}
		}

		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(TableColumn.class)) {
				Annotation annotation = method.getAnnotation(TableColumn.class);
				TableColumn tc = (TableColumn) annotation;
				listName.add(new Pair(tc.order(), !tc.header().isEmpty() ? tc.header() : method.getName()));
			}
		}

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
		return list.size();
	}

	/**
	 * If you want to show class in table
	 * 
	 * <pre>
	 * class Employee {
	 *   public String name;
	 *   public String address;
	 *   ...
	 * }
	 * </pre>
	 * 
	 * you should do
	 * 
	 * <pre>
	 * public Object getValueAt(int rowIndex, int columnIndex) {
	 * 	Employee x = getItem(rowIndex);
	 * 	switch (columnIndex) {
	 * 	case 0:
	 * 		return x.name;
	 * 	case 1:
	 * 		return x.address;
	 * 	}
	 * 	return null;
	 * }
	 * </pre>
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {

		Object obj = getItem(rowIndex);

		Field[] fields = obj.getClass().getFields();
		Method[] methods = obj.getClass().getMethods();

		try {
			List<Pair> listValue = new ArrayList<>();
			for (Field field : fields) {
				if (field.isAnnotationPresent(TableColumn.class)) {
					Annotation annotation = field.getAnnotation(TableColumn.class);
					TableColumn tc = (TableColumn) annotation;
					listValue.add(new Pair(tc.order(), field.get(obj)));
				}
			}
			for (Method method : methods) {
				if (method.isAnnotationPresent(TableColumn.class)) {
					Annotation annotation = method.getAnnotation(TableColumn.class);
					TableColumn tc = (TableColumn) annotation;
					listValue.add(new Pair(tc.order(), method.invoke(obj)));
				}
			}

			Collections.sort(listValue, (a, b) -> a.order - b.order);

			return listValue.get(columnIndex).value;

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
		return list.get(index);
	}

	public List<T> getAllItem() {
		return list;
	}

	public void setItem(List<T> x) {
		if (x == null) {
			list.clear();
		} else {
			list = x;
		}
		fireTableDataChanged();
	}

	public void insert(T x) {
		list.add(x);
		int n = list.size() - 1;
		fireTableRowsInserted(n, n);
	}

	public void insertOrder(T x, Comparable<T> comparator) {
		int size = list.size();
		boolean found = false;
		if (size != 0) {
			int n = size - 1;
			for (int i = 0; i < n && !found; i++) {
				if (comparator.compareTo(list.get(i)) < 0) {
					insert(x, i);
					found = true;
				}
			}
		}
		if (!found) {
			insert(x);
		}
	}

	public void insert(T x, int index) {
		list.add(index, x);
		int n = list.size() - 1;
		fireTableRowsInserted(n, n);
	}

	public void insert(List<T> list) {
		list.addAll(list);
		fireTableDataChanged();
	}

	public void update(T x, int index) {
		list.set(index, x);
		fireTableRowsUpdated(index, index);
	}

	public void update(int index) {
		fireTableRowsUpdated(index, index);
	}

	public void update(T x) {
		int index = list.indexOf(x);
		if (index != -1) {
			fireTableRowsUpdated(index, index);
		}
	}

	public void update() {
		fireTableDataChanged();
	}

	public void delete(int index) {
		list.remove(index);
		fireTableRowsDeleted(index, index);
	}

	public void delete(T x) {
		int index = list.indexOf(x);
		list.remove(x);
		fireTableRowsDeleted(index, index);

	}

	public void deleteAll() {
		list.clear();
		fireTableDataChanged();
	}

}
