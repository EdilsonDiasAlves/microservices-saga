package br.com.microservices.orchestrated.productvalidationservice.core.service;

import br.com.microservices.orchestrated.productvalidationservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.Event;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.History;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.OrderProducts;
import br.com.microservices.orchestrated.productvalidationservice.core.enums.SagaStatusEnum;
import br.com.microservices.orchestrated.productvalidationservice.core.model.Validation;
import br.com.microservices.orchestrated.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ProductRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@AllArgsConstructor
@Service
public class ProductValidationService {

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE ";

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;

    public void validateExistingProducts(Event event) {
        try {
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error trying to validate products: ", ex);
//            TODO: IMPLEMENT METHOD
//            handleFailCurrentNotExecuted(event, ex.getMessage());
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void checkCurrentValidation(Event event) {
        validateProductsInformed(event);
        if (validationRepository.existsByOrderIdAndTransactionId(
            event.getOrderId(),
            event.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation!");
        }
        event.getOrder().getProducts().forEach(orderProducts -> {
            validateProductInOrderProducts(orderProducts);
            validateProductCode(orderProducts.getProduct().getCode());
        });
    }

    private static void validateProductsInformed(Event event) {
        if (isEmpty(event.getOrder()) || isEmpty(event.getOrder().getProducts())) {
            throw new ValidationException("Product list is empty!");
        }
        if (isEmpty(event.getOrder().getId()) || isEmpty(event.getOrder().getTransactionId())) {
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
            .orderId(event.getOrder().getId())
            .success(success)
            .build();
        validationRepository.save(validation);
    }

    private void handleSuccess(Event event) {
        event.setStatus(SagaStatusEnum.SUCCESS);
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


}