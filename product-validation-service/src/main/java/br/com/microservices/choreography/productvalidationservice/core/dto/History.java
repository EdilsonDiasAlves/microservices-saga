package br.com.microservices.choreography.productvalidationservice.core.dto;

import br.com.microservices.choreography.productvalidationservice.core.enums.SagaStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private String source;
    private SagaStatusEnum status;
    private String message;
    private LocalDateTime createdAt;
}
