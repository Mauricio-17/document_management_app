package com.maurice.DocumentManagement.utils;

import com.maurice.DocumentManagement.dto.*;
import com.maurice.DocumentManagement.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class Mappers {

    private static final DateTimeFormatter format =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static Function<Plan, PlanDto> planToDto = item ->
            new PlanDto(item.getId(), item.getName(), item.getDescription(), item.getMonthlyCost());

    public static Function<PlanDto, Plan> dtoToPlan = item ->
            new Plan(item.id(), item.name(), item.description(), item.monthlyCost());

    public static Function<UserEntity, UserResponse> userToDto = item ->
            new UserResponse(item.getId(), item.getName(), item.getLastname(), item.getEmail(), item.getCreatedAt().format(format), item.getLastModifiedAt().format(format));

    public static Function<UserRequest, UserEntity> dtoToUser = item ->
            new UserEntity(item.name(), item.lastname(), item.email(), item.password());

    public static Function<Folder, FolderResponse> folderToDto = item ->
            new FolderResponse(item.getId(), item.getName(), item.getDescription(), item.getKey(), item.getCreatedAt().format(format), item.getLastModifiedAt().format(format));

    public static Function<FolderRequest, Folder> dtoToFolder = item ->
            new Folder(item.name(), item.description(), item.key() + item.name() + "/");

    public static Function<Document, DocumentResponse> documentToDto = item ->
            new DocumentResponse(item.getId(), item.getFileName(), item.getFileType(), item.getSize(), item.getStatus().toString(), item.getIsPublic(), item.getKey(), item.getCreatedAt().format(format), item.getLastModifiedAt().format(format));

    public static Function<DocumentRequest, Document> dtoToDocument = item ->
            new Document(item.fileName(), item.fileType(), Status.valueOf(item.status()), item.isPublic(), item.size(), item.key());

    public static Function<Category, CategoryDto> categoryToDto = item ->
            new CategoryDto(item.getId(), item.getName(), item.getDescription(), item.getCreatedAt().format(format), item.getLastModifiedAt().format(format));

    public static Function<CategoryDto, Category> dtoToCategory = item ->
            new Category(item.name(), item.description());

    public static Function<Share, ShareResponse> shareToDto = item ->
            new ShareResponse(item.getId(), item.getPermission().toString(), item.getCreatedAt().format(format), item.getLastModifiedAt().format(format));


}
