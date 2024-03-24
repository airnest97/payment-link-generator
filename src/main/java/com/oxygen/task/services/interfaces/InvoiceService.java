package com.oxygen.task.services.interfaces;

import com.oxygen.task.dtos.*;


public interface InvoiceService {

    InvoiceDto generateInvoice(InvoiceRequest invoiceRequest);
    InvoiceDto generateInvoiceDetailsLink(InvoiceUrlRequest invoiceUrlRequest);
    String viewInvoice(String merchantId, String invoiceId);
    PaymentLink generatePaymentLink(String invoiceId);
}
