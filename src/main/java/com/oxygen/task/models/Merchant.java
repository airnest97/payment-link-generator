package com.oxygen.task.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "Merchant")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    @Email
    private String email;
    @CreationTimestamp
    private Date createdAt;
}
