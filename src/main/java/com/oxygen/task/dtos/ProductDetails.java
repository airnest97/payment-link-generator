package com.oxygen.task.dtos;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails {
    private String productName;
    private BigDecimal productPrice;
    private int productQuantity;
}
