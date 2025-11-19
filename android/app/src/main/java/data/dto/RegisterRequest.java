package data.dto;

public class RegisterRequest {
    private String userName;
    private String email;
    private String phoneNumber;
    private String password;
    private String confirmPassword;

    public RegisterRequest(String userName, String email, String phoneNumber, String password, String confirmPassword) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
