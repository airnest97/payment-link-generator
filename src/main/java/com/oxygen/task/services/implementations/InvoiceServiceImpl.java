package com.oxygen.task.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygen.task.config.Properties;
import com.oxygen.task.dtos.*;
import com.oxygen.task.exception.GenericException;
import com.oxygen.task.models.Invoice;
import com.oxygen.task.models.Merchant;
import com.oxygen.task.repositories.InvoiceRepository;
import com.oxygen.task.repositories.MerchantRepository;
import com.oxygen.task.services.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

import static com.oxygen.task.utils.Constants.NOT_FOUND;


@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    private final MerchantRepository merchantRepository;
    private final InvoiceRepository invoiceRepository;
    private final ObjectMapper objectMapper;
    private final TemplateEngine templateEngine;
    private final RestTemplate restTemplate;
    private final Context context;
    private final Properties properties;
    private static final String INVOICE_EVENT = "INVOICE_SERVICE";



    @Override
    public InvoiceDto generateInvoice(InvoiceRequest invoiceRequest) {

        Merchant merchant = new Merchant();
        Invoice invoice = new Invoice();
        InvoiceDto invoiceDto = new InvoiceDto();
        try {
            Optional<Merchant> optionalMerchant = merchantRepository.findByEmail(invoiceRequest.getMerchantEmail());
            if (optionalMerchant.isEmpty()) {
                merchant.setName(invoiceRequest.getMerchantName());
                merchant.setAddress(invoiceRequest.getMerchantAddress());
                merchant.setEmail(invoiceRequest.getMerchantEmail());
                merchant = merchantRepository.save(merchant);
            } else {
                merchant = optionalMerchant.get();
            }
            invoice.setMerchantId(merchant.getId());

            String productDetails = getProductDetails(invoiceRequest);
            invoice.setProductDetails(productDetails);

            BigDecimal totalAmount = getProductTotal(invoiceRequest);
            invoice.setTotalAmount(totalAmount);

            invoice = invoiceRepository.save(invoice);
            invoiceDto.setInvoiceId(invoice.getId());
            invoiceDto.setMerchantId(merchant.getId());
            return invoiceDto;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new GenericException("An error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public InvoiceDto generateInvoiceDetailsLink(InvoiceUrlRequest invoiceUrlRequest) {
        Merchant merchant = merchantRepository.findById(invoiceUrlRequest.getMerchantId()).orElseThrow(() -> new GenericException(NOT_FOUND, HttpStatus.NOT_FOUND));
        log.info("Event={}, author={}", INVOICE_EVENT, merchant);
        Invoice invoice = invoiceRepository.findById(invoiceUrlRequest.getInvoiceId()).orElseThrow(() -> new GenericException(NOT_FOUND, HttpStatus.NOT_FOUND));
        log.info("Event={}, author={}", INVOICE_EVENT, invoice);
        String merchantId = String.valueOf(merchant.getId());
        String invoiceId = String.valueOf(invoice.getId());

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setUrl(properties.getInvoiceRedirectUrl()+merchantId+"/"+invoiceId);
        return invoiceDto;
    }

    @Override
    public String viewInvoice(String merchantId, String invoiceId) {
        Merchant merchant = merchantRepository.findById(Long.valueOf(merchantId)).orElseThrow(() -> new GenericException(NOT_FOUND, HttpStatus.NOT_FOUND));
        Invoice invoice = invoiceRepository.findById(Long.valueOf(invoiceId)).orElseThrow(() -> new GenericException(NOT_FOUND, HttpStatus.NOT_FOUND));

        String productDetails = invoice.getProductDetails();

        try {
            List<ProductDetails> productDetails1 = objectMapper.readValue(productDetails, new TypeReference<>() {
            });
            context.setVariable("product", productDetails1);
            context.setVariable("merchant", merchant);
            context.setVariable("invoice", invoice);
            return templateEngine.process("invoice.html", context);
        } catch (Exception ex){
            log.error(ex.getMessage());
            throw new GenericException("An error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public PaymentLink generatePaymentLink(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(Long.valueOf(invoiceId)).orElseThrow(() -> new GenericException(NOT_FOUND, HttpStatus.NOT_FOUND));
        invoice.setTransactionReference(generateReference());

        invoice = invoiceRepository.save(invoice);

        Merchant merchant = merchantRepository.findById(invoice.getMerchantId()).orElseThrow(() -> new GenericException(NOT_FOUND, HttpStatus.NOT_FOUND));

        Map<String, Object> request = new HashMap<>();
        request.put("email", merchant.getEmail());
        request.put("amount", invoice.getTotalAmount());
        request.put("transactionRef", invoice.getTransactionReference());

        PaymentResponse paymentResponse = paystackInitialize(request);

        return PaymentLink.builder()
                .paymentUrl(paymentResponse.getData().authorizationUrl())
                .transactionReference(paymentResponse.getData().reference())
                .build();
    }


    private PaymentResponse paystackInitialize(Map<String, Object> request) {
        try {
            PaymentResponse response = restTemplate.exchange(properties.getPaystackUrl(), HttpMethod.POST,
                    new HttpEntity<>(request, getHeaders()), PaymentResponse.class).getBody();
            log.info("paystack payment {}, {}", request, response);
            return response;
        } catch (HttpClientErrorException ex) {
            log.error("Exception occurred while making payment call {}", request, ex);
        }
        throw new GenericException("Failed to Initiate Payment", HttpStatus.SERVICE_UNAVAILABLE);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(properties.getPaystackSecretKey());
        return headers;
    }

    private static String generateReference(){
        return UUID.randomUUID() +"-"+ new SecureRandom().nextInt(1000) + System.currentTimeMillis();
    }

    private String getProductDetails(InvoiceRequest invoiceRequest) throws JsonProcessingException {
        return objectMapper.writeValueAsString(invoiceRequest.getProductDetails());
    }

    private static BigDecimal getProductTotal(InvoiceRequest invoiceRequest) {
        return invoiceRequest.getProductDetails().stream().map(productDetails1 -> productDetails1.getProductPrice()
                .multiply(BigDecimal.valueOf(productDetails1.getProductQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

