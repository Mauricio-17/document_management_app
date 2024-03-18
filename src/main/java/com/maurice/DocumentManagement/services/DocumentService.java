package com.maurice.DocumentManagement.services;

import com.maurice.DocumentManagement.dto.*;
import com.maurice.DocumentManagement.entities.*;
import com.maurice.DocumentManagement.exceptions.BadRequestException;
import com.maurice.DocumentManagement.exceptions.CreateStatusException;
import com.maurice.DocumentManagement.exceptions.NotAcceptableRequestException;
import com.maurice.DocumentManagement.exceptions.NotFoundException;
import com.maurice.DocumentManagement.repository.DocumentDetailRepository;
import com.maurice.DocumentManagement.repository.DocumentRepository;
import com.maurice.DocumentManagement.repository.FolderRepository;
import com.maurice.DocumentManagement.repository.UserRepository;
import com.maurice.DocumentManagement.utils.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.maurice.DocumentManagement.utils.Utilities.*;

@Service
public class DocumentService {

    private DocumentRepository documentRepository;
    private UserRepository userRepository;
    private FolderRepository folderRepository;
    private DocumentDetailRepository detailRepository;
    private StorageService storageService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository, FolderRepository folderRepository, DocumentDetailRepository detailRepository, StorageService storageService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.detailRepository = detailRepository;
        this.storageService = storageService;
    }

    public DocumentPageResponse getAllPublicDocuments(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return this.getPageableDocument(
                documentRepository.findDocumentsByIsPublic(pageable)
        );
    }

    public List<DocumentResponse> getDocumentsByShare(Long shareId){
        List<Document> list = documentRepository.findDocumentsByShareId(shareId);
        if (list.isEmpty()) throw CreateStatusException.getThrowableException("Documents not found", 404);

        return list.stream().map(item -> {
            var element = Mappers.documentToDto.apply(item);
            element.setFolderName(item.getDocumentDetail().getFolder().getName());
            return element;
        }).collect(Collectors.toList());
    }

    public DocumentPageResponse getDocumentsByFolderId(Long folderId, int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return this.getPageableDocument(
                documentRepository.findDocumentsByFolderId(folderId, pageable)
        );
    }



    public Resource getDocumentByKey(String docKey, String email){
        userRepository.findUserByEmail(email).orElseThrow(
                () -> CreateStatusException.getThrowableException("No user found with email "+email, 404)
        );
        Document document = documentRepository.findDocumentByKey(docKey).orElseThrow(
                () -> CreateStatusException.getThrowableException("No document found", 404)
        );
        String fullDocument = document.getFileName()+"."+document.getFileType();
        return storageService.loadAsResource(fullDocument, document.getKey(), email);
    }



    public List<DocumentResponse> getDocumentsByCategoryId(Long categoryId){
        List<Document> listOfDocuments = documentRepository.findDocumentsByCategoryId(categoryId);
        if(listOfDocuments.isEmpty()){
            throw CreateStatusException.getThrowableException("Documents not found", 404);
        }
        return listOfDocuments.stream().map(item -> {
            DocumentResponse documentResponse = Mappers.documentToDto.apply(item);
            Folder foundFolder = item.getDocumentDetail().getFolder();
            if (foundFolder != null)
                documentResponse.setFolderName(foundFolder.getName());

            return documentResponse;
        }).toList();
    }

    public void registerDocument(MultipartFile file, Long userId, String folderKey) {
        Folder folderFound = folderRepository.findFolderByKey(folderKey).orElseThrow(
                () -> CreateStatusException.getThrowableException("Folder not found", 404)
        );
        UserEntity userFound = userRepository.findById(userId).orElseThrow(
                () -> CreateStatusException.getThrowableException("User not found", 404)
        );

        DocumentRequest document = getDocumentRequest(file, userId, folderFound);
        Optional<Document> foundDoc = documentRepository.findDocumentByKey(document.key());
        if (foundDoc.isPresent()) {
            throw CreateStatusException.getThrowableException("Document with key "+ document.key() +" already exists", 400);
        }

        Document doc = Mappers.dtoToDocument.apply(document);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setLastModifiedAt(LocalDateTime.now());

        var savedDoc = documentRepository.save(doc);

        // Defining the detail table
        var savedDetail =  detailRepository.save(new DocumentDetail(savedDoc, folderFound));
        System.out.println(savedDoc);
        storageService.store(file, userFound.getEmail(), folderFound.getKey());

        // updating the related document and folder
        savedDoc.setDocumentDetail(savedDetail);
        folderFound.addDocumentDetail(savedDetail);
        documentRepository.save(savedDoc);
        folderRepository.save(folderFound);
    }


    public void updateDocument(DocumentRequest request, Long documentId){
        Document doc = documentRepository.findById(documentId).orElseThrow(
                () -> CreateStatusException.getThrowableException("Document not found", 404)
        );
        String folder = extractFolderFromKey(doc.getKey());
        String newKey = folder + request.fileName() + "." + doc.getFileType();
        int result = documentRepository.updateDocument(
                request.fileName(),
                Status.valueOf(request.status()),
                request.isPublic(),
                newKey,
                documentId
        );
        if(result == 0) {
            throw CreateStatusException.getThrowableException("Document not found", 404);
        }

        storageService.updateObject(doc.getKey(), request.fileName());
    }

    public void deleteDocument(Long id) {
        Document found = documentRepository.findById(id).orElseThrow(
                () -> CreateStatusException.getThrowableException("Document not found", 404)
        );

        //detailRepository.findById(found.getDocumentDetail().getId()).ifPresent(det -> detailRepository.delete(det));

        String key = found.getKey();
        documentRepository.delete(found);
        storageService.deleteObject(key);
    }

    private static DocumentRequest getDocumentRequest(MultipartFile file, Long userId, Folder folderFound) {
        DocumentRequest document = new DocumentRequest(
                extractNameFromFile(Objects.requireNonNull(file.getOriginalFilename())),
                extractFileTypeFromFile(file.getOriginalFilename()),
                "UNAVAILABLE",
                false,
                file.getSize(),
                folderFound.getKey()+ file.getOriginalFilename(),
                folderFound.getId(),
                userId
        );

        if (document.fileName().contains("/")){
            throw CreateStatusException.getThrowableException("The name must not contain the \"/\" character!", 400);
        }
        return document;
    }

    public DocumentPageResponse getPageableDocument(Page<Document> page) {
        List<Document> documentList = page.getContent();
        if (documentList.isEmpty()) {
            throw CreateStatusException.getThrowableException("No documents found", 404);
        }

        List<DocumentResponse> content = documentList.stream().map(item -> {
                    DocumentResponse res = Mappers.documentToDto.apply(item);
                    res.setFolderName(item.getDocumentDetail().getFolder().getName());
                    return res;
                }
        ).toList();

        return new DocumentPageResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

}
