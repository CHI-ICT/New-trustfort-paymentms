package com.chh.trustfort.payment.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dofoleta
 */
@Setter
@Getter
public class TransactionHistoryGroupingDTO {

    String valueDate;
    List<TransactionHistoryDTO> data;

}
