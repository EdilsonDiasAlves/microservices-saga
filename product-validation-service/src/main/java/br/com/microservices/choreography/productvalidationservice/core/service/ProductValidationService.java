package br.com.microservices.choreography.productvalidationservice.core.service;

import br.com.microservices.choreography.productvalidationservice.config.exception.ValidationException;
import br.com.microservices.choreography.productvalidationservice.core.dto.Event;
import br.com.microservices.choreography.productvalidationservice.core.dto.History;
import br.com.microservices.choreography.productvalidationservice.core.dto.OrderProducts;
import br.com.microservices.choreography.productvalidationservice.core.model.Validation;
import br.com.microservices.choreography.productvalidationservice.core.repository.ProductRepository;
import br.com.microservices.choreography.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.choreography.productvalidationservice.core.saga.SagaExecutionController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.choreography.productvalidationservice.core.enums.SagaStatusEnum.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@AllArgsConstructor
@Service
public class ProductValidationService {

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;
    private final SagaExecutionController sagaExecutionController;

    public void validateExistingProducts(Event event) {
        try {
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error trying to validate products: ", ex);
            handleFailCurrentNotExecuted(event, ex.getMessage());
        }
        sagaExecutionController.handleSaga(event);
    }

    private void checkCurrentValidation(Event event) {
        validateProductsInformed(event);
        if (validationRepository.existsByOrderIdAndTransactionId(
            event.getOrderId(),
            event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation!");
        }
        event.getPayload().getProducts().forEach(orderProducts -> {
            validateProductInOrderProducts(orderProducts);
            validateProductCode(orderProducts.getProduct().getCode());
        });
    }

    private static void validateProductsInformed(Event event) {
        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())) {
            throw new ValidationException("Product list is empty!");
        }
        if (isEmpty(event.getPayload().getId()) || isEmpty(event.getPayload().getTransactionId())) {
            throw new ValidationException("orderId or transactionId must be informed!");
        }
    }

    private void validateProductCode(String code) {
        if (!productRepository.existsByCode(code)) {
            throw new ValidationException("Product doesn't exists in database!");
        }
    }

    private void validateProductInOrderProducts(OrderProducts orderProducts) {
        if (isEmpty(orderProducts.getProduct()) || isEmpty(orderProducts.getProduct().getCode())) {
            throw new ValidationException("Product must be informed!");
        }
    }

    private void createValidation(Event event, boolean success) {
        var validation = Validation
            .builder()
            .orderId(event.getPayload().getId())
            .transactionId(event.getPayload().getTransactionId())
            .success(success)
            .build();
        validationRepository.save(validation);
    }

    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Products are validated successfully!");
    }

    private void addHistory(Event event, String message) {
        var history = History
            .builder()
            .source(event.getSource())
            .status(event.getStatus())
            .message(message)
            .createdAt(LocalDateTime.now())
            .build();
        event.addToHistory(history);
    }

    private void handleFailCurrentNotExecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to validate products: " + message);
    }

    public void rollbackEvent(Event event) {
        changeValidationToFail(event);
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Rollback executed on product-validation!");
        sagaExecutionController.handleSaga(event);
    }

    public void changeValidationToFail(Event event) {
        validationRepository
            .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
            .ifPresentOrElse( validation -> {
                validation.setSuccess(false);
                validationRepository.save(validation);
            },
            () -> createValidation(event, false));
    }
}
