package assara.group.ossaraanalytics.security.service;

import assara.group.ossaraanalytics.exception.response.AuthenticationResponse;
import assara.group.ossaraanalytics.security.dto.*;

import java.util.List;
import java.util.UUID;

public interface UserService {

    AuthenticationResponse authenticate(LoginDTO loginDTO);
    UserDTO saveUser(UserDTO userDTO);
    List<UserRoleReponse> getAllUsers();
    UserRoleReponse getUserById(Long id);
    UserDTO updateUser(UserDTO userDTO, UUID id);
    void deleteUserById(UUID id);
    UserDTO updatePassword(UUID id, PasswordDTO passwordDTO);
    List<HistoryReponse> getAllHistory();
    List<RoleDTO> getAllRoles();

}
