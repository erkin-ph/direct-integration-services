package com.backbase.academy.peachtree.service;

import com.backbase.academy.peachtree.service.ProductSummaryService;
import com.mambu.sdk.api.v2.ClientsApi;
import com.mambu.sdk.api.v2.DepositAccountsApi;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
public class ProductSummaryServiceTest {

    @InjectMocks
    private ProductSummaryService productSummaryService;
    @Mock
    private ClientsApi clientsApi;
    @Mock
    private DepositAccountsApi depositAccountsApi;

    @Test
    @Ignore
    public void getProductSummary() {


        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


    }
}