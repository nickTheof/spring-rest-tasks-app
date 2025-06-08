package gr.aueb.cf.springtaskrest.service;

import gr.aueb.cf.springtaskrest.core.enums.TaskStatus;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.core.filters.TaskFilters;
import gr.aueb.cf.springtaskrest.dto.Paginated;
import gr.aueb.cf.springtaskrest.dto.TaskInsertDTO;
import gr.aueb.cf.springtaskrest.dto.TaskReadOnlyDTO;
import gr.aueb.cf.springtaskrest.dto.TaskUpdateDTO;

import java.util.List;

public interface ITaskService {
    TaskReadOnlyDTO findTaskByUuid(String uuid) throws AppObjectNotFoundException;
    TaskReadOnlyDTO findTaskByUserUuidAndTaskTitle(String uuid, String taskTitle) throws AppObjectNotFoundException;
    TaskReadOnlyDTO findTaskByUserUuidAndTaskUuid(String uuid, String taskUuid) throws AppObjectNotFoundException;
    Paginated<TaskReadOnlyDTO> getFilteredPaginatedTasks(TaskFilters filters);
    List<TaskReadOnlyDTO> getFilteredTasks(TaskFilters filters);
    void deleteTaskByUuid(String uuid) throws AppObjectNotFoundException;
    void deleteTaskByUuidAndUserUuid(String uuid, String taskUuid) throws AppObjectNotFoundException;
    void deleteAllTasks();
    void deleteAllUserTasks(String uuid) throws AppObjectNotFoundException;
    TaskReadOnlyDTO createTask(String userUuid, TaskInsertDTO taskInsertDTO) throws AppObjectAlreadyExistsException, AppObjectNotFoundException;
    TaskReadOnlyDTO updateTask(String userUuid, String taskUuid, TaskUpdateDTO taskUpdateDTO) throws AppObjectNotFoundException, AppObjectAlreadyExistsException;
}
