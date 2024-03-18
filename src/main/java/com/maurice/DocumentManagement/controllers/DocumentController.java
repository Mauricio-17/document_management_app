package com.maurice.DocumentManagement.controllers;

import com.maurice.DocumentManagement.dto.DocumentPageResponse;
import com.maurice.DocumentManagement.dto.DocumentRequest;
import com.maurice.DocumentManagement.dto.DocumentResponse;
import com.maurice.DocumentManagement.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService){
        this.documentService = documentService;
    }

    @GetMapping
    public DocumentPageResponse getAllDocuments(

            ){
        return null;
    }

    @GetMapping("/public")
    public DocumentPageResponse getAllPublicDocuments(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ){
        return documentService.getAllPublicDocuments(pageNo, pageSize);
    }

    @GetMapping("/category/{id}")
    public List<DocumentResponse> getDocumentsByCategory(@PathVariable("id") Long categoryId){
        return documentService.getDocumentsByCategoryId(categoryId);
    }

    @GetMapping("/share/{id}")
    public List<DocumentResponse> getDocumentsByShare(@PathVariable("id") Long shareId){
        return documentService.getDocumentsByShare(shareId);
    }

    @GetMapping("/folder/{id}")
    public DocumentPageResponse getPublicDocumentsByFolder(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @PathVariable("id") Long folderId
    ){
        return documentService.getDocumentsByFolderId(folderId, pageNo, pageSize);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> addDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderKey") String folderKey,
            @RequestParam(value = "userId") Long userId
    ){
        documentService.registerDocument(file, userId, folderKey);
        System.out.println("File stored successfully.");

        return ResponseEntity.status(201).body(null);
    }

    @GetMapping("/file")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "docKey") String docKey,
            @RequestParam(name = "email") String email
    ) {
        Resource file = documentService.getDocumentByKey(docKey, email);
        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable("id") Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.status(204).body(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFile(
            @PathVariable("id") Long documentId,
            @RequestBody DocumentRequest request
    ){
        documentService.updateDocument(request, documentId);
        return ResponseEntity.status(204).body(null);
    }

}
