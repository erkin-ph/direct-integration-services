package com.backbase.academy.peachtree.service;

import static com.backbase.peachtree.util.PeachtreeConstants.FILTER_DETAILS_LEVEL_FULL;
import static com.backbase.peachtree.util.PeachtreeConstants.FILTER_PAGINATION_DETAILS_ON;
import static java.util.Collections.emptyList;

import com.backbase.academy.peachtree.mapper.TransactionMapper;
import com.backbase.peachtree.mambu.config.MambuApiClientConfiguration;
import com.backbase.presentation.transaction.rest.spec.v2.transactions.TransactionItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mambu.sdk.api.ApiException;
import com.mambu.sdk.api.v2.ClientsApi;
import com.mambu.sdk.api.v2.DepositAccountsApi;
import com.mambu.sdk.api.v2.DepositTransactionsApi;
import com.mambu.sdk.model.v2.Client;
import com.mambu.sdk.model.v2.DepositAccount;
import com.mambu.sdk.model.v2.DepositTransaction;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionsService {

    private final DepositTransactionsApi depositTransactionsApi;
    private final DepositAccountsApi depositAccountsApi;
    private final ClientsApi clientsApi;

    public List<TransactionItem> retrieveTransactionsByAccountId(String depositAccountId, Integer from, Integer size)
        throws ApiException {
        MambuApiClientConfiguration
            .setBasicTokenToConfiguration(depositAccountsApi.getApiClient(), clientsApi.getApiClient(),
                depositTransactionsApi.getApiClient());

        log.info("Retrieving transactions for account '{}' with offset '{}' and payload size '{}'", depositAccountId,
            from, size);

        List<DepositTransaction> depositTransactions = depositTransactionsApi
            .getAll(depositAccountId, from, size, FILTER_PAGINATION_DETAILS_ON, FILTER_DETAILS_LEVEL_FULL);
        long start = System.currentTimeMillis();
        List<TransactionItem> transactionItems = depositTransactions.parallelStream().map(depositTransaction -> {
            TransactionItem transactionItem = new TransactionItem();
            try {

                DepositTransaction destination = retrieveFullDepositTransactionDetails(depositTransaction);

                String accountDestinationKey = destination.getParentAccountKey();

                DepositAccount accountDestination = retrieveFullDepositAccountsById(accountDestinationKey);

                String accountDestinationAccountHolderKey = accountDestination.getAccountHolderKey();

                Client destinationClient = retrieveFullClientById(accountDestinationAccountHolderKey);

                log.info("Merging and mapping result to TransactionItem defined in the transaction-presentation-spec");

                transactionItem = TransactionMapper.MAPPER
                    .toTransactionsPostRequestBody(depositTransaction, destinationClient, accountDestination);

            } catch (ApiException e) {
                log.error(e.getMessage());
            }
            return transactionItem;
        }).collect(Collectors.toList());

        log.info("Time spent '{}", (System.currentTimeMillis() - start));

        return transactionItems;
    }

    private DepositTransaction retrieveFullDepositTransactionDetails(DepositTransaction depositTransaction)
        throws ApiException {
        log.info(
            "Retrieving the depositTransaction details for the destination account based on the type of the Transaction '{}'",
            depositTransaction.getType().toString());

        if (!isTransfer(depositTransaction)
            || depositTransaction.getTransferDetails().getLinkedDepositTransactionKey() == null) {
            return depositTransaction;
        }

        String linkedDepositTransactionKey = depositTransaction.getTransferDetails().getLinkedDepositTransactionKey();
        return isTransfer(depositTransaction) ? depositTransactionsApi
            .getById(linkedDepositTransactionKey, FILTER_DETAILS_LEVEL_FULL) : depositTransaction;
    }

    private DepositAccount retrieveFullDepositAccountsById(String depositAccountId) throws ApiException {
        log.info("Retrieving the details for the account with id '{}'", depositAccountId);
        return depositAccountsApi.getById(depositAccountId, FILTER_DETAILS_LEVEL_FULL);
    }

    private Client retrieveFullClientById(String clientId) throws ApiException {
        log.info("Retrieving the details for the client with id '{}'", clientId);
        return clientsApi.getById(clientId, FILTER_DETAILS_LEVEL_FULL);
    }

    private boolean isTransfer(DepositTransaction depositTransaction) {
        log.info("Verifying if the type of the transaction is equal to TRANSFER '{}'", depositTransaction.getType());
        return depositTransaction.getType().equals(DepositTransaction.TypeEnum.TRANSFER);
    }

}