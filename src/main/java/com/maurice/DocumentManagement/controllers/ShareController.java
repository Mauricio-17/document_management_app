package com.maurice.DocumentManagement.controllers;

import com.maurice.DocumentManagement.dto.ShareRequest;
import com.maurice.DocumentManagement.services.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    private ShareService shareService;

    @Autowired
    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping
    public ResponseEntity<?> registerShare(@RequestBody ShareRequest request){
        shareService.registerShare(request);
        return ResponseEntity.status(201).body(null);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> addUser(@PathVariable("userId") Long userId, @RequestParam("shareId") Long shareId){
        shareService.addUser(shareId, userId);
        return ResponseEntity.status(201).body(null);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable("userId") Long userId, @RequestParam("shareId") Long shareId){
        shareService.removeUser(shareId, userId);
        return ResponseEntity.status(204).body(null);
    }

    @DeleteMapping("/{shareId}")
    public ResponseEntity<?> removeShare(@PathVariable("shareId") Long shareId){
        shareService.removeShare(shareId);
        return ResponseEntity.status(204).body(null);
    }

}
