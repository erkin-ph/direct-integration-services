package com.backbase.academy.peachtree.controller;

import static com.backbase.peachtree.util.PeachtreeConstants.HEADER_TOTAL_COUNT;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import com.backbase.academy.peachtree.service.TransactionsService;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.ForbiddenException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.ConflictException;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.EnumValuesByAttributeNameGetResponseBody;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.MethodNotAllowedException;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionItem;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionsApi;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionsClientPatchRequestBody;
import com.mambu.sdk.api.ApiException;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/v2/transactions", "/client-api/v2/transactions"})
@Slf4j
@RequiredArgsConstructor
public class TransactionsController implements TransactionsApi {

    private final TransactionsService transactionsService;

    @Override
    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionItem> getTransactions(
        @RequestParam(required = false)
            BigDecimal amountGreaterThan,
        @RequestParam(required = false)
            BigDecimal amountLessThan,
        @RequestParam(required = false)
            String bookingDateGreaterThan,
        @RequestParam(required = false)
            String bookingDateLessThan,
        @RequestParam(required = false)
            String type,
        @RequestParam(required = false)
            String description,
        @RequestParam(required = false)
            String reference,
        @RequestParam(required = false)
            String typeGroup,
        @RequestParam(required = false)
            String counterPartyName,
        @RequestParam(required = false)
            String counterPartyAccountNumber,
        @RequestParam(required = false)
            String creditDebitIndicator,
        @RequestParam(required = false)
            String category,
        @RequestParam(required = false)
            String[] categories,
        @RequestParam(required = false)
            String billingStatus,
        @RequestParam(required = false)
            String currency,
        @RequestParam(required = false)
            Integer notes,
        @RequestParam(required = false)
            String id,
        @RequestParam(required = false)
            String productId,
        @RequestParam(required = false)
            String arrangementId,
        @RequestParam(required = false)
            String[] arrangementsIds,
        @RequestParam(required = false)
            BigDecimal fromCheckSerialNumber,
        @RequestParam(required = false)
            BigDecimal toCheckSerialNumber,
        @RequestParam(required = false)
            BigDecimal[] checkSerialNumbers,
        @RequestParam(required = false)
            String query,
        @RequestParam(required = false, defaultValue = "0")
            Integer from,
        @RequestParam(required = false, defaultValue = "")
            String cursor,
        @RequestParam(required = false, defaultValue = "10")
            Integer size,
        @RequestParam(required = false)
            String orderBy,
        @RequestParam(required = false, defaultValue = "DESC")
            String direction,
        @RequestParam(required = false, defaultValue = "DESC")
            String secDirection, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) {

        try {
            List<TransactionItem> transactionItems = transactionsService
                .retrieveTransactionsByAccountId(arrangementId, from, size);
            //set response header
            httpServletResponse.addHeader(HEADER_TOTAL_COUNT, new Integer(transactionItems.size()).toString());
            return transactionItems;
        } catch (RuntimeException | ApiException e) {
            log.error("Unable to retrieve the transactions", e);
        }
        return emptyList();
    }

    @Override
    public void patchTransactions(@Valid List<TransactionsClientPatchRequestBody> list,
        HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
        throws BadRequestException, ForbiddenException, InternalServerErrorException, NotFoundException, ConflictException {
        httpServletResponse.setStatus(NOT_IMPLEMENTED.value());
    }

    @Override
    public void getTransactionsCsv(BigDecimal bigDecimal, BigDecimal bigDecimal1, String s, String s1, String s2,
        String s3, String s4, String s5, String s6, String s7, String s8, String s9, String[] strings, String s10,
        String s11, Integer integer, String s12, String s13, String s14, String[] strings1, BigDecimal bigDecimal2,
        BigDecimal bigDecimal3, BigDecimal[] bigDecimals, String s15, Integer integer1, String s16, Integer integer2,
        String s17, String s18, String s19, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse)
        throws BadRequestException, ForbiddenException, InternalServerErrorException {
        httpServletResponse.setStatus(NOT_IMPLEMENTED.value());
    }

    @Override
    public void getTransactionsExport(BigDecimal bigDecimal, BigDecimal bigDecimal1, String s, String s1, String s2,
        String s3, String s4, String s5, String s6, String s7, String s8, String s9, String[] strings, String s10,
        String s11, Integer integer, String s12, String s13, String s14, String[] strings1, BigDecimal bigDecimal2,
        BigDecimal bigDecimal3, BigDecimal[] bigDecimals, String s15, Integer integer1, String s16, Integer integer2,
        String s17, String s18, String s19, String s20, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse)
        throws BadRequestException, ForbiddenException, InternalServerErrorException {
        httpServletResponse.setStatus(NOT_IMPLEMENTED.value());
    }

    @Override
    public List<EnumValuesByAttributeNameGetResponseBody> getEnumValuesByAttributeName(String s,
        HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
        throws BadRequestException, ForbiddenException, InternalServerErrorException, MethodNotAllowedException {
        return emptyList();
    }
}