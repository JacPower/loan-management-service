package com.interview.lender.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String accountNumber;
    private BigDecimal alternativechanneltrnscrAmount;
    private long alternativechanneltrnscrNumber;
    private BigDecimal alternativechanneltrnsdebitAmount;
    private long alternativechanneltrnsdebitNumber;
    private long atmTransactionsNumber;
    private BigDecimal atmtransactionsAmount;
    private int bouncedChequesDebitNumber;
    private int bouncedchequescreditNumber;
    private BigDecimal bouncedchequetransactionscrAmount;
    private BigDecimal bouncedchequetransactionsdrAmount;
    private BigDecimal chequeDebitTransactionsAmount;
    private int chequeDebitTransactionsNumber;
    private long createdAt;
    private long createdDate;
    private BigDecimal credittransactionsAmount;
    private BigDecimal debitcardpostransactionsAmount;
    private long debitcardpostransactionsNumber;
    private BigDecimal fincominglocaltransactioncrAmount;
    private int id;
    private BigDecimal incominginternationaltrncrAmount;
    private long incominginternationaltrncrNumber;
    private int incominglocaltransactioncrNumber;
    private BigDecimal intrestAmount;
    private long lastTransactionDate;
    private Integer lastTransactionType;
    private int lastTransactionValue;
    private double maxAtmTransactions;
    private double maxMonthlyBebitTransactions;
    private double maxalternativechanneltrnscr;
    private double maxalternativechanneltrnsdebit;
    private double maxbouncedchequetransactionscr;
    private double maxchequedebittransactions;
    private double maxdebitcardpostransactions;
    private double maxincominginternationaltrncr;
    private double maxincominglocaltransactioncr;
    private double maxmobilemoneycredittrn;
    private double maxmobilemoneydebittransaction;
    private double maxmonthlycredittransactions;
    private double maxoutgoinginttrndebit;
    private double maxoutgoinglocaltrndebit;
    private double maxoverthecounterwithdrawals;
    private double minAtmTransactions;
    private double minMonthlyDebitTransactions;
    private double minalternativechanneltrnscr;
    private double minalternativechanneltrnsdebit;
    private double minbouncedchequetransactionscr;
    private double minchequedebittransactions;
    private double mindebitcardpostransactions;
    private double minincominginternationaltrncr;
    private double minincominglocaltransactioncr;
    private double minmobilemoneycredittrn;
    private double minmobilemoneydebittransaction;
    private double minmonthlycredittransactions;
    private double minoutgoinginttrndebit;
    private double minoutgoinglocaltrndebit;
    private double minoverthecounterwithdrawals;
    private BigDecimal mobilemoneycredittransactionAmount;
    private long mobilemoneycredittransactionNumber;
    private BigDecimal mobilemoneydebittransactionAmount;
    private long mobilemoneydebittransactionNumber;
    private BigDecimal monthlyBalance;
    private BigDecimal monthlydebittransactionsAmount;
    private BigDecimal outgoinginttransactiondebitAmount;
    private long outgoinginttrndebitNumber;
    private BigDecimal outgoinglocaltransactiondebitAmount;
    private long outgoinglocaltransactiondebitNumber;
    private BigDecimal overdraftLimit;
    private BigDecimal overthecounterwithdrawalsAmount;
    private long overthecounterwithdrawalsNumber;
    private BigDecimal transactionValue;
    private long updatedAt;
}
