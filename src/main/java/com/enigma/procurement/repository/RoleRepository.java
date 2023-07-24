
package com.enigma.procurement.repository;

import com.enigma.procurement.entity.Role;
import com.enigma.procurement.entity.constraint.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByRole(ERole role);

}
