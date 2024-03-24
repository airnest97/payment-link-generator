package com.oxygen.task.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygen.task.dtos.*;
import com.oxygen.task.models.Invoice;
import com.oxygen.task.models.Merchant;
import com.oxygen.task.repositories.InvoiceRepository;
import com.oxygen.task.repositories.MerchantRepository;
import com.oxygen.task.services.interfaces.InvoiceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class InvoiceServiceTest {
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private ObjectMapper objectMapper;
    private InvoiceRequest invoiceRequest;
    private InvoiceUrlRequest invoiceUrlRequest;

    @BeforeEach
    void setUp() {
        invoiceRequest = new InvoiceRequest();
        invoiceRequest.setMerchantName("Test name");
        invoiceRequest.setMerchantAddress("test address");
        invoiceRequest.setMerchantEmail("a-z@gmail.com");
        invoiceRequest.setProductDetails(List.of(
                new ProductDetails("Slippers", BigDecimal.valueOf(1000), 2),
                new ProductDetails("Shoes", BigDecimal.valueOf(2000), 3))
        );
    }

    @AfterEach
    void tearDown() {
        invoiceRepository.deleteAll();
        merchantRepository.deleteAll();
    }



    @Test
    void testThatInvoiceCanBeGenerated() throws JsonProcessingException {
        Merchant merchant = new Merchant();
        merchant.setName(invoiceRequest.getMerchantName());
        merchant.setEmail(invoiceRequest.getMerchantEmail());
        merchant.setAddress(invoiceRequest.getMerchantAddress());
        merchant = merchantRepository.save(merchant);

        Invoice invoice = new Invoice();
        invoice.setMerchantId(merchant.getId());
        String productDetails = getProductDetails(invoiceRequest);
        invoice.setProductDetails(productDetails);
        BigDecimal productTotal = getProductTotal(invoiceRequest);
        invoice.setTotalAmount(productTotal);
        invoiceRepository.save(invoice);

        assertNotNull(invoice);
        assertThat(merchant.getEmail()).isEqualTo("a-z@gmail.com");
        assertThat(invoice.getTotalAmount()).isEqualTo(BigDecimal.valueOf(8000));
    }

    @Test
    void testThatInvoiceAndMerchantIdCanBeGotten(){
        InvoiceDto invoiceDto = invoiceService.generateInvoice(invoiceRequest);
        assertNotNull(invoiceDto);
    }

    @Test
    void testThatInvoiceLinkCanBeGenerated(){
        InvoiceDto invoiceDto1 = invoiceService.generateInvoice(invoiceRequest);
        invoiceUrlRequest = new InvoiceUrlRequest();
        invoiceUrlRequest.setInvoiceId(invoiceDto1.getInvoiceId());
        invoiceUrlRequest.setMerchantId(invoiceDto1.getMerchantId());
        InvoiceDto invoiceDto = invoiceService.generateInvoiceDetailsLink(invoiceUrlRequest);
        assertNotNull(invoiceDto);
        assertThat(invoiceDto.getUrl(), instanceOf(String.class));
    }


    @Test
    void testThatInvoiceCanBeViewed(){
        InvoiceDto invoiceDto = invoiceService.generateInvoice(invoiceRequest);
        invoiceUrlRequest = new InvoiceUrlRequest();
        invoiceUrlRequest.setInvoiceId(invoiceDto.getInvoiceId());
        invoiceUrlRequest.setMerchantId(invoiceDto.getMerchantId());
        InvoiceDto invoiceDto1 = invoiceService.generateInvoiceDetailsLink(invoiceUrlRequest);
        assertNotNull(invoiceDto1);
        assertThat(invoiceDto1.getUrl(), instanceOf(String.class));

        String invoice = invoiceService.viewInvoice(String.valueOf(invoiceDto.getMerchantId()), String.valueOf(invoiceDto.getInvoiceId()));
        assertNotNull(invoice);
        assertThat(invoiceDto1.getUrl(), instanceOf(String.class));
    }

    @Test
    void testThatPaymentLinkCanBegenerated(){
        InvoiceDto invoiceDto = invoiceService.generateInvoice(invoiceRequest);
        invoiceUrlRequest = new InvoiceUrlRequest();
        invoiceUrlRequest.setInvoiceId(invoiceDto.getInvoiceId());
        invoiceUrlRequest.setMerchantId(invoiceDto.getMerchantId());
        InvoiceDto invoiceDto1 = invoiceService.generateInvoiceDetailsLink(invoiceUrlRequest);
        assertNotNull(invoiceDto1);
        assertThat(invoiceDto1.getUrl(), instanceOf(String.class));

        String invoice = invoiceService.viewInvoice(String.valueOf(invoiceDto.getMerchantId()), String.valueOf(invoiceDto.getInvoiceId()));
        assertNotNull(invoice);
        assertThat(invoiceDto1.getUrl(), instanceOf(String.class));

        PaymentLink paymentLink = invoiceService.generatePaymentLink(String.valueOf(invoiceDto.getInvoiceId()));
        assertNotNull(paymentLink);
        assertThat(paymentLink.getPaymentUrl(), instanceOf(String.class));
    }

    private String getProductDetails(InvoiceRequest invoiceRequest) throws JsonProcessingException {
        return objectMapper.writeValueAsString(invoiceRequest.getProductDetails());
    }

    private static BigDecimal getProductTotal(InvoiceRequest invoiceRequest) {
        return invoiceRequest.getProductDetails().stream().map(productDetails1 -> productDetails1.getProductPrice()
                .multiply(BigDecimal.valueOf(productDetails1.getProductQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
