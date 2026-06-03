package assara.group.ossaraanalytics.security.mappers;


import org.springframework.stereotype.Service;
import assara.group.ossaraanalytics.security.dto.HistoryReponse;
import assara.group.ossaraanalytics.security.dto.RoleDTO;
import assara.group.ossaraanalytics.security.dto.UserDTO;
import assara.group.ossaraanalytics.security.dto.UserRoleReponse;
import assara.group.ossaraanalytics.security.model.History;
import assara.group.ossaraanalytics.security.model.Role;
import assara.group.ossaraanalytics.security.model.User;

@Service
public class UserMapper {

    public User mapToUser(UserDTO userDTO) {
        User user = new User();
        user.setNom(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setEnable(userDTO.isEnable());
        return user;
    }

    public  UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFullName(user.getNom());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setEnable(user.isEnable());
        userDTO.setRoles(user.getRoles());

        return  userDTO;
    }
    public UserRoleReponse mapToUserRoleDTO(User user) {
        UserRoleReponse userRoleReponse = new UserRoleReponse();
        userRoleReponse.setId(user.getId());
        userRoleReponse.setFullName(user.getNom());
        userRoleReponse.setUsername(user.getUsername());
        userRoleReponse.setEnable(user.isEnable());

            userRoleReponse.setMinistere((long) user.getCodeMinistere());
            userRoleReponse.setDirection( user.getCodeDirection());


        userRoleReponse.setRoles(user.getRoles());

        return userRoleReponse;
    }

    public RoleDTO mapToRoleDTO(Role role) {

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(String.valueOf(role.getName()));

        return roleDTO;
    }
    public HistoryReponse mapToHistoryReponse(History history) {

        HistoryReponse historyReponse = new HistoryReponse();
        historyReponse.setId(history.getId());
        historyReponse.setFullName(history.getUser().getNom());
        historyReponse.setName(history.getName());
        historyReponse.setDateHistory(history.getDateHistory());

        return historyReponse;
    }
}
