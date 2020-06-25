package com.ibm.garage.cpat.cp4i.MessageValidator;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.garage.cpat.cp4i.FinancialMessage.FinancialMessage;

@ApplicationScoped
public class MessageValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageValidator.class);
    
    @Incoming("pre-final-check")
    @Outgoing("post-final-check")
    @Broadcast
    public Flowable<FinancialMessage> checkValidation(FinancialMessage financialMessage) {

        FinancialMessage receivedMessage = financialMessage;

        LOGGER.info("Message received from topic = {}", receivedMessage);

        if (readyToPublish(receivedMessage)) {
            /*
            Since Business Validation is the last service along the "conveyor belt" we just change
            the boolean to false to indicate that it's finished.
            */
            return Flowable.just(receivedMessage);
        }

        else {
            return Flowable.empty();
        }
    }


    public boolean readyToPublish(FinancialMessage financialMessage) {
        // Returns true only if all the microservice flags are false. This means that all the
        // checks have passed and ready to be published to the REST API.
        return (!financialMessage.compliance_services && !financialMessage.technical_validation &&
                !financialMessage.schema_validation && !financialMessage.business_validation &&
                !financialMessage.trade_enrichment);
    }
}