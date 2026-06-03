package assara.group.ossaraanalytics.security.dto;

public class PasswordDTO {

    private String currentPassword;
    private String newPassword;
    private Long userId;

    public PasswordDTO() {
    }

    public PasswordDTO(String currentPassword, String newPassword, Long userId) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.userId = userId;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
