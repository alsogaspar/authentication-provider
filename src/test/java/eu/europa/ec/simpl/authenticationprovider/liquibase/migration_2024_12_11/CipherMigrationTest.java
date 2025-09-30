package eu.europa.ec.simpl.authenticationprovider.liquibase.migration_2024_12_11;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CipherMigrationTest {

    public static final String SECRET_KEY = "Uj2lLjQjLl45+oBACICQWrJp0KwUoPdVROEWI/OlY3g=";

    @Mock
    private Database database;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private JdbcConnection jdbcConnection;

    @Spy
    private CipherMigration cipherMigration;

    @Test
    public void executeTest_success() throws Exception {
        prepareCipherMigration();
        cipherMigration.execute(database);
        verify(preparedStatement).setBytes(eq(1), notNull());
        verify(preparedStatement).setBytes(eq(2), notNull());
        verify(preparedStatement).setObject(eq(3), eq("junit-id-1"));
        verify(preparedStatement, atLeastOnce()).executeUpdate();
    }

    @Test
    public void getConfirmationMessageTest_axpectedNoThrow() {
        assertDoesNotThrow(() -> cipherMigration.getConfirmationMessage());
    }

    @Test
    public void setFileOpenerTest_axpectedNoThrow() {
        assertDoesNotThrow(() -> cipherMigration.setFileOpener(null));
    }

    @Test
    public void setUpTest_axpectedNoThrow() {
        assertDoesNotThrow(() -> cipherMigration.setUp());
    }

    @Test
    public void validateTest_axpectedNoThrow() {
        assertDoesNotThrow(() -> cipherMigration.validate(null));
    }

    private void prepareCipherMigration() throws Exception {

        // echo -n "Hello World!" | openssl enc -aes-256-ecb -K
        // 523DA52E34232E5E39FA80400880905AB269D0AC14A0F75544E11623F3A56378 -nosalt  | base64
        var originalMessage = Base64.getDecoder().decode("/tqMAbBPNhJGeXvYdsFaDg==");

        when(database.getConnection()).thenReturn(jdbcConnection);
        when(jdbcConnection.getUnderlyingConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getObject("id")).thenReturn("junit-id-1");
        when(resultSet.getBytes("public_key")).thenReturn(originalMessage);
        when(resultSet.getBytes("private_key")).thenReturn(originalMessage);
        doReturn(SECRET_KEY).when(cipherMigration).getBase64SecretKeyFromEnv();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }
}
