package br.com.microservices.choreography.orderservice.core.enums;

public enum SagaStatusEnum {
    SUCCESS,
    ROLLBACK_PENDING,
    FAIL
}
