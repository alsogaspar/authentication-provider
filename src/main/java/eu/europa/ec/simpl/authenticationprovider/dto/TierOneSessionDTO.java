package eu.europa.ec.simpl.authenticationprovider.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TierOneSessionDTO {
    @NotBlank
    private String jwt;
}
