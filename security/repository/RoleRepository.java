package assara.group.ossaraanalytics.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import assara.group.ossaraanalytics.security.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
