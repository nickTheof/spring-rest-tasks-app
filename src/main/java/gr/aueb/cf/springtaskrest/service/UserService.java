package gr.aueb.cf.springtaskrest.service;

import gr.aueb.cf.springtaskrest.authentication.AuthenticationService;
import gr.aueb.cf.springtaskrest.core.enums.Role;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.core.filters.UserFilters;
import gr.aueb.cf.springtaskrest.core.specifications.UserSpecification;
import gr.aueb.cf.springtaskrest.dto.*;
import gr.aueb.cf.springtaskrest.mapper.Mapper;
import gr.aueb.cf.springtaskrest.model.User;
import gr.aueb.cf.springtaskrest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final AuthenticationService authenticationService;

    @Override
    public UserReadOnlyDTO findByUsername(String username) throws AppObjectNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + username + " not found"));
        return mapper.mapToUserReadOnly(user);
    }

    @Override
    public UserReadOnlyDTO findByUuid(String uuid) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with uuid " + uuid + " not found"));
        return mapper.mapToUserReadOnly(user);
    }

    @Override
    public Paginated<UserReadOnlyDTO> getUsersFilteredPaginated(UserFiltersDTO filters) {
        UserFilters userFilters = mapper.mapToUserFilters(filters);
        var filtered = userRepository.findAll(getSpecsFromFilters(userFilters), userFilters.getPageable());
        return new Paginated<>(filtered.map(mapper::mapToUserReadOnly));
    }

    @Override
    public Page<UserReadOnlyDTO> getUsersPaginated(int page, int pageSize) {
        return userRepository.findAll(PageRequest.of(page, pageSize)).map(mapper::mapToUserReadOnly);
    }

    @Override
    public List<UserReadOnlyDTO> getUsersFiltered(UserFiltersDTO filters) {
        UserFilters userFilters = mapper.mapToUserFilters(filters);
        return userRepository.findAll(getSpecsFromFilters(userFilters)).stream().map(mapper::mapToUserReadOnly).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = {AppObjectAlreadyExistsException.class})
    @Override
    public UserReadOnlyDTO saveUser(UserInsertDTO dto) throws AppObjectAlreadyExistsException {
        if (userRepository.findByUsername(dto.username()).isPresent()) throw new AppObjectAlreadyExistsException("User", "User with username " + dto.username() + " already exists");
        User user = mapper.mapToUser(dto);
        User savedUser = userRepository.save(user);
        return mapper.mapToUserReadOnly(savedUser);
    }

    @Transactional(rollbackFor = {AppObjectAlreadyExistsException.class})
    @Override
    public UserReadOnlyDTO registerUser(UserRegisterDTO dto) throws AppObjectAlreadyExistsException {
        if (userRepository.findByUsername(dto.username()).isPresent()) throw new AppObjectAlreadyExistsException("User", "User with username " + dto.username() + " already exists");
        User user = mapper.mapToUser(dto);
        User savedUser = userRepository.save(user);
        return mapper.mapToUserReadOnly(savedUser);
    }


    @Transactional(rollbackFor = {AppObjectAlreadyExistsException.class, AppObjectNotFoundException.class})
    @Override
    public UserReadOnlyDTO updateUser(String uuid, UserUpdateDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExistsException {
        User fetchedUser = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + uuid + " not found"));
        Optional<User> optionalUser = userRepository.findByUsername(dto.username());
        if (optionalUser.isPresent() && !optionalUser.get().getUuid().equals(fetchedUser.getUuid())) {
            throw new AppObjectAlreadyExistsException("User", "User with username " + dto.username() + " already exists");
        }
        User toUpdate = mapper.mapToUser(dto, fetchedUser);
        User updatedUser = userRepository.save(toUpdate);
        return mapper.mapToUserReadOnly(updatedUser);
    }

    @Transactional(rollbackFor = {AppObjectNotFoundException.class})
    @Override
    public void reverseUserStatusActivity(String uuid) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + uuid + " not found"));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }


    @Transactional(rollbackFor = {AppObjectNotFoundException.class})
    @Override
    public void deleteUser(String uuid) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User with username " + uuid + " not found"));
        userRepository.delete(user);
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Transactional(rollbackFor = {AppObjectNotFoundException.class, AppObjectNotAuthorizedException.class})
    @Override
    public void changeUserPassword(String username, ChangePasswordDTO dto) throws AppObjectNotFoundException, AppObjectNotAuthorizedException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + username + " not found"));
        boolean isOldPasswordValid = authenticationService.isPasswordValid(username, dto.oldPassword());
        if (!isOldPasswordValid) {
            throw new AppObjectNotAuthorizedException("User", "User " + username + " not authorized");
        }
        UserUpdateDTO updateDTO = new UserUpdateDTO(dto.newPassword());
        User updatedUser = mapper.mapToUser(updateDTO, user);
        userRepository.save(updatedUser);
    }

    private Specification<User> getSpecsFromFilters(UserFilters filters) {
        Specification<User> spec = (root, query, builder) -> null;
        if (filters.getUuid() != null) {
            spec = spec.and(UserSpecification.usersFieldLike("uuid", filters.getUuid()));
        }
        if (filters.getUsername() != null) {
            spec = spec.and(UserSpecification.usersFieldLike("username", filters.getUsername()));
        }
        if (filters.getRole() != null) {
            spec = spec.and(UserSpecification.usersRoleIs(filters.getRole()));
        }
        if (filters.getActive() != null) {
            spec = spec.and(UserSpecification.usersStatusIs(filters.getActive()));
        }
        return spec;
    }
}
