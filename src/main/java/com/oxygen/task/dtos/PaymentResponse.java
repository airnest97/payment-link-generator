package com.oxygen.task.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    @JsonProperty("status")
    private boolean status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private PaystackDataResponse data;




    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaystackDataResponse (
            @JsonProperty("authorization_url")
            String authorizationUrl,
            @JsonProperty("access_code")
            String accessCode,
            @JsonProperty("reference")
            String reference
    ){
        @Override
        public String toString() {
            return "PaystackDataResponse{" +
                    "authorizationUrl='" + authorizationUrl + '\'' +
                    ", accessCode='" + accessCode + '\'' +
                    ", reference='" + reference + '\'' +
                    '}';
        }
    }
}
