package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.model.inbound.staging.SearchStagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;
import com.tekclover.wms.api.transaction.model.report.SearchTransactionDetailsDashBoard;
import com.tekclover.wms.api.transaction.model.report.TransactionDetailDashBoardImpl;
import com.tekclover.wms.api.transaction.model.report.TransactionDetailsDashBoard;
import com.tekclover.wms.api.transaction.model.report.TransactionDetailsDashBoardReport;
import com.tekclover.wms.api.transaction.repository.TransactionDetailsDashBoardRepository;
import com.tekclover.wms.api.transaction.repository.specification.StagingHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class TransactionDetailsDashBoardService extends BaseService {

    @Autowired
    private TransactionDetailsDashBoardRepository transactionDetailsDashBoardRepository;

    //---------------------------------------------------------------------------------------------------------------------------


    /**
     *
     * @param searchTransactionDetailsDashBoard
     * @return
     */
//    public List<TransactionDetailsDashBoardReport> findTransactionDetailsDashBoard(SearchTransactionDetailsDashBoard searchTransactionDetailsDashBoard) {
//        try {
//            if (searchTransactionDetailsDashBoard.getStartCreatedOn() != null && searchTransactionDetailsDashBoard.getEndCreatedOn() != null) {
//                Date[] dates = DateUtils.addTimeToDatesForSearch(searchTransactionDetailsDashBoard.getStartCreatedOn(), searchTransactionDetailsDashBoard.getEndCreatedOn());
//                searchTransactionDetailsDashBoard.setStartCreatedOn(dates[0]);
//                searchTransactionDetailsDashBoard.setEndCreatedOn(dates[1]);
//            }
//
//            List<TransactionDetailDashBoardImpl> readResults = transactionDetailsDashBoardRepository.getRead();
//            List<TransactionDetailDashBoardImpl> unReadResults = transactionDetailsDashBoardRepository.getRead();
//
//            List<TransactionDetailsDashBoardReport> results = new ArrayList<>();
//            TransactionDetailsDashBoardReport readTransactionDetailsDashBoardReport = new TransactionDetailsDashBoardReport();
//            readTransactionDetailsDashBoardReport.setType("PreviousLines");
//            readTransactionDetailsDashBoardReport.setLines(readResults);
//            results.add(readTransactionDetailsDashBoardReport);
//            TransactionDetailsDashBoardReport unReadTransactionDetailsDashBoardReport = new TransactionDetailsDashBoardReport();
//            unReadTransactionDetailsDashBoardReport.setType("NewLines");
//            unReadTransactionDetailsDashBoardReport.setLines(unReadResults);
//            results.add(unReadTransactionDetailsDashBoardReport);
//
//            return results;
//        } catch (Exception e) {
//            log.error("Exception while TransactionDetailsDashBoard find : " + e.toString());
//        }
//    }

    /**
     * @param newTransactionDetailsDashBoard
     * @param loginUserID
     * @return
     */
    public void createTransactionDetailsDashBoard(TransactionDetailsDashBoard newTransactionDetailsDashBoard, String loginUserID) {
        try {
            TransactionDetailsDashBoard dbTransactionDetailsDashBoard = new TransactionDetailsDashBoard();
            log.info("newTransactionDetailsDashBoard : " + newTransactionDetailsDashBoard);
            BeanUtils.copyProperties(newTransactionDetailsDashBoard, dbTransactionDetailsDashBoard, CommonUtils.getNullPropertyNames(newTransactionDetailsDashBoard));
            dbTransactionDetailsDashBoard.setCreatedBy(loginUserID);
            dbTransactionDetailsDashBoard.setCreatedOn(new Date());
            transactionDetailsDashBoardRepository.save(dbTransactionDetailsDashBoard);
        } catch (Exception e) {
            log.error("Exception while TransactionDetailsDashBoard create : " + e.toString());
        }
    }

    /**
     * @param loginUserID
     */
    public void updateTransactionDetailsDashBoard(List<TransactionDetailsDashBoard> updateTransactionDetailsDashBoards, String loginUserID) {
        try {
            if (updateTransactionDetailsDashBoards != null && !updateTransactionDetailsDashBoards.isEmpty()) {
                for (TransactionDetailsDashBoard updateTransactionDetailsDashBoard : updateTransactionDetailsDashBoards) {
                    TransactionDetailsDashBoard dbTransactionDetailsDashBoard = transactionDetailsDashBoardRepository.findByTransactionId(updateTransactionDetailsDashBoard.getTransactionId());
                    if (dbTransactionDetailsDashBoard != null) {
                        dbTransactionDetailsDashBoard.setRead(true);
                        dbTransactionDetailsDashBoard.setUpdatedBy(loginUserID);
                        dbTransactionDetailsDashBoard.setUpdatedOn(new Date());
                        transactionDetailsDashBoardRepository.save(dbTransactionDetailsDashBoard);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception while TransactionDetailsDashBoard update : " + e.toString());
        }
    }

}