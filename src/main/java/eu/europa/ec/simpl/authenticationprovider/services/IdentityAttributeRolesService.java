package eu.europa.ec.simpl.authenticationprovider.services;

import java.util.List;

public interface IdentityAttributeRolesService {
    void updateAssignments(List<String> idaCodes);
}
