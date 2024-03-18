package com.maurice.DocumentManagement.services;

import com.maurice.DocumentManagement.dto.ShareRequest;
import com.maurice.DocumentManagement.dto.ShareUser;
import com.maurice.DocumentManagement.entities.*;
import com.maurice.DocumentManagement.exceptions.BadRequestException;
import com.maurice.DocumentManagement.exceptions.NotFoundException;
import com.maurice.DocumentManagement.repository.DocumentDetailRepository;
import com.maurice.DocumentManagement.repository.ShareRepository;
import com.maurice.DocumentManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShareService {

    private ShareRepository shareRepository;
    private UserRepository userRepository;
    private DocumentDetailRepository documentDetailRepository;

    @Autowired
    public ShareService(ShareRepository shareRepository, UserRepository userRepository, DocumentDetailRepository documentDetailRepository) {
        this.shareRepository = shareRepository;
        this.userRepository = userRepository;
        this.documentDetailRepository = documentDetailRepository;
    }

    public void registerShare(ShareRequest share){
        List<UserEntity> users = userRepository.findUsersBySomeEmails(share.userEmails());
        List<DocumentDetail> details = documentDetailRepository.findDocumentDetailsByDocumentKey(share.documentKeys());

        Share shareToBeSaved = new Share(PermissionAsset.valueOf(share.permission()), details, users);

        shareToBeSaved.setCreatedAt(LocalDateTime.now());
        shareToBeSaved.setLastModifiedAt(LocalDateTime.now());
        var savedShare = shareRepository.save(shareToBeSaved);

        // Updating the document detail relatives
        details.forEach(item -> {
            item.setShare(savedShare);
            documentDetailRepository.save(item);
        });
        System.out.println(savedShare);
    }

    public void updateShare(PermissionAsset permission, Long shareId) {
        int result = shareRepository.updateShare(permission, shareId);
        if(result < 1) {
            throw new NotFoundException("Not share found");
        }
    }

    public void removeUser(Long shareId, Long userId){
        Share foundShare = shareRepository.findById(shareId).orElseThrow(
                () -> new NotFoundException("Share not found.")
        );
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found.")
        );

        List<UserEntity> newListOfUsers = foundShare.getUsers().stream().filter(
                item -> !Objects.equals(item.getId(), userId)
        ).collect(Collectors.toList());

        System.out.println(newListOfUsers);
        foundShare.setUsers(newListOfUsers);

        // Updating the Share
        shareRepository.save(foundShare);
    }

    public void addUser(Long shareId, Long userId){

        Share foundShare = shareRepository.findById(shareId).orElseThrow(
                () -> new NotFoundException("Share not found.")
        );
        UserEntity foundUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found.")
        );
        List<UserEntity> listOfUsers = foundShare.getUsers();

        // Preventing to have more than one user for this share
        listOfUsers.forEach(item -> {
            if (Objects.equals(item.getId(), foundUser.getId())){
                throw new BadRequestException("ID's combination already exists.");
            }
        });

        listOfUsers.add(foundUser);
        foundShare.setUsers(listOfUsers);

        // Updating the Share
        shareRepository.save(foundShare);
    }

    public void removeShare(Long shareId){
        System.out.println("-------- "+shareId);
        shareRepository.findById(shareId).ifPresentOrElse(item -> {

            // detaching this share from each document detail found
            documentDetailRepository.findDocumentDetailsByShareId(item.getId()).forEach(element -> {
                element.setShare(null);
                documentDetailRepository.save(element);
            });

            shareRepository.delete(item);
        }
        // Otherwise
        , () -> {
            throw new BadRequestException("No share found with ID: "+ shareId);
        });
    }
}
