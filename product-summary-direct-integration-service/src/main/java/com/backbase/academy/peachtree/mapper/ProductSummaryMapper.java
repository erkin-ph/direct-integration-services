package com.backbase.academy.peachtree.mapper;

import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.CurrentAccount;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.InvestmentAccount;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.SavingsAccount;
import com.mambu.sdk.model.v2.Client;
import com.mambu.sdk.model.v2.DepositAccount;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.SneakyThrows;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.threeten.bp.OffsetDateTime;

@Mapper
public interface ProductSummaryMapper {

    ProductSummaryMapper MAPPER = Mappers.getMapper(ProductSummaryMapper.class);

    @Mappings({
        @Mapping(
            source = "account.currencyCode",
            target = "currency"),
        @Mapping(
            source = "account.name",
            target = "name"),
        @Mapping(
            source = "account.notes",
            target = "alias"),
        @Mapping(
            expression = "java(account.getBalances().getAvailableBalance().toString())",
            target = "availableBalance"),
        @Mapping(
            expression = "java(account.getBalances().getTotalBalance().toString())",
            target = "bookedBalance"),
        @Mapping(
            source = "account.id",
            target = "id"),
        @Mapping(
            expression = "java(true)",
            target = "visible"),
        @Mapping(
            source = "account.encodedKey",
            target = "IBAN"),
        @Mapping(
            source = "account.accountType",
            target = "productTypeName")
    })
    CurrentAccount toCurrentAccount(DepositAccount account, Client client);

    @Mappings({
        @Mapping(
            source = "account.currencyCode",
            target = "currency"),
        @Mapping(
            source = "account.name",
            target = "name"),
        @Mapping(
            source = "account.notes",
            target = "alias"),
        @Mapping(
            source = "account.id",
            target = "id"),
        @Mapping(
            expression = "java(true)",
            target = "visible"),
        @Mapping(
            source = "account.accountType",
            target = "productTypeName")
    })
    InvestmentAccount toInvestmentAccount(DepositAccount account);

    @Mappings({
        @Mapping(
            source = "account.currencyCode",
            target = "currency"),
        @Mapping(
            source = "account.name",
            target = "name"),
        @Mapping(
            expression = "java(true)",
            target = "visible"),
        @Mapping(
            source = "account.notes",
            target = "alias"),
        @Mapping(
            expression = "java(account.getBalances().getTotalBalance().toString())",
            target = "bookedBalance"),
        @Mapping(
            source = "account.encodedKey",
            target = "IBAN"),
        @Mapping(
            expression = "java(account.getAccruedAmounts().getInterestAccrued())",
            target = "accountInterestRate"),
        @Mapping(
            source = "account.id",
            target = "id"),
        @Mapping(
            source = "account.accountType",
            target = "productTypeName"),
        @Mapping(
            ignore = true,
            target = "maturityDate")
    })
    SavingsAccount toSavingsAccount(DepositAccount account, Client client);

    @AfterMapping
    default void setAccountHolderName(@MappingTarget CurrentAccount account, Client client) {
        account.setAccountHolderName(client.getFirstName() + " " + client.getLastName());
    }

    @AfterMapping
    default void setAccountHolderName(@MappingTarget SavingsAccount account, Client client) {
        account.setAccountHolderName(client.getFirstName() + " " + client.getLastName());
    }

    @AfterMapping
    default void setAccountInterestRate(@MappingTarget CurrentAccount currentAccount, DepositAccount depositAccount) {
        if (depositAccount.getInterestSettings().getInterestRateSettings() != null) {
            currentAccount.setAccountInterestRate(
                depositAccount.getInterestSettings().getInterestRateSettings().getInterestRate());
        } else if (depositAccount.getAccruedAmounts().getInterestAccrued() != null) {
            currentAccount.setAccountInterestRate(depositAccount.getAccruedAmounts().getInterestAccrued());
        }
    }

    @AfterMapping
    default void setAccountOpeningDate(@MappingTarget InvestmentAccount account, DepositAccount depositAccount) {
        account.setAccountOpeningDate(getDateInProperFormat(depositAccount.getCreationDate()));
    }

    @AfterMapping
    default void setAccountOpeningDate(@MappingTarget SavingsAccount account, DepositAccount depositAccount) {
        account.setAccountOpeningDate(getDateInProperFormat(depositAccount.getCreationDate()));
    }

    @AfterMapping
    default void setAccountOpeningDate(@MappingTarget CurrentAccount account, DepositAccount depositAccount) {
        account.setAccountOpeningDate(getDateInProperFormat(depositAccount.getCreationDate()));
    }

    @SneakyThrows
    default Date getDateInProperFormat(OffsetDateTime creationDate) {
        int year = creationDate.getYear();
        int month = creationDate.getMonthValue();
        int day = creationDate.getDayOfMonth();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        StringBuffer sb = new StringBuffer(day + "-" + month + "-" + year);
        return simpleDateFormat.parse(sb.toString());
    }

}