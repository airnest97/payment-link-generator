package com.oxygen.task.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class InvoiceDto {
    private long invoiceId;
    private long merchantId;
    private String url;
}
