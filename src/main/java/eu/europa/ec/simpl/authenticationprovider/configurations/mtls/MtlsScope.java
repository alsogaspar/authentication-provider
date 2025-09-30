package eu.europa.ec.simpl.authenticationprovider.configurations.mtls;

import eu.europa.ec.simpl.authenticationprovider.event.OnCredentialUpdateEvent;
import eu.europa.ec.simpl.common.exchanges.mtls.AuthorityExchange;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.event.EventListener;

@Log4j2
public class MtlsScope implements Scope {

    private AuthorityExchange authorityExchange;

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if (authorityExchange == null) {
            log.info("Building new mtls client");
            authorityExchange = (AuthorityExchange) objectFactory.getObject();
        }
        return authorityExchange;
    }

    @Override
    public Object remove(String name) {
        return null;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // Optional operation
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }

    @EventListener(OnCredentialUpdateEvent.class)
    public void invalidEvent() {
        log.info("Invalidate authority mtls client");
        authorityExchange = null;
    }
}
