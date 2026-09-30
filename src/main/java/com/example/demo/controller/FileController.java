package com.example.demo.controller;


import com.example.demo.common.ApiResponse;
import com.example.demo.service.FileStoreService;
//import com.example.demo.service.FileStoreService;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController 
@RequestMapping ("/files")
public class FileController {
    private final FileStoreService fileStoreService;

    public FileController(FileStoreService fileStoreService) {
        this.fileStoreService = fileStoreService;
    }

    @PostMapping 
    public ApiResponse<String> uplaod(@RequestPart("file") MultipartFile file){
        String filename=fileStoreService.store(file);
        return ApiResponse.success("/file/"+filename);
    }
    @GetMapping ("/{filename:.+}")
    public ResponseEntity<Resource> getfile(@PathVariable  String filename){
        MediaType dMediaType=filename.endsWith(".png")
                ? MediaType.IMAGE_PNG
                : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(dMediaType)
                .body(fileStoreService.load(filename));
    }
}
