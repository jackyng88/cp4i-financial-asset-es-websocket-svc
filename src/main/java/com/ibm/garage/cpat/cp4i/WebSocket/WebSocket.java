package com.ibm.garage.cpat.cp4i.WebSocket;

import javax.ws.rs.Path;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.garage.cpat.cp4i.FinancialMessage.FinancialMessage;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;

import org.eclipse.microprofile.reactive.messaging.Channel;
//import io.smallrye.reactive.messaging.annotations.Channel;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;


@ServerEndpoint("/validatedmessages")
@ApplicationScoped
public class WebSocket {

    @Inject @Channel("post-final-check") Flowable<FinancialMessage> finalizedMessages;
    //private Jsonb jsonb;
    ObjectMapper obj = new ObjectMapper(); 

    private static final Logger LOGGER = Logger.getLogger(WebSocket.class);

    private List<Session> sessions = new CopyOnWriteArrayList<>();
    private Disposable subscription;

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @PostConstruct
    public void subscribe() {
        subscription = finalizedMessages.subscribe(message -> sessions.forEach(session -> {
			try {
				write(session, message);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));
    }

    @PreDestroy
    public void cleanup() throws Exception {
        subscription.dispose();
        //jsonb.close();
    }

    private void write(Session session, FinancialMessage message) throws JsonProcessingException {
        session.getAsyncRemote().sendText(obj.writeValueAsString(message), result -> {
            if (result.getException() != null) {
                LOGGER.error("Unable to write message to web socket", result.getException());
            }
        });
    }
}
