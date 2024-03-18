package com.maurice.DocumentManagement.services;

import com.maurice.DocumentManagement.dto.FolderRequest;
import com.maurice.DocumentManagement.dto.UserPageResponse;
import com.maurice.DocumentManagement.dto.UserRequest;
import com.maurice.DocumentManagement.dto.UserResponse;
import com.maurice.DocumentManagement.entities.Plan;
import com.maurice.DocumentManagement.exceptions.BadRequestException;
import com.maurice.DocumentManagement.exceptions.NotAcceptableRequestException;
import com.maurice.DocumentManagement.exceptions.NotFoundException;
import com.maurice.DocumentManagement.repository.PlanRepository;
import com.maurice.DocumentManagement.repository.ShareRepository;
import com.maurice.DocumentManagement.repository.UserRepository;
import com.maurice.DocumentManagement.entities.UserEntity;
import com.maurice.DocumentManagement.utils.Mappers;
import com.maurice.DocumentManagement.utils.Validators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private ShareRepository shareRepository;
    private PlanRepository planRepository;
    private FolderService folderService;
    private StorageService storageService;

    @Autowired
    public UserService(UserRepository userRepository, ShareRepository shareRepository, PlanRepository planRepository, FolderService folderService, StorageService storageService) {
        this.userRepository = userRepository;
        this.shareRepository = shareRepository;
        this.planRepository = planRepository;
        this.folderService = folderService;
        this.storageService = storageService;
    }

    private final DateTimeFormatter format =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");



    public UserPageResponse getUsersByPlanName(String planName, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return getPageableUser(
                userRepository.findUsersByPlanName(planName, pageable)
        );
    }

    public List<UserResponse> getUsersByShareId(Long shareId) {
        List<UserEntity> listOfUsers = userRepository.findUsersByShareId(shareId);
        return listOfUsers.stream().map(item -> {
            UserResponse res = Mappers.userToDto.apply(item);
            res.setPlanName(item.getPlan().getName());;
            return res;
        }).collect(Collectors.toList());
    }

    public UserPageResponse getAllUsers(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return getPageableUser(
                userRepository.findAll(pageable)
        );
    }

    public UserPageResponse getPageableUser(Page<UserEntity> page) {
        List<UserEntity> userList = page.getContent();
        if (userList.isEmpty()) {
            throw new NotFoundException("No userEmails found.");
        }

        List<UserResponse> content = userList.stream().map(item -> {
                    UserResponse res = Mappers.userToDto.apply(item);
                    res.setPlanName(item.getPlan().getName());
                    return res;
                }
        ).toList();

        return new UserPageResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public void updateUser(UserRequest payload, Long userId) {
        int results = userRepository.updateUser(payload.name(), payload.lastname(), payload.email(), LocalDateTime.now(), userId);
        if (results == 0) {
            throw new NotFoundException("User not found.");
        }
    }

    public void changePassword(String oldPass, String newPass, Long userId) {
        // ... comparing the old password
        // ... if it does not fulfill then throw an exception

        int results = userRepository.changePassword(newPass, LocalDateTime.now(), userId);
        if (results == 0) {
            throw new NotFoundException("User not found.");
        }
    }

    public void changePlan(Long planId, Long userId) {
        planRepository.findById(planId).orElseThrow(
                () -> new NotFoundException("Plan not found")
        );
        int results = userRepository.changePlan(planId, LocalDateTime.now(), userId);
        if (results == 0) {
            throw new NotFoundException("User not found.");
        }

    }
}
