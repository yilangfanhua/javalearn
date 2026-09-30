package com.example.demo.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

@Service 
public class FileStoreService {
    
    private static  final long MAX_BYTES=5L*1024*1024;
    private final Path root;

    public FileStoreService(@Value ("${app.upload-dir}")String uploadDir){
        this.root=Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String store (MultipartFile file){
        if(file==null||file.isEmpty()){
            throw new IllegalArgumentException("请选择要上传的图片");

        }

        if(file.getSize()>MAX_BYTES){
            throw new IllegalArgumentException("图片不能超过5MB");


        }

    
        String extension=ValidateImage(file);
        String filename=UUID.randomUUID()+extension;
        Path destination=root.resolve(filename).normalize();



        try{
            Files.createDirectories(root);
            Files.copy(file.getInputStream(), destination);
            return filename;
        }
        catch(IOException exception){
            throw new IllegalStateException("保存图片失败",exception);

        }
    }


    public Resource load(String filename){
            if(filename==null||!filename.matches("[0-9a-fA-F-]{36}\\.(png|jpg)")){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            try{
                Path file=root.resolve(filename).normalize();
                if(!file.startsWith(root)||!Files.isRegularFile(file)){
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
                return new UrlResource(file.toUri());
            }
            catch(IOException exception){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
           

    }

    private String ValidateImage(MultipartFile file){
        try(ImageInputStream input=ImageIO.createImageInputStream(file.getInputStream())){;
            Iterator<ImageReader> readers =ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new IllegalArgumentException("文件不是支持的图片");
            }

            ImageReader reader = readers.next();

            try{
                reader.setInput(input, true, true);
                String format = reader.getFormatName()
                        .toLowerCase(Locale.ROOT);

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);


                if (!format.equals("png")
                        && !format.equals("jpeg")) {
                    throw new IllegalArgumentException(
                            "只支持 PNG 或 JPEG 图片"
                    );
                }

                if (width <= 0 || height <= 0
                        || (long) width * height > 20_000_000L) {
                    throw new IllegalArgumentException(
                            "图片尺寸不合法或过大"
                    );
                }
                return format.equals("png") ? ".png" : ".jpg";
            }
            finally{
                reader.dispose();
            }
        }
        catch(IOException exception){
            throw new IllegalArgumentException("无法读取图片文件");
        }
    }



}
