package com.tekclover.wms.api.transaction.util;

import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

	public static enum DashboardTypes {
		AGREEMENT, AGREEMENT_TOTAL, AGREEMENT_SENT, AGREEMENT_RECEIVED, AGREEMENT_RESENT
	}

	/**
	 * getNullPropertyNames
	 * 
	 * @param source
	 * @return
	 */
	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	/**
	 * copy
	 * 
	 * @param fromBean
	 * @return
	 */
	public static Object copy(Object fromBean) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XMLEncoder out = new XMLEncoder(bos);
		out.writeObject(fromBean);
		out.close();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		XMLDecoder in = new XMLDecoder(bis);
		Object toBean = in.readObject();
		in.close();
		return toBean;
	}
	
	private void sortList () {
		Inventory inv1 = new Inventory();
		inv1.setQty(301);
		
		Inventory inv2 = new Inventory();
		inv2.setQty(202);
		
		Inventory inv3 = new Inventory();
		inv3.setQty(3022);
		
		Inventory inv4 = new Inventory();
		inv4.setQty(202);
		
		List<Inventory> invList1 = new ArrayList<>();
		invList1.add(inv1);
		invList1.add(inv2);
		invList1.add(inv3);
		invList1.add(inv4);
		
		List<Long> l = invList1.stream().map(Inventory::getQty).collect(Collectors.toList());
		long r = l.stream().filter(a->a == 20 || a == 30).count();
		System.out.println(r == l.size());
		
		List<Inventory> l1 = invList1.stream().filter(a -> a.getQty() != 202).collect(Collectors.toList());
		System.out.println(l1);
	}
	
//	public static <T> boolean areAllUnique(List<T> list){
//		return list.stream().distinct().collect(Collectors.toList());
//	}
	
	public static void main(String[] args) {
//		LocalDate localDate = LocalDate.now();
//		System.out.println(localDate);
//		LocalDateTime datetime = LocalDateTime.now();
		Date date = new Date();
		LocalDateTime datetime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String currentDate = datetime.format(newPattern);
		
		DateTimeFormatter newTimePattern = DateTimeFormatter.ofPattern("HH:mm:ss");
		String currentTime = datetime.format(newTimePattern);
		
		System.out.println(currentDate);
		System.out.println(currentTime);
		
//		CommonUtils o = new CommonUtils();
//		o.sortList();
		
		List<String> list = new ArrayList<> ();
		list.add("A1");
		list.add("A2");
		list.add("A3");
		list.add("A4");
		list.add("A5");
		list.add("A6");
		List[] list1 = splitList (list);
		System.out.println(list1[0]);
		System.out.println(list1[1]);
	}
	
	private static<T> List[] splitList (List<String> list) {
	    // get the size of the list
	    int size = list.size();
	 
	    // construct a new list from the returned view by `List.subList()` method
	    List<String> first = new ArrayList<>(list.subList(0, (size + 1)/2));
	    List<String> second = new ArrayList<>(list.subList((size + 1)/2, size));
	 
	    // return an array of lists to accommodate both lists
	    return new List[] {first, second};
	}
	
	@Data
	class Inventory {
		private long qty;
	}
}
