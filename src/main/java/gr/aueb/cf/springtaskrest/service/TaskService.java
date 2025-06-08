package gr.aueb.cf.springtaskrest.service;

import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.springtaskrest.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.springtaskrest.core.filters.TaskFilters;
import gr.aueb.cf.springtaskrest.core.specifications.TaskSpecification;
import gr.aueb.cf.springtaskrest.dto.Paginated;
import gr.aueb.cf.springtaskrest.dto.TaskInsertDTO;
import gr.aueb.cf.springtaskrest.dto.TaskReadOnlyDTO;
import gr.aueb.cf.springtaskrest.dto.TaskUpdateDTO;
import gr.aueb.cf.springtaskrest.mapper.Mapper;
import gr.aueb.cf.springtaskrest.model.Task;
import gr.aueb.cf.springtaskrest.model.User;
import gr.aueb.cf.springtaskrest.repository.TaskRepository;
import gr.aueb.cf.springtaskrest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Override
    public TaskReadOnlyDTO findTaskByUuid(String uuid) throws AppObjectNotFoundException {
        Task task = taskRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("Task", "Task with uuid " + uuid + " not found"));
        return mapper.mapToTaskReadOnly(task);
    }

    @Override
    public TaskReadOnlyDTO findTaskByUserUuidAndTaskTitle(String uuid, String taskTitle) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + uuid + " not found"));
        Task task = taskRepository.findByTitleAndUser(taskTitle, user).orElseThrow(() -> new AppObjectNotFoundException("Task", "Task with title " + taskTitle + " not found"));
        return mapper.mapToTaskReadOnly(task);
    }

    @Override
    public TaskReadOnlyDTO findTaskByUserUuidAndTaskUuid(String uuid, String taskUuid) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + uuid + " not found"));
        Task task = taskRepository.findByUuidAndUser(uuid, user).orElseThrow(() -> new AppObjectNotFoundException("Task", "Task with uuid " + uuid + " not found"));
        return mapper.mapToTaskReadOnly(task);
    }

    @Override
    public Paginated<TaskReadOnlyDTO> getFilteredPaginatedTasks(TaskFilters filters) {
        return new Paginated<>(taskRepository.findAll(getSpecsFromFilters(filters), filters.getPageable()).map(mapper::mapToTaskReadOnly));
    }

    @Override
    public List<TaskReadOnlyDTO> getFilteredTasks(TaskFilters filters) {
        return taskRepository.findAll(getSpecsFromFilters(filters)).stream().map(mapper::mapToTaskReadOnly).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = {AppObjectNotFoundException.class})
    @Override
    public void deleteTaskByUuid(String uuid) throws AppObjectNotFoundException {
        Task task = taskRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("Task", "Task with uuid " + uuid + " not found"));
        taskRepository.delete(task);
    }

    @Override
    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }

    @Transactional(rollbackFor = {AppObjectNotFoundException.class})
    @Override
    public void deleteAllUserTasks(String uuid) throws AppObjectNotFoundException {
        if (userRepository.findByUuid(uuid).isEmpty()) throw new AppObjectNotFoundException("User", "User with uuid " + uuid + " not found");
        taskRepository.deleteByUserUuid(uuid);
    }

    @Transactional(rollbackFor = {AppObjectNotFoundException.class, AppObjectAlreadyExistsException.class})
    @Override
    public TaskReadOnlyDTO createTask(String userUuid, TaskInsertDTO taskInsertDTO) throws AppObjectAlreadyExistsException, AppObjectNotFoundException {
        User user = userRepository.findByUuid(userUuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + userUuid + " not found"));
        if(taskRepository.findByTitleAndUser(taskInsertDTO.title(), user).isPresent()){
            throw new AppObjectAlreadyExistsException("Task", "Task with title " + taskInsertDTO.title() + " already exists");
        }
        Task task = mapper.mapToTask(taskInsertDTO);
        task.setUser(user);
        Task savedTask = taskRepository.save(task);
        return mapper.mapToTaskReadOnly(savedTask);
    }

    @Transactional(rollbackFor = {AppObjectNotFoundException.class, AppObjectAlreadyExistsException.class})
    @Override
    public TaskReadOnlyDTO updateTask(String userUuid, String taskUuid, TaskUpdateDTO taskUpdateDTO) throws AppObjectNotFoundException, AppObjectAlreadyExistsException {
        User user = userRepository.findByUuid(userUuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + userUuid + " not found"));
        Task task = taskRepository.findByUuidAndUser(taskUuid, user).orElseThrow(() -> new AppObjectNotFoundException("Task", "Task with uuid " + taskUuid + " not found"));
        Optional<Task> fetchByTitle = taskRepository.findByTitleAndUser(taskUpdateDTO.title(), user);
        if (fetchByTitle.isPresent() && !fetchByTitle.get().getUuid().equals(task.getUuid())){
            throw new AppObjectAlreadyExistsException("Task", "Task with title " + taskUpdateDTO.title() + " already exists");
        }
        Task toUpdate = mapper.mapToTask(taskUpdateDTO, task);
        Task updatedTask = taskRepository.save(toUpdate);
        return mapper.mapToTaskReadOnly(updatedTask);
    }

    @Override
    public void deleteTaskByUuidAndUserUuid(String uuid, String taskUuid) throws AppObjectNotFoundException {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AppObjectNotFoundException("User", "User " + uuid + " not found"));
        Task task = taskRepository.findByUuidAndUser(taskUuid, user).orElseThrow(() -> new AppObjectNotFoundException("Task", "Task with uuid " + taskUuid + " not found"));
        taskRepository.delete(task);
    }


    private Specification<Task> getSpecsFromFilters(TaskFilters filters) {
        Specification<Task> spec = (root, query, builder) -> null;
        if (filters.getUuid() != null) {
            spec = spec.and(TaskSpecification.tasksFieldLike("uuid", filters.getUuid()));
        }

        if (filters.getTitle() != null) {
            spec = spec.and(TaskSpecification.tasksFieldLike("title", filters.getTitle()));
        }

        if (filters.getStatus() != null) {
            spec = spec.and(TaskSpecification.taskStatusIn(filters.getStatus()));
        }

        if (filters.getUserUuid() != null) {
            spec = spec.and(TaskSpecification.tasksFieldLike("userUuid", filters.getUserUuid()));
        }

        if (filters.getUserIsActive() != null) {
            spec = spec.and(TaskSpecification.tasksUserIsActive(filters.getUserIsActive()));
        }
        return spec;
    }
}
