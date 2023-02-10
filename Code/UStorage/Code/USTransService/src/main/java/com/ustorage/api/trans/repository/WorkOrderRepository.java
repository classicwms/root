package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ustorage.api.trans.model.workorder.WorkOrder;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>,
		JpaSpecificationExecutor<WorkOrder> {

	public List<WorkOrder> findAll();

	public Optional<WorkOrder> findByWorkOrderIdAndDeletionIndicator(String workOrderId, long l);

	@Query(value = "SELECT distinct tblleadcustomer.CUSTOMER_NAME \r\n"
			+ "FROM tblleadcustomer \r\n"
			+ "JOIN tblworkorder ON tblworkorder.CUSTOMER_ID=tblleadcustomer.CUSTOMER_CODE and \r\n"
			+ "(COALESCE(:workOrderId,null) IS NULL OR (tblworkorder.WORK_ORDER_ID IN (:workOrderId))) and \r\n"
			+ "tblleadcustomer.IS_DELETED=0 AND tblworkorder.IS_DELETED=0", nativeQuery = true)
	public String getCustomerName(@Param("workOrderId") String workOrderId);

	@Query(value = "select STRING_AGG(twp.processed_by,', ') \n" +
			"from tblwoprocessedbyteam twp \n" +
			"join tblworkorder tw on tw.work_order_id=twp.work_order_id\n"+
			" where \n" +
			"(COALESCE(:workOrderId,null) IS NULL OR (tw.work_order_id IN (:workOrderId))) and\n"+
			"twp.is_deleted=0 ",nativeQuery = true)
	public String getProcessedBy(@Param("workOrderId") String workOrderId);
}