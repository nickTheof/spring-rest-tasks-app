package gr.aueb.cf.springtaskrest.service;

import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.dto.*;
import gr.aueb.cf.springtaskrest.model.User;
import org.springframework.data.domain.Page;
import java.util.List;


public interface IUserService {
    UserReadOnlyDTO findByUsername(String username) throws AppObjectNotFoundException;
    UserReadOnlyDTO findByUuid(String uuid) throws AppObjectNotFoundException;
    Paginated<UserReadOnlyDTO> getUsersFilteredPaginated(UserFiltersDTO filters);
    Page<UserReadOnlyDTO> getUsersPaginated(int page, int pageSize);
    List<UserReadOnlyDTO> getUsersFiltered(UserFiltersDTO filters);
    UserReadOnlyDTO saveUser(UserInsertDTO dto) throws AppObjectAlreadyExistsException;
    UserReadOnlyDTO registerUser(UserRegisterDTO dto) throws AppObjectAlreadyExistsException;
    UserReadOnlyDTO updateUser(String uuid, UserUpdateDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExistsException;
    void reverseUserStatusActivity(String uuid) throws AppObjectNotFoundException;
    void deleteUser(String uuid) throws AppObjectNotFoundException;
    void deleteAllUsers();
    void changeUserPassword(String username, ChangePasswordDTO passwordDTO) throws AppObjectNotFoundException, AppObjectNotAuthorizedException;
    void updateUserPasswordAfterReset(User user, String newPassword);
}
