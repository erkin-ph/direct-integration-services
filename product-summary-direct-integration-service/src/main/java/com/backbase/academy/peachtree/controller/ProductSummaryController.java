package com.backbase.academy.peachtree.controller;

import static java.util.Collections.emptyList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import com.backbase.academy.peachtree.service.ProductSummaryService;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.ForbiddenException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.ArrangementsByBusinessFunctionGetResponseBody;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.ProductSummaryApi;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.ProductSummaryEntitlementsByLegalEntityIdGetResponseBody;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.ProductSummaryGetResponseBody;
import com.mambu.sdk.api.ApiException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/v2/productsummary", "/client-api/v2/productsummary"})
@Slf4j
@RequiredArgsConstructor
public class ProductSummaryController implements ProductSummaryApi {

    private final ProductSummaryService productSummaryService;

    @Override
    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ProductSummaryGetResponseBody getProductSummary(@RequestParam(required = false) Boolean debitAccount,
        @RequestParam(required = false) Boolean creditAccount,
        @RequestParam(required = false) Boolean maskIndicator,
        HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse)
        throws BadRequestException, ForbiddenException, InternalServerErrorException {

        try {
            return productSummaryService.getProductSummary();
        } catch (RuntimeException | ApiException e) {
            log.error("Unable to retrieve the transactions", e);
        }

        return new ProductSummaryGetResponseBody();
    }

    @Override
    public List<ProductSummaryEntitlementsByLegalEntityIdGetResponseBody> getProductSummaryEntitlementsByLegalEntityId(
        String[] strings, String[] strings1, String[] strings2, String s, Integer integer, String s1, Integer integer1,
        String s2, String s3, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
        throws BadRequestException, ForbiddenException, InternalServerErrorException {
        return emptyList();
    }

    @Override
    public List<ProductSummaryEntitlementsByLegalEntityIdGetResponseBody> getProductSummaryEntitlementsByLegalEntityId(
        String s, Integer integer, String s1, Integer integer1, String s2, String s3,
        HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
        throws BadRequestException, ForbiddenException, InternalServerErrorException {
        return emptyList();
    }

    @Override
    public List<ArrangementsByBusinessFunctionGetResponseBody> getArrangementsByBusinessFunction(Boolean aBoolean,
        Boolean aBoolean1, Boolean aBoolean2, Boolean aBoolean3, String s, String s1, String s2, Boolean aBoolean4,
        String s3, String[] strings, String s4, Boolean aBoolean5, String s5, Boolean aBoolean6, Boolean aBoolean7,
        Integer integer, String s6, Integer integer1, String s7, String s8, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse)
        throws BadRequestException, ForbiddenException, InternalServerErrorException {
        return emptyList();
    }

}