package com.backbase.academy.peachtree.mapper;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionItem;
import com.mambu.sdk.model.v2.Client;
import com.mambu.sdk.model.v2.DepositAccount;
import com.mambu.sdk.model.v2.DepositTransaction;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.OffsetDateTime;

@Mapper
public interface TransactionMapper {

    TransactionMapper MAPPER = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "id", source = "depositTransaction.encodedKey")
    @Mapping(target = "arrangementId", source = "depositTransaction.parentAccountKey")
    @Mapping(target = "bookingDate", dateFormat = "dd-MM-yyyy", source = "depositTransaction.creationDate", qualifiedByName = "fromThreeTenToDate")
    //@Mapping(target = "bookingDate", dateFormat = "dd-MM-yyyy", source = "depositTransaction.bookingDate", qualifiedByName = "fromThreeTenToDate")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "type", source = "depositTransaction.type")
    @Mapping(target = "valueDate", dateFormat = "dd-MM-yyyy", source = "depositTransaction.valueDate", qualifiedByName = "fromThreeTenToDate")
    @Mapping(target = "transactionAmountCurrency.amount", source = "depositTransaction.amount")
    @Mapping(target = "transactionAmountCurrency.currencyCode", source = "depositTransaction.currencyCode")
    @Mapping(target = "currency", source = "depositTransaction.currencyCode")
    @Mapping(target = "creditDebitIndicator", source = "depositTransaction.amount", qualifiedByName = "creditDebitIndicatorUtil")
    @Mapping(target = "runningBalance", source = "depositTransaction.accountBalances.totalBalance")
    @Mapping(target = "counterPartyName", ignore = true)
    @Mapping(target = "counterPartyAccountNumber", source = "depositAccount.id")
    @Mapping(target = "counterPartyCountry", ignore = true)
//    @Mapping(target = "creditorId", source = "otherAccount.holder.name")
    //display arrangment id is reference
    @Mapping(target = "reference", source = "depositTransaction.parentAccountKey")
    @Mapping(target = "typeGroup", source = "depositTransaction.type")
//    @Mapping(target = "category", constant = "Food Drinks")
//    @Mapping(target = "amount", source = "details.newBalance.amount", qualifiedByName = "instructedAmountConverter", defaultValue = "0")
    @Mapping(target = "instructedAmount", source = "depositTransaction.amount")
//    @Mapping(target = "instructedCurrency", defaultValue = "EUR")
//    @Mapping(target = "currencyExchangeRate", defaultValue = "1")
//    @Mapping(target = "counterPartyBIC", source = "thisAccount.bank.nationalIdentifier", defaultValue = "ING00000001")
//    @Mapping(target = "counterPartyBankName", source = "thisAccount.bank.name")
//    @Mapping(target = "mandateReference", source = "details.type")
//    @Mapping(target = "billingStatus", defaultValue = "BILLED")
//    @Mapping(target = "checkSerialNumber", defaultValue = "1")
    TransactionItem toTransactionsPostRequestBody(DepositTransaction depositTransaction, Client client,
        DepositAccount depositAccount);

    @Named("creditDebitIndicatorUtil")
    default TransactionItem.CreditDebitIndicator amountToCreditDebitIndicator(String amount) {
        BigDecimal value = new BigDecimal(amount);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return TransactionItem.CreditDebitIndicator.DBIT;
        }
        return TransactionItem.CreditDebitIndicator.CRDT;
    }

    @Named("fromThreeTenToDate")
    default Date fromThreeTenInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : DateTimeUtils.toDate(offsetDateTime.toInstant());
    }

    @BeforeMapping
    default void setCategories(@MappingTarget TransactionItem transactionItem) {
        String[] categories = {"Fast Food", "Public Transport", "Home", "Transfers", "Hobbies & Entertainment",
            "Shopping", "Other Income"};
        transactionItem.setCategory(categories[new Random().nextInt(categories.length)]);
    }

    @AfterMapping
    default void setCounterPartyName(@MappingTarget TransactionItem transactionItem, Client client) {
        transactionItem.setCounterPartyName(client.getFirstName() + " " + client.getLastName());
    }


    @AfterMapping
    default void setNotes(@MappingTarget TransactionItem transactionItem, DepositTransaction depositTransaction) {
        transactionItem.setDescription(depositTransaction.getNotes());
    }

    @AfterMapping
    default void setCountry(@MappingTarget TransactionItem transactionItem, Client client) {
        if (!isEmpty(client.getAddresses())) {
            return;
        }
        transactionItem.setCounterPartyName(client.getAddresses().get(0).getCountry());
    }
}