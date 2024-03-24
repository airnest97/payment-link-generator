package com.oxygen.task.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Table(name = "Merchant")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long merchantId;
    private BigDecimal totalAmount;
    private String transactionReference;
    private String productDetails;
}
