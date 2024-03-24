package com.oxygen.task.controllers;

import com.oxygen.task.dtos.InvoiceDto;
import com.oxygen.task.dtos.InvoiceRequest;
import com.oxygen.task.dtos.InvoiceUrlRequest;
import com.oxygen.task.dtos.PaymentLink;
import com.oxygen.task.services.interfaces.InvoiceService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<InvoiceDto> createInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        InvoiceDto invoiceDto = invoiceService.generateInvoice(invoiceRequest);
        return new ResponseEntity<>(invoiceDto, HttpStatus.CREATED);
    }


    @PostMapping(value = "/generate-invoice-link", produces = "application/json")
    public ResponseEntity<InvoiceDto> generateInvoiceLink(@RequestBody InvoiceUrlRequest invoiceUrlRequest) {
        InvoiceDto invoiceDto = invoiceService.generateInvoiceDetailsLink(invoiceUrlRequest);
        return new ResponseEntity<>(invoiceDto, HttpStatus.OK);
    }


    @GetMapping(value = "/view-invoice/{merchantId}/{invoiceId}", produces = "text/html")
    public ResponseEntity<String> viewInvoice(@PathVariable(value = "merchantId") String merchantId,
                                    @PathVariable(value = "invoiceId") String invoiceId) {
        return new ResponseEntity<>(invoiceService.viewInvoice(merchantId, invoiceId), HttpStatus.OK);
    }


    @PostMapping(value = "/payment-link", produces = "application/json")
    public ResponseEntity<PaymentLink> paymentLink(@RequestParam(value = "invoiceId") String invoiceId) {
        return new ResponseEntity<>(invoiceService.generatePaymentLink(invoiceId), HttpStatus.OK);
    }
}
