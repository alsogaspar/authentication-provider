package eu.europa.ec.simpl.authenticationprovider.configurations.mtls;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.europa.ec.simpl.common.exchanges.mtls.AuthorityExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;

@ExtendWith(MockitoExtension.class)
public class MtlsScopeTest {

    @Mock
    private AuthorityExchange authorityExchange;

    @Test
    public void get_whenAuthorityExchangeIsNull_thenReturnNewExhange() {
        var scope = new MtlsScope();
        var objectFactory = mock(ObjectFactory.class);
        when(objectFactory.getObject()).thenReturn(authorityExchange);
        var exchange = scope.get("junit-name", objectFactory);
        assertThat(exchange).isEqualTo(authorityExchange);
    }

    @Test
    public void removeTest() {
        var scope = new MtlsScope();
        assertDoesNotThrow(() -> scope.remove("junit-name"));
    }

    @Test
    public void getConversationIdTest() {
        var scope = new MtlsScope();
        assertDoesNotThrow(() -> scope.getConversationId());
    }

    @Test
    public void resolveContextualObjectTest() {
        var scope = new MtlsScope();
        assertDoesNotThrow(() -> scope.resolveContextualObject("junit-key"));
    }

    @Test
    public void registerDestructionCallbackTest() {
        var scope = new MtlsScope();
        assertDoesNotThrow(() -> scope.invalidEvent());
    }
}
