package assara.group.ossaraanalytics.security.dto;


import java.util.UUID;


public class UserDTO {

    private String fullName;
    private String username;
    private String password;
    private String roles;
    private boolean enable;
    private Long ministere;
    private String direction;
    private UUID publicId;

    public UserDTO() {
    }

    public UserDTO(String fullName, String username, String password, String roles, boolean enable, Long ministere, String direction, UUID publicId) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.enable = enable;
        this.ministere = ministere;
        this.direction = direction;
        this.publicId = publicId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Long getMinistere() {
        return ministere;
    }

    public void setMinistere(Long ministere) {
        this.ministere = ministere;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }
}
