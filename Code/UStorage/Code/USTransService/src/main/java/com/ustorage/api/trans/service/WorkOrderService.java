package com.ustorage.api.trans.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;


import com.ustorage.api.trans.model.agreement.GAgreement;
import com.ustorage.api.trans.model.itemservice.*;
import com.ustorage.api.trans.model.storenumber.StoreNumber;
import com.ustorage.api.trans.repository.ItemServiceRepository;
import com.ustorage.api.trans.repository.ReportRepository;
import com.ustorage.api.trans.repository.Specification.WorkOrderSpecification;
import com.ustorage.api.trans.repository.WoProcessedByRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.model.workorder.*;

import com.ustorage.api.trans.repository.WorkOrderRepository;
import com.ustorage.api.trans.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkOrderService {
	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private WorkOrderRepository workOrderRepository;
	@Autowired
	private ItemServiceRepository itemServiceRepository;
	@Autowired
	private ItemServiceService itemServiceService;
	@Autowired
	private WoProcessedByRepository woProcessedByRepository;
	@Autowired
	private WoProcessedByService woProcessedByService;
	
	public List<WorkOrder> getWorkOrder () {
		List<WorkOrder> workOrderList =  workOrderRepository.findAll();
		workOrderList = workOrderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return workOrderList;
	}
	
	/**
	 * getWorkOrder
	 * @param workOrderId
	 * @return
	 */
	public GWorkOrder getWorkOrder (String workOrderId) {
		Optional<WorkOrder> workOrder = workOrderRepository.findByWorkOrderIdAndDeletionIndicator(workOrderId, 0L);
		if (workOrder.isEmpty()) {
			return null;
		}
		GWorkOrder dbWorkOrder = new GWorkOrder();
		BeanUtils.copyProperties(workOrder.get(),dbWorkOrder,CommonUtils.getNullPropertyNames(workOrder));
		dbWorkOrder.setWorkOrderProcessedBy(workOrderRepository.getProcessedBy(dbWorkOrder.getWorkOrderId()));
		dbWorkOrder.setWoProcessedBy(new ArrayList<>());
		for(WoProcessedBy dbWoProcessedBy : workOrder.get().getWoProcessedBy()){
			dbWorkOrder.getWoProcessedBy().add(dbWoProcessedBy.getProcessedBy());
		}
		dbWorkOrder.setItemServices(new ArrayList<>());
		for(ItemService newItemService : workOrder.get().getItemServices()){
			dbWorkOrder.getItemServices().add(newItemService);
		}
		return dbWorkOrder;
	}

	public WorkOrder getWorkOrdr (String workOrderId) {
		Optional<WorkOrder> workOrder = workOrderRepository.findByWorkOrderIdAndDeletionIndicator(workOrderId, 0L);
		if (workOrder.isEmpty()) {
			return null;
		}
		return workOrder.get();
	}
	/**
	 * createWorkOrder
	 * @param newWorkOrder
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkOrder createWorkOrder (AddWorkOrder newWorkOrder, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {

		WorkOrder dbWorkOrder = new WorkOrder();
		BeanUtils.copyProperties(newWorkOrder, dbWorkOrder, CommonUtils.getNullPropertyNames(newWorkOrder));
		dbWorkOrder.setDeletionIndicator(0L);
		dbWorkOrder.setCreatedBy(loginUserId);
		dbWorkOrder.setUpdatedBy(loginUserId);
		dbWorkOrder.setCreatedOn(new Date());
		dbWorkOrder.setUpdatedOn(new Date());

		WorkOrder savedWorkOrder = workOrderRepository.save(dbWorkOrder);

			savedWorkOrder.setItemServices(new HashSet<>());
			if(newWorkOrder.getItemServices()!=null){
				for (ItemService newItemService : newWorkOrder.getItemServices()) {
					ItemService dbItemService = new ItemService();
					BeanUtils.copyProperties(newItemService, dbItemService, CommonUtils.getNullPropertyNames(newItemService));
					dbItemService.setDeletionIndicator(0L);
					dbItemService.setCreatedBy(loginUserId);
					dbItemService.setUpdatedBy(loginUserId);
					dbItemService.setCreatedOn(new Date());
					dbItemService.setUpdatedOn(new Date());
					dbItemService.setWorkOrderId(savedWorkOrder.getWorkOrderId());
					ItemService savedItemService = itemServiceRepository.save(dbItemService);
					savedWorkOrder.getItemServices().add(savedItemService);
				}
			}
				savedWorkOrder.setWoProcessedBy(new HashSet<>());
			if(newWorkOrder.getWoProcessedBy()!=null){
				for(String newWoProcessedBy : newWorkOrder.getWoProcessedBy()){
					WoProcessedBy dbWoProcessedBy = new WoProcessedBy();
					BeanUtils.copyProperties(newWoProcessedBy, dbWoProcessedBy, CommonUtils.getNullPropertyNames(newWoProcessedBy));
					dbWoProcessedBy.setDeletionIndicator(0L);
					dbWoProcessedBy.setCreatedBy(loginUserId);
					dbWoProcessedBy.setUpdatedBy(loginUserId);
					dbWoProcessedBy.setCreatedOn(new Date());
					dbWoProcessedBy.setUpdatedOn(new Date());
					dbWoProcessedBy.setWorkOrderId(savedWorkOrder.getWorkOrderId());
					dbWoProcessedBy.setProcessedBy(newWoProcessedBy);
					WoProcessedBy savedWoProcessedBy = woProcessedByRepository.save(dbWoProcessedBy);
					savedWorkOrder.getWoProcessedBy().add(savedWoProcessedBy);
				}
			}

		return savedWorkOrder;
	}
	
	/**
	 * updateWorkOrder
	 * @param workOrderId
	 * @param loginUserId 
	 * @param updateWorkOrder
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkOrder updateWorkOrder (String workOrderId, String loginUserId, UpdateWorkOrder updateWorkOrder)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrder dbWorkOrder = getWorkOrdr(workOrderId);
		BeanUtils.copyProperties(updateWorkOrder, dbWorkOrder, CommonUtils.getNullPropertyNames(updateWorkOrder));
		dbWorkOrder.setUpdatedBy(loginUserId);
		dbWorkOrder.setUpdatedOn(new Date());

		WorkOrder savedWorkOrder = workOrderRepository.save(dbWorkOrder);

		if(updateWorkOrder.getItemServices()!=null){
			if(itemServiceService.getItemService(workOrderId)!=null) {
				itemServiceService.deleteItemService(workOrderId, loginUserId);
			}
			for (ItemService newItemService : updateWorkOrder.getItemServices()) {
				ItemService dbItemService = new ItemService();
				BeanUtils.copyProperties(newItemService, dbItemService, CommonUtils.getNullPropertyNames(newItemService));
				dbItemService.setDeletionIndicator(0L);
				dbItemService.setCreatedBy(loginUserId);
				dbItemService.setCreatedOn(new Date());
				dbItemService.setUpdatedBy(loginUserId);
				dbItemService.setUpdatedOn(new Date());
				dbItemService.setWorkOrderId(savedWorkOrder.getWorkOrderId());

				if(dbItemService.getItemServiceQuantity()==null){
					dbItemService.setItemServiceQuantity((double) 0);
				}
				if(dbItemService.getItemServiceUnitPrice()==null){
					dbItemService.setItemServiceUnitPrice((double) 0);
				}
				if(dbItemService.getItemServiceTotal()==null){
					dbItemService.setItemServiceTotal((double) 0);
				}

				ItemService savedItemService = itemServiceRepository.save(dbItemService);
				savedWorkOrder.getItemServices().add(savedItemService);
			}
		}

		if(updateWorkOrder.getWoProcessedBy()!=null){
			if(woProcessedByService.getWoProcessedBy(workOrderId)!=null) {
				woProcessedByService.deleteWoProcessedBy(workOrderId, loginUserId);
			}
			for (String newWoProcessedBy : updateWorkOrder.getWoProcessedBy()) {
				WoProcessedBy dbWoProcessedBy = new WoProcessedBy();
				BeanUtils.copyProperties(newWoProcessedBy, dbWoProcessedBy, CommonUtils.getNullPropertyNames(newWoProcessedBy));
				dbWoProcessedBy.setDeletionIndicator(0L);
				dbWoProcessedBy.setCreatedOn(new Date());
				dbWoProcessedBy.setCreatedBy(loginUserId);
				dbWoProcessedBy.setUpdatedBy(loginUserId);
				dbWoProcessedBy.setUpdatedOn(new Date());
				dbWoProcessedBy.setWorkOrderId(savedWorkOrder.getWorkOrderId());
				dbWoProcessedBy.setProcessedBy(newWoProcessedBy);
				WoProcessedBy savedWoProcessedBy = woProcessedByRepository.save(dbWoProcessedBy);
				savedWorkOrder.getWoProcessedBy().add(savedWoProcessedBy);
			}
		}
		return savedWorkOrder;
	}
	
	/**
	 * deleteWorkOrder
	 * @param loginUserID 
	 * @param workOrderId
	 */
	public void deleteWorkOrder (String workOrderId, String loginUserID) {
		WorkOrder workorder = getWorkOrdr(workOrderId);
		if (workorder != null) {
			workorder.setDeletionIndicator(1L);
			workorder.setUpdatedBy(loginUserID);
			workorder.setUpdatedOn(new Date());
			workOrderRepository.save(workorder);
			if(itemServiceService.getItemService(workOrderId)!=null) {
				itemServiceService.deleteItemService(workOrderId,loginUserID);
			}
			if(woProcessedByService.getWoProcessedBy(workOrderId)!=null) {
				woProcessedByService.deleteWoProcessedBy(workOrderId,loginUserID);
			}
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + workOrderId);
		}
	}
	//Find WorkOrder
	public List<GWorkOrder> findWorkOrder(FindWorkOrder findWorkOrder) throws ParseException {

		WorkOrderSpecification spec = new WorkOrderSpecification(findWorkOrder);
		List<WorkOrder> results = workOrderRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);

		List<GWorkOrder> gWorkOrder = new ArrayList<>();
		for (WorkOrder dbWorkOrder : results) {
			GWorkOrder newGWorkOrder = new GWorkOrder();
			BeanUtils.copyProperties(dbWorkOrder, newGWorkOrder, CommonUtils.getNullPropertyNames(dbWorkOrder));
			newGWorkOrder.setWorkOrderProcessedBy(workOrderRepository.getProcessedBy(dbWorkOrder.getWorkOrderId()));
			newGWorkOrder.setCustomerName(workOrderRepository.getCustomerName(newGWorkOrder.getWorkOrderId()));
			newGWorkOrder.setWoProcessedBy(new ArrayList<>());
			for (WoProcessedBy newWoProcessedBy : dbWorkOrder.getWoProcessedBy()) {
				newGWorkOrder.getWoProcessedBy().add(newWoProcessedBy.getProcessedBy());
			}

			newGWorkOrder.setItemServices(new ArrayList<>());
			for(ItemService newItemService : dbWorkOrder.getItemServices()){
				newGWorkOrder.getItemServices().add(newItemService);
			}
			gWorkOrder.add(newGWorkOrder);
		}
		return gWorkOrder;
	}

}
