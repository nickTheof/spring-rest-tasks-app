package gr.aueb.cf.springtaskrest.authentication;

import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.springtaskrest.dto.AuthenticationRequestDTO;
import gr.aueb.cf.springtaskrest.dto.AuthenticationResponseDTO;
import gr.aueb.cf.springtaskrest.model.User;
import gr.aueb.cf.springtaskrest.repository.UserRepository;
import gr.aueb.cf.springtaskrest.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO dto)
            throws AppObjectNotAuthorizedException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppObjectNotAuthorizedException("User", "User not authorized"));

        String token = jwtService.generateToken(authentication.getName(), user.getRole().name());
        return new AuthenticationResponseDTO(token);
    }

}
