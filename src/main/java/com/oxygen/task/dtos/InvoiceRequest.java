package com.oxygen.task.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {
    @NotBlank
    private String merchantName;
    @NotBlank
    private String merchantAddress;
    @NotBlank
    private String merchantEmail;
    @NotBlank
    private List<ProductDetails> productDetails;
}
