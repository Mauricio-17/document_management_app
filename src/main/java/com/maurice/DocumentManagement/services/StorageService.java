package com.maurice.DocumentManagement.services;

import com.maurice.DocumentManagement.exceptions.BadRequestException;
import com.maurice.DocumentManagement.exceptions.FileNotFoundException;
import com.maurice.DocumentManagement.exceptions.NotFoundException;
import com.maurice.DocumentManagement.exceptions.StorageException;
import com.maurice.DocumentManagement.utils.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class StorageService {

    private final S3Operations s3Operations;
    private final Path rootLocation;


    @Autowired
    public StorageService(StorageProperties properties, S3Operations operations) {

        if(properties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }
        this.rootLocation = Paths.get(properties.getLocation());
        this.s3Operations = operations;
    }

    public File createFolderForUser(String userEmail){
        File newDirectory = new File(rootLocation.getFileName().toString(), userEmail);
        if (!newDirectory.exists()){
            newDirectory.mkdir();
        }
        return newDirectory;
    }

    /**
    @param file It's the file that's going to be stored
     @param userEmail It's the email of the user that will be used to reference the local directory
     @param s3DirectoryKey It's the key that's going to be used to retrieve the S3 object, the object then will be stored in the referenced local directory.
     */
    public void store(MultipartFile file, String userEmail, String s3DirectoryKey) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            // Compare if the proper folder exist, it can only be created once per user
            File actualDirectory = createFolderForUser(userEmail);

            this.deleteChildFilesFromGivenDirectory(actualDirectory);
            // Adding the file to the given directory
            Files.copy(file.getInputStream(), actualDirectory.toPath().resolve(Objects.requireNonNull(file.getOriginalFilename())));

            String pathname = actualDirectory.toPath() +"/"+file.getOriginalFilename();
            String newKey = "fullstack-app/"+ s3DirectoryKey + file.getOriginalFilename();
            s3Operations.putObject(newKey, pathname);


        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    /**
    * @param filename It's the real name of the file to be retrieved
    * @param objectKey It's the referenced key of the S3 object to be retrieved
     * @param email it's the email of the current user
    * */
    public Resource loadAsResource(String filename, String objectKey, String email) {

        // Defining a folder for the user and/or removing existing files inside the user local folder
        File actualDirectory = createFolderForUser(email);
        this.deleteChildFilesFromGivenDirectory(actualDirectory);

        try {
            // Referencing the local folder path for the current user
            File path = new File(rootLocation.getFileName().toString(), email);
            String currentPath = path.toPath().toString();
            System.out.println("current path "+currentPath);

            s3Operations.getObject(objectKey, currentPath+"/"+filename);

            // Once the object is retrieved from S3, we load it to the user
            Path file = load(filename, path.toPath());
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() || resource.isReadable()) {
                return resource;
            }

            else {
                throw new NotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new NotFoundException("Could not read file: " + filename, e);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    public void deleteFolder(String folderKey){
        s3Operations.getObjectKeys(folderKey).forEach(s3Operations::deleteObject);
    }

    public void updateObject(String objectKey, String newName){
        s3Operations.updateObject(objectKey, newName);
    }

    public void deleteObject(String objectKey){
        s3Operations.deleteObject(objectKey);
    }

    public Path load(String filename, Path currentPath) {
        return currentPath.resolve(filename);
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(this.rootLocation.toFile());
    }

    public void deleteChildFilesFromGivenDirectory(File file){
        for (File itemFile : Objects.requireNonNull(file.listFiles())){
            itemFile.delete();
        }
    }
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
