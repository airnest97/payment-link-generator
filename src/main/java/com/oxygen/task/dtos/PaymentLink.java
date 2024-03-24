package com.oxygen.task.dtos;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentLink {
    private String paymentUrl;
    private String transactionReference;
}
