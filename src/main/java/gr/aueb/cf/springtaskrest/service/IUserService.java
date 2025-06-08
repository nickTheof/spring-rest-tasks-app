package gr.aueb.cf.springtaskrest.service;

import gr.aueb.cf.springtaskrest.core.enums.Role;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.core.filters.UserFilters;
import gr.aueb.cf.springtaskrest.dto.Paginated;
import gr.aueb.cf.springtaskrest.dto.UserInsertDTO;
import gr.aueb.cf.springtaskrest.dto.UserReadOnlyDTO;
import gr.aueb.cf.springtaskrest.dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import java.util.List;


public interface IUserService {
    UserReadOnlyDTO findByUsername(String username) throws AppObjectNotFoundException;
    UserReadOnlyDTO findByUuid(String uuid) throws AppObjectNotFoundException;
    Paginated<UserReadOnlyDTO> getUsersFilteredPaginated(UserFilters filters);
    Page<UserReadOnlyDTO> getUsersPaginated(int page, int pageSize);
    List<UserReadOnlyDTO> getUsersFiltered(UserFilters filters);
    UserReadOnlyDTO saveUser(UserInsertDTO dto) throws AppObjectAlreadyExistsException;
    UserReadOnlyDTO updateUser(String uuid, UserUpdateDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExistsException;
    UserReadOnlyDTO reverseUserStatusActivity(String uuid) throws AppObjectNotFoundException;
    UserReadOnlyDTO updateUserRole(String uuid, Role role) throws AppObjectNotFoundException;
    void deleteUser(String uuid) throws AppObjectNotFoundException;
    void deleteAllUsers();
}
