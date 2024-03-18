package com.maurice.DocumentManagement.controllers;

import com.maurice.DocumentManagement.dto.FolderRequest;
import com.maurice.DocumentManagement.dto.FolderResponse;
import com.maurice.DocumentManagement.dto.KeyResponse;
import com.maurice.DocumentManagement.services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/folder")
public class FolderController {

    private FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/user/{id}")
    public List<FolderResponse> getFoldersByUser(@PathVariable("id") Long userId) {
        return folderService.getFoldersByUserId(userId);
    }

    @GetMapping
    public KeyResponse getChildKeys(@RequestParam("folderKey") String folderKey) {
        System.out.println(folderKey);
        return folderService.getKeysByFolderKey(folderKey);
    }

    @PostMapping
    public ResponseEntity<?> registerFolder(@RequestBody FolderRequest request){
        folderService.registerFolder(request);
        return ResponseEntity.status(201).body(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFolder(@PathVariable("id") Long folderId, @RequestParam("userEmail") String email) {
        folderService.deleteFolder(folderId, email);
        return ResponseEntity.noContent().build();
    }

}
