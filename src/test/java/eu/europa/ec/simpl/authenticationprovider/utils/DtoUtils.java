package eu.europa.ec.simpl.authenticationprovider.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.simpl.common.exceptions.RuntimeWrapperException;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DtoUtils {
    public static String json(Object dto) {
        var om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        try {
            return om.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeWrapperException(e);
        }
    }

    public static boolean areJsonEquals(Object a, Object b) {
        var jsonA = json(a);
        var jsonB = json(b);
        var typeA = Optional.of(a).map(Object::getClass).map(Class::getName).orElse(null);
        var typeB = Optional.of(b).map(Object::getClass).map(Class::getName).orElse(null);
        var result = jsonA.equals(jsonB);
        log.info("Type A: {} ", typeA);
        log.info("Type B: {} ", typeB);
        log.info("JSON A: {}", jsonA);
        log.info("JSON B: {}", jsonB);
        log.info("JSON compare result: {}", result);
        return result;
    }
}
