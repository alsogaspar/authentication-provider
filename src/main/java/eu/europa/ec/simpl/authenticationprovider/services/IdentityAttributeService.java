package eu.europa.ec.simpl.authenticationprovider.services;

import eu.europa.ec.simpl.authenticationprovider.filters.IdentityAttributeWithOwnershipFilter;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IdentityAttributeService {
    Page<IdentityAttributeWithOwnershipDTO> search(IdentityAttributeWithOwnershipFilter request, Pageable pageable);

    @Transactional
    void overwriteIdentityAttributes(List<IdentityAttributeWithOwnershipDTO> identityAttributes);

    @Transactional
    void updateAssignedIdentityAttributes(List<IdentityAttributeDTO> idaFromEphemeralProof);
}
