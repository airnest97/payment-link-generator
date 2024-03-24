package com.oxygen.task.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygen.task.controllers.InvoiceController;
import com.oxygen.task.dtos.*;
import com.oxygen.task.services.interfaces.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class InvoiceControllerTest {
    private MockMvc mockMvc;
    @Mock
    private InvoiceService invoiceService;
    @InjectMocks
    private InvoiceController invoiceController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(invoiceController).build();
    }


    @Test
    public void createInvoice() throws Exception {
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setMerchantName("A-Z Logistics");
        invoiceRequest.setMerchantAddress("Lagos");
        invoiceRequest.setMerchantEmail("a2z@gmail.com");
        invoiceRequest.setProductDetails(List.of(
                new ProductDetails("Slippers", BigDecimal.valueOf(1000), 2),
                new ProductDetails("Shoes", BigDecimal.valueOf(2000), 3))
        );

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setMerchantId(1L);
        invoiceDto.setInvoiceId(2L);

        when(invoiceService.generateInvoice(any(InvoiceRequest.class))).thenReturn(invoiceDto);

        mockMvc.perform(post("/api/v1/invoice/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.invoiceId").value(2L))
                .andExpect(jsonPath("$.merchantId").value(1L));
    }


    @Test
    public void generateInvoiceLink() throws Exception {
        InvoiceUrlRequest invoiceRequest = new InvoiceUrlRequest();
        invoiceRequest.setMerchantId(1L);
        invoiceRequest.setInvoiceId(2L);

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setUrl("http://localhost:6040/api/v1/invoice/view-invoice/1/2");

        when(invoiceService.generateInvoiceDetailsLink(any(InvoiceUrlRequest.class))).thenReturn(invoiceDto);

        mockMvc.perform(post("/api/v1/invoice/generate-invoice-link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invoiceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url").value("http://localhost:6040/api/v1/invoice/view-invoice/1/2"));
    }



    @Test
    public void viewInvoice() throws Exception {
        when(invoiceService.viewInvoice(any(String.class), any(String.class))).thenReturn("invoice.html");

        mockMvc.perform(get("/api/v1/invoice/view-invoice/{merchantId}/{invoiceId}", "1L", "2L"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string("invoice.html"));
    }


    @Test
    public void paymentLink() throws Exception {
        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setPaymentUrl("http://dummy-payment-link");
        paymentLink.setTransactionReference("dummy-transaction-reference");

        when(invoiceService.generatePaymentLink(any(String.class))).thenReturn(paymentLink);

        mockMvc.perform(post("/api/v1/invoice/payment-link")
                        .param("invoiceId", "1L"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.paymentUrl").value("http://dummy-payment-link"));
    }
}
