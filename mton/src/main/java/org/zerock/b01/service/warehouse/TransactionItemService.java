package org.zerock.b01.service.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.operation.Transaction;
import org.zerock.b01.domain.operation.TransactionItem;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.operation.PartnerTransactionDTO;
import org.zerock.b01.dto.warehouse.TransactionListDTO;
import org.zerock.b01.dto.warehouse.TransactionViewDTO;

import java.util.List;

public interface TransactionItemService {

//  List<TransactionViewDTO> listWithTransactionItem();
  List<TransactionViewDTO> listWithTransactionItem(String keyword, List<String> category, String sort);

//  void createTransactionItemsForOrdering(Ordering ordering);

  void createTransactionByPartner(List<PartnerTransactionDTO> partnerTransactions);

  byte[] generateTransactionPdf(Transaction tran, List<TransactionItem> items) throws Exception;

  Page<Transaction> searchPartnerTransactions(Partner partner, String keyword, List<String> category, Pageable pageable);

  Page<Transaction> searchAllTransactions(String keyword, List<String> category, Pageable pageable);

  Page<TransactionListDTO> getAllIssuedTransactions(String keyword, List<String> category, Pageable pageable);
}
