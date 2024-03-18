package com.maurice.DocumentManagement.services;

import com.maurice.DocumentManagement.dto.FolderRequest;
import com.maurice.DocumentManagement.dto.FolderResponse;
import com.maurice.DocumentManagement.dto.KeyResponse;
import com.maurice.DocumentManagement.entities.Folder;
import com.maurice.DocumentManagement.entities.UserEntity;
import com.maurice.DocumentManagement.exceptions.BadRequestException;
import com.maurice.DocumentManagement.exceptions.NotFoundException;
import com.maurice.DocumentManagement.repository.FolderRepository;
import com.maurice.DocumentManagement.repository.UserRepository;
import com.maurice.DocumentManagement.utils.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.maurice.DocumentManagement.utils.Utilities.getFirstLevelObjectKeys;
import static com.maurice.DocumentManagement.utils.Utilities.getOnlyFolderNamesFromKeys;

@Service
public class FolderService {

    private FolderRepository folderRepository;
    private UserRepository userRepository;
    private S3Operations operations;
    private StorageService storageService;

    @Autowired
    public FolderService(FolderRepository folderRepository, UserRepository userRepository, S3Operations operations, StorageService storageService) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.operations = operations;
        this.storageService = storageService;
    }

    public List<FolderResponse> getFoldersByUserId(Long userId){
        return folderRepository.findFoldersByUserId(userId).orElseThrow(
                () -> new NotFoundException("No folders found for the user with ID: "+ userId)
        ).stream().map(Mappers.folderToDto).toList();
    }

    public KeyResponse getKeysByFolderKey(String folderKey){
        folderRepository.findFolderByFolderKey(folderKey).orElseThrow(
                () -> new NotFoundException("No folder found with key: "+folderKey)
        );

        List<Folder> listOfKeys = folderRepository.findFoldersByParentFolderKey(folderKey);
        if (listOfKeys.isEmpty()) {
            throw new NotFoundException("No keys were found");
        }
        var totalFetchedKeys = listOfKeys.stream().map(Folder::getKey).collect(Collectors.toList());

        Set<String> resultingKeys = new HashSet<>();
        getFirstLevelObjectKeys(folderKey, totalFetchedKeys, resultingKeys);

        var resultingNames = getOnlyFolderNamesFromKeys(resultingKeys);
        return new KeyResponse(resultingKeys, resultingNames);
    }

    public void registerFolder(FolderRequest request){

        Optional<Folder> parentFolderFound = folderRepository.findFolderByKey("fullstack-app/" + request.key());
        var referencedFolderKeyFragments = request.key().split("/"); // It'll be used to recognize if a folder is root

        if (parentFolderFound.isEmpty() && referencedFolderKeyFragments.length != 1){
            throw new BadRequestException("No parent folder exist");
        }

        String keyToBeFound = request.key() + request.name() + "/";
        Optional<Folder> found = folderRepository.findFolderByKey(keyToBeFound);
        if (found.isPresent()){
            throw new BadRequestException("Folder already exist.");
        }

        Optional<UserEntity> user = userRepository.findById(request.userId());
        if (user.isEmpty()) {
            throw new NotFoundException("User with ID:"+request.userId()+" not found.");
        }

        // Vary the folder key definition if it's root
        String folderKey = user.get().getEmail().equalsIgnoreCase(request.name()) ?
                request.key() : request.key() + request.name();

        Folder folder = new Folder(request.name(), request.description(), "fullstack-app/"+ folderKey + "/");

        folder.setUser(user.get());
        folder.setCreatedAt(LocalDateTime.now());
        folder.setLastModifiedAt(LocalDateTime.now());
        folderRepository.save(folder);
    }

    public void updateFolder(FolderRequest payload, Long folderId) {
        int result = folderRepository.updateFolder(payload.name(), payload.description(), LocalDateTime.now(), folderId);
        if(result == 0) {
            throw new NotFoundException("Folder not found.");
        }
    }

    public void deleteFolder(Long folderId, String email) {
        UserEntity foundUser = userRepository.findUserByEmail(email).orElseThrow(
                () -> new NotFoundException("Not user found with email: "+ email)
        );
        Folder foundFolder = folderRepository.findById(folderId).orElseThrow(
                () -> new NotFoundException("Folder not found")
        );
        if(Objects.equals(foundUser.getEmail(), foundFolder.getName())) {
            throw new BadRequestException("The root folder can't be removed");
        }

        var foldersToBeRemoved = folderRepository.findFoldersByFolderKey(foundFolder.getKey());

        folderRepository.deleteAll(foldersToBeRemoved);
        storageService.deleteFolder(foundFolder.getKey());

    }
}
