//package com.tekclover.wms.api.outbound.transaction.service;
//
//import java.lang.reflect.InvocationTargetException;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//import com.tekclover.wms.api.outbound.transaction.model.stock.AddInventoryStock;
//import com.tekclover.wms.api.outbound.transaction.model.stock.InventoryStock;
//import com.tekclover.wms.api.outbound.transaction.model.stock.SearchInventoryStock;
//import com.tekclover.wms.api.outbound.transaction.repository.InventoryStockRepository;
//import com.tekclover.wms.api.outbound.transaction.repository.specification.InventoryStockSpecification;
//import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.expression.ParseException;
//import org.springframework.stereotype.Service;
//
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class InventoryStockService extends BaseService {
//
//	@Autowired
//	private InventoryStockRepository inventoryRepository;
//
//	/**
//	 * getInventoryStocks
//	 * @return
//	 */
//	public List<InventoryStock> getInventoryStocks () {
//		List<InventoryStock> inventoryList =  inventoryRepository.findAll();
//		inventoryList = inventoryList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
//		return inventoryList;
//	}
//
//	/**
//	 *
//	 * @param searchInventoryStock
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<InventoryStock> findInventoryStock(SearchInventoryStock searchInventoryStock)
//			throws ParseException {
//		InventoryStockSpecification spec = new InventoryStockSpecification(searchInventoryStock);
//		List<InventoryStock> results = inventoryRepository.findAll(spec);
//		results.stream().forEach(n -> {
//			if (n.getInventoryQuantity() == null) { n.setInventoryQuantity(0D);}
//			if (n.getAllocatedQuantity() == null) { n.setAllocatedQuantity(0D);}
//			n.setReferenceField4(n.getInventoryQuantity() + n.getAllocatedQuantity());
//		});
//		return results;
//	}
//
//	/**
//	 * createInventoryStock
//	 * @param newInventoryStock
//	 * @param loginUserID
//	 * @return
//	 * @throws IllegalAccessException
//	 * @throws InvocationTargetException
//	 */
//	public InventoryStock createInventoryStock (AddInventoryStock newInventoryStock, String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//		InventoryStock dbInventoryStock = new InventoryStock();
//		log.info("newInventoryStock : " + newInventoryStock);
//		BeanUtils.copyProperties(newInventoryStock, dbInventoryStock, CommonUtils.getNullPropertyNames(newInventoryStock));
//		dbInventoryStock.setDeletionIndicator(0L);
//		dbInventoryStock.setCreatedBy(loginUserID);
//		dbInventoryStock.setCreatedOn(new Date());
//		return inventoryRepository.save(dbInventoryStock);
//	}
//
//}
