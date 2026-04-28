package tech.wetech.admin3.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.wetech.admin3.sys.model.UserCredential;

import java.util.Optional;

/**
 * @author cjbi
 */
@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

  @Query("from UserCredential c where c.identifier=:identifier and c.identityType=:identityType")
  Optional<UserCredential> findCredential(String identifier, UserCredential.IdentityType identityType);

  @Query("from UserCredential c where c.user.id=:userId and c.identityType=:identityType")
  Optional<UserCredential> findByUserId(Long userId, UserCredential.IdentityType identityType);

}
