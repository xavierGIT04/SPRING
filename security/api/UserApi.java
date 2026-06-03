package assara.group.ossaraanalytics.security.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import assara.group.ossaraanalytics.exception.response.AuthenticationResponse;
import assara.group.ossaraanalytics.security.dto.*;
import assara.group.ossaraanalytics.security.repository.UserRepository;
import assara.group.ossaraanalytics.security.service.UserService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Gestion des utilisateurs", description = "Point d'entrée des utilisateurs")
@RestController
@RequestMapping("/api/v1")
public class UserApi {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserApi(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Authenticate",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé/invalide",
                            responseCode = "401"
                    )
                    ,
                    @ApiResponse(
                            description = "Point d'entré non trouvé",
                            responseCode = "404"
                    )
            }
    )
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody @Valid LoginDTO loginDTO) {
        return  ResponseEntity.ok(userService.authenticate(loginDTO));
    }

    @GetMapping("/role")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Liste des roles",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<List<RoleDTO>> getAllRole() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Save new user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<UserDTO> saveUsers(@RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.saveUser(userDTO), HttpStatus.CREATED);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Retrieve all user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<List<UserRoleReponse>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DISPENSATEUR')")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Retrieve a user by Id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<UserRoleReponse> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Update a user by Id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<UserDTO> updateUsers(@RequestBody UserDTO userDTO,@PathVariable("id")  UUID id){
        return ResponseEntity.ok(userService.updateUser(userDTO,id));
    }

    @PutMapping("/users/change_password/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DISPENSATEUR','MENTOR')")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Change password a user by Id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<UserDTO> updatePassword(@PathVariable("id") UUID id, @RequestBody PasswordDTO passwordDTO){
        return ResponseEntity.ok(userService.updatePassword(id,passwordDTO));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "History  all",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<List<HistoryReponse>> getAllHistory() {
        return ResponseEntity.ok(userService.getAllHistory());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            description = "Ce point de terminaison ne nécessite pas de JWT valide",
            summary = "Delete a user by Id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Jeton non autorisé / invalide",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<Void>  deleteUserById(@PathVariable("id") UUID id) {
        this.userService.deleteUserById(id);
        return ResponseEntity.status(204).build();
    }


}
