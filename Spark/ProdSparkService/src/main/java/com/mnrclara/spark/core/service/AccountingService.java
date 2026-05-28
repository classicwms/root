//package com.mnrclara.spark.core.service;
//
//import com.mnrclara.spark.core.model.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@Service
//@Slf4j
//public class AccountingService {
//
//
//    @Autowired
//    private InvoiceService invoiceService;
//
//    /**
//     *
//     * @param requestData
//     * @return
//     * @throws ParseException
//     */
//    public List<BilledHoursPaidReport> getBilledHoursPaidReport(BilledHoursPaid requestData) throws Exception {
////        if (requestData.getFromPostingDate() != null && requestData.getToPostingDate() != null) {
////            Date[] dates = DateUtils.addTimeToDatesForSearch(requestData.getFromPostingDate(), requestData.getToPostingDate());
////            requestData.setFromPostingDate(dates[0]);
////            requestData.setToPostingDate(dates[1]);
////        }
//
//        List<IBilledHourReportInvoiceHeader> invoiceHeaderDetails = null;
//        if (requestData.getMatterNumber() == null && requestData.getClientId() == null) {
//            invoiceHeaderDetails =
//                    invoiceService.SumOfInvoiceAmtWOMatterNumberAndClientID(requestData.getFromPostingDate(), requestData.getToPostingDate());
//            log.info("invoiceHeaderDetails.......: " + invoiceHeaderDetails);
//        }
//        else if (requestData.getMatterNumber() != null && requestData.getClientId() == null) {
//            invoiceHeaderDetails =
//                    invoiceService.getSumOfInvoiceAmtByMatterNumber(requestData.getFromPostingDate(), requestData.getToPostingDate(),
//                            requestData.getMatterNumber());
//            log.info("invoiceHeaderDetails.......: " + invoiceHeaderDetails);
//        } else if (requestData.getMatterNumber() == null && requestData.getClientId() != null) {
//            invoiceHeaderDetails =
//                    invoiceService.getSumOfInvoiceAmtByClientID(requestData.getFromPostingDate(), requestData.getToPostingDate(),
//                            requestData.getClientId());
//            log.info("invoiceHeaderDetails.......: " + invoiceHeaderDetails);
//
//        } else if (requestData.getMatterNumber() != null && requestData.getClientId() != null) {
//            invoiceHeaderDetails =
//                    invoiceService.getSumOfInvoiceAmtByMatterNumberAndClientID(requestData.getFromPostingDate(), requestData.getToPostingDate(),
//                            requestData.getMatterNumber(), requestData.getClientId());
//        }
//
//        List<BilledHoursPaidReport> results = new ArrayList<>();
//        try {
//            invoiceHeaderDetails.stream().forEach(a -> {
//                BilledHoursPaidReport response = new BilledHoursPaidReport();
//
////                response.setMatterNumber(a.getMatterNumber());
////                MatterGenAcc matterGenAcc = matterGenAccRepository.findByMatterNumber(a.getMatterNumber());
////                if (matterGenAcc != null) {
////                    response.setMatterText(matterGenAcc.getMatterDescription());
////                }
////
////                response.setInvoiceNumber(a.getInvoiceNumber());
////                response.setDateOfBill(a.getDateOfBill());
////                log.info("a.getMatterNumber() : " + a.getMatterNumber());
////                log.info("getFromPostingDate() : " + requestData.getFromPostingDate());
////                log.info("getToPostingDate() : " + requestData.getToPostingDate());
////
////                Double paymentAmount = invoiceHeaderRepository.getSumOfPaymentAmtByMatterNumber(requestData.getFromPostingDate(),
////                        requestData.getToPostingDate(), a.getMatterNumber());
////                paymentAmount = (paymentAmount != null) ? paymentAmount : 0D;
////                log.info("paymentAmount : " + paymentAmount);
////
////                if (paymentAmount != 0D) {
////                    Double dividedAmount = (paymentAmount / a.getInvoiceAmount());
////                    dividedAmount = (dividedAmount != null) ? dividedAmount : 0D;
////
////                    IBilledHourReportMatterTimeTicket mttAppDetails = null;
////                    if (requestData.getTimeKeeperCode() != null) {
////                        mttAppDetails = invoiceHeaderRepository.getMatterTimeTicketByMatterNumberWithTKCode(a.getPreBillNumber(), a.getMatterNumber(),
////                                requestData.getTimeKeeperCode());
////                    } else {
////                        mttAppDetails = invoiceHeaderRepository.getMatterTimeTicketByMatterNumberWOTKCode(a.getPreBillNumber(), a.getMatterNumber());
////                    }
////
////                    Double amountBilled = 0D;
////                    Double hoursBilled = 0D;
////                    if (mttAppDetails != null) {
////                        response.setAttorney(mttAppDetails.getAttorney());
////                        response.setHoursBilled(mttAppDetails.getHoursBilled());
////                        response.setAmountBilled(mttAppDetails.getAmountBilled());
////                        hoursBilled = (mttAppDetails.getHoursBilled() != null) ? mttAppDetails.getHoursBilled() : 0D;
////                        amountBilled = (mttAppDetails.getAmountBilled() != null) ? mttAppDetails.getAmountBilled() : 0D;
////                    }
////
////                    Double approxAmountReceived = (dividedAmount * amountBilled);
////                    response.setApproxAmountReceived(approxAmountReceived);
////
////                    Double calculatedBillHours = (amountBilled / hoursBilled);
////                    Double approxBilledhours = (approxAmountReceived / calculatedBillHours);
////                    response.setApproxHoursPaid(approxBilledhours);
////                }
//
//                log.info("response.......: " + response);
//                results.add(response);
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return results;
//    }
//
//}