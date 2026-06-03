package assara.group.ossaraanalytics.security.service;


import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import assara.group.ossaraanalytics.exception.AlreadyExistException;
import assara.group.ossaraanalytics.exception.ResourceNotFoundException;
import assara.group.ossaraanalytics.exception.response.AuthenticationResponse;
import assara.group.ossaraanalytics.security.UserDetailsImpl;
import assara.group.ossaraanalytics.security.dto.*;
import assara.group.ossaraanalytics.security.jwt.JwtUtils;
import assara.group.ossaraanalytics.security.mappers.UserMapper;
import assara.group.ossaraanalytics.security.model.History;
import assara.group.ossaraanalytics.security.model.User;
import assara.group.ossaraanalytics.security.repository.HistoryRepository;
import assara.group.ossaraanalytics.security.repository.RoleRepository;
import assara.group.ossaraanalytics.security.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final HistoryRepository historyRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, UserMapper userMapper, HistoryRepository historyRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.historyRepository = historyRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public AuthenticationResponse authenticate(LoginDTO loginDTO) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
                            loginDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities()
                                 .stream().map(item -> item.getAuthority()).collect(Collectors.toList());

            createHistory(userDetails.getId());

            return new AuthenticationResponse(token, userDetails.getId(), userDetails.getFullName(), userDetails.getUsername(), userDetails.getMinistere(), userDetails.getDirection(),roles);

        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Les paramètres de connexion sont incorrectes");
        }
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        checkIfUserExists(userDTO);
        User user = userMapper.mapToUser(userDTO);

       /* Optional<HealthCenter> healthCenter = healthCenterRepository.findById(userDTO.getHealthCenterId());
        if (healthCenter.isEmpty()) {
            throw new ResourceNotFoundException("HealthCenter Not Found");
        }*/

        user.setCodeDirection(user.getCodeDirection());
        user.setCodeMinistere(user.getCodeMinistere());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(userDTO.getRoles());

        Optional<User> user1 = userRepository.findByPublicId(userDTO.getPublicId());
        History history = new History();
        history.setName("Enregistrement de l'utilisateur " + userDTO.getFullName());
        history.setUser(user1.get());
        history.setDateHistory(new Date());

        historyRepository.save(history);

        User savedUser = userRepository.save(user);

        return userMapper.mapToUserDTO(savedUser);
    }

    @Override
    public List<UserRoleReponse> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(userMapper::mapToUserRoleDTO)
                .toList();
    }

    @Override
    public UserRoleReponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User is not exists with given id : "+id));

        return userMapper.mapToUserRoleDTO(user);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, UUID id) {
        User user = userRepository.findByPublicId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User is not exists with given id:" + id));

     /*   Optional<HealthCenter> healthCenter = healthCenterRepository.findById(userDTO.getHealthCenterId());
        if (healthCenter.isEmpty()) {
            throw new ResourceNotFoundException("HealthCenter Not Found");
        }*/

        user.setCodeDirection(user.getCodeDirection());
        user.setCodeMinistere(user.getCodeMinistere());
        user.setNom(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(userDTO.getRoles());

        Optional<User> userHistory = userRepository.findByPublicId(userDTO.getPublicId());
        History history = new History();
        history.setName("Modification de l'utilisateur " + userDTO.getFullName());
        history.setUser(userHistory.get());
        history.setDateHistory(new Date());

        historyRepository.save(history);

        User updateUser = userRepository.save(user);

        return userMapper.mapToUserDTO(updateUser);
    }

    @Override
    public void deleteUserById(UUID id) {
        Optional<User> optionalUser = userRepository.findByPublicId(id);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Utilisateur introuvable!");
        }

        User user = optionalUser.get();
        user.setEnable(false);
        userRepository.save(user);
    }

    @Override
    public UserDTO updatePassword(UUID id, PasswordDTO passwordDTO) {

        Optional<User> optionalUser = userRepository.findByPublicId(id);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("Utilisateur introuvable");
        }

        User user = optionalUser.get();
        if (passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Le mot de passe actuel ne correspond pas !");
        }

        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        User updatePassword = userRepository.save(user);

        Optional<User> userHistory = userRepository.findById(passwordDTO.getUserId());
        History history = new History();
        history.setName("Modification du mot de passe de l'utilisateur " + passwordDTO.getUserId());
        history.setUser(userHistory.get());
        history.setDateHistory(new Date());

        historyRepository.save(history);

        return userMapper.mapToUserDTO(updatePassword);
    }

    @Override
    public List<HistoryReponse> getAllHistory() {
        return historyRepository.findAllByOrderByDateHistoryDesc()
                .stream().map(userMapper::mapToHistoryReponse)
                .toList();
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(userMapper::mapToRoleDTO)
                .toList();
    }

    private void checkIfUserExists(UserDTO userDTO){
        if(userRepository.existsByUsername(userDTO.getUsername())) {
            throw new AlreadyExistException(String.format("Ce nom existe déjà !!!", userDTO.getUsername()));
        }
    }

    private History createHistory(UUID userId) {
        User user = userRepository.findByPublicId(userId).get();
        History history = new History();
        history.setName("Connexion de l'utilisateur " + user.getUsername());
        history.setUser(user);
        history.setDateHistory(new Date());

        return historyRepository.save(history);
    }
}
