package st.infos.elementalcube.breakbaloon.theme.editor;

import java.util.ArrayList;

public class DoubleArrayList<E1, E2> {
	private ArrayList<E1> list1;
	private ArrayList<E2> list2;
	private int size;
	
	public DoubleArrayList() {
		list1 = new ArrayList<E1>();
		list2 = new ArrayList<E2>();
		size = 0;
	}
	
	public int add(E1 o1, E2 o2) {
		list1.add(o1);
		list2.add(o2);
		size++;
		return list1.size() - 1;
	}
	
	public ArrayList<E1> getList1() {
		return list1;
	}
	
	public ArrayList<E2> getList2() {
		return list2;
	}
	
	public E1 getObjectOf1(int id) {
		return list1.get(id);
	}
	
	public E2 getObjectOf2(int id) {
		return list2.get(id);
	}
	
	public void remove(int id) {
		list1.remove(id);
		list2.remove(id);
		size--;
	}
	
	public void set(int id, E1 o1, E2 o2) {
		list1.set(id, o1);
		list2.set(id, o2);
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		if(size == 0) {
			return true;
		}
		return false;
	}
}
