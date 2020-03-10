package com.backbase.academy.peachtree.service;

import static com.backbase.peachtree.util.PeachtreeConstants.ACCOUNT_HOLDER_TYPE_CLIENT;
import static com.backbase.peachtree.util.PeachtreeConstants.ACCOUNT_STATE_ACTIVE;
import static com.backbase.peachtree.util.PeachtreeConstants.CENTRE_ID;
import static com.backbase.peachtree.util.PeachtreeConstants.CURRENT_ACCOUNTS;
import static com.backbase.peachtree.util.PeachtreeConstants.FILTER_DETAILS_LEVEL_FULL;
import static com.backbase.peachtree.util.PeachtreeConstants.FILTER_PAGINATION_DETAILS_OFF;
import static com.backbase.peachtree.util.PeachtreeConstants.INVESTMENT_ACCOUNTS;
import static com.backbase.peachtree.util.PeachtreeConstants.NULL_BRANCH_ID;
import static com.backbase.peachtree.util.PeachtreeConstants.NULL_CREDIT_OFFICER_USERNAME;
import static com.backbase.peachtree.util.PeachtreeConstants.NULL_LIMIT;
import static com.backbase.peachtree.util.PeachtreeConstants.NULL_OFFSET;
import static com.backbase.peachtree.util.PeachtreeConstants.SAVINGS_ACCOUNTS;

import com.backbase.academy.peachtree.mapper.ProductSummaryMapper;
import com.backbase.peachtree.mambu.config.MambuApiClientConfiguration;
import com.backbase.peachtree.util.InternalTokenClaimsUtil;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.CurrentAccount;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.CurrentAccounts;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.InvestmentAccount;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.InvestmentAccounts;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.ProductSummaryGetResponseBody;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.SavingsAccount;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.SavingsAccounts;
import com.mambu.sdk.api.ApiException;
import com.mambu.sdk.api.v2.ClientsApi;
import com.mambu.sdk.api.v2.DepositAccountsApi;
import com.mambu.sdk.model.v2.Client;
import com.mambu.sdk.model.v2.DepositAccount;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductSummaryService {

    private final ClientsApi clientsApi;
    private final DepositAccountsApi depositAccountsApi;
    private final ProductSummaryMapper productSummaryMapper;

    public ProductSummaryGetResponseBody getProductSummary() throws ApiException {
        MambuApiClientConfiguration
            .setBasicTokenToConfiguration(clientsApi.getApiClient(), depositAccountsApi.getApiClient());
        log.info("Retrieving deposit accounts");

        final List<DepositAccount> depositAccounts = depositAccountsApi.getAll(NULL_OFFSET,
            NULL_LIMIT,
            FILTER_PAGINATION_DETAILS_OFF,
            FILTER_DETAILS_LEVEL_FULL,
            NULL_CREDIT_OFFICER_USERNAME,
            NULL_BRANCH_ID,
            CENTRE_ID,
            ACCOUNT_STATE_ACTIVE,
            ACCOUNT_HOLDER_TYPE_CLIENT,
            InternalTokenClaimsUtil.getAccountHolderId());
        log.debug("Deposit accounts list is " + depositAccounts);

        List<InvestmentAccount> investmentAccounts = new ArrayList();
        List<CurrentAccount> currentAccounts = new ArrayList<>();
        List<SavingsAccount> savingsAccounts = new ArrayList<>();

        Client client = getClientById();
        if (depositAccounts != null && !depositAccounts.isEmpty()) {
            for (DepositAccount depositAccount : depositAccounts) {
                switch (depositAccount.getAccountType()) {
                    case SAVINGS_PLAN:
                        investmentAccounts.add(productSummaryMapper.toInvestmentAccount(depositAccount));
                        break;
                    case FIXED_DEPOSIT:
                        log.info("Not sure what fixed deposit maps to in productSummary");
                        break;
                    case CURRENT_ACCOUNT:
                        currentAccounts.add(productSummaryMapper.toCurrentAccount(depositAccount, client));
                        break;
                    case REGULAR_SAVINGS:
                        savingsAccounts.add(productSummaryMapper.toSavingsAccount(depositAccount, client));
                        break;
                    case INVESTOR_ACCOUNT:
                        log.info("Not sure what investor account maps to in productSummary");
                        break;
                    default:
                        log.info("unknown account type");
                        break;
                }
            }
        }
        return setAccounts(investmentAccounts, currentAccounts, savingsAccounts);
    }

    private ProductSummaryGetResponseBody setAccounts(List<InvestmentAccount> investmentAccounts,
        List<CurrentAccount> currentAccounts, List<SavingsAccount> savingsAccounts) {

        ProductSummaryGetResponseBody responseBody = new ProductSummaryGetResponseBody();

        if (savingsAccounts != null && !savingsAccounts.isEmpty()) {
            SavingsAccounts savingsAccountsHolder = new SavingsAccounts();
            savingsAccountsHolder.setName(SAVINGS_ACCOUNTS);
            savingsAccountsHolder.setProducts(savingsAccounts);
            responseBody.setSavingsAccounts(savingsAccountsHolder);
        }

        if (investmentAccounts != null && !investmentAccounts.isEmpty()) {
            InvestmentAccounts investmentAccountsHolder = new InvestmentAccounts();
            investmentAccountsHolder.setName(INVESTMENT_ACCOUNTS);
            investmentAccountsHolder.setProducts(investmentAccounts);
            responseBody.setInvestmentAccounts(investmentAccountsHolder);
        }

        if (currentAccounts != null && !currentAccounts.isEmpty()) {
            CurrentAccounts currentAccountsHolder = new CurrentAccounts();
            currentAccountsHolder.setName(CURRENT_ACCOUNTS);
            currentAccountsHolder.setProducts(currentAccounts);
            responseBody.setCurrentAccounts(currentAccountsHolder);
        }
        return responseBody;
    }

    private Client getClientById() throws ApiException {
        return clientsApi.getById(InternalTokenClaimsUtil.getAccountHolderId(), FILTER_DETAILS_LEVEL_FULL);
    }

}