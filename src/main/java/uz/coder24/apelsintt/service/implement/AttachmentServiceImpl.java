package uz.coder24.apelsintt.service.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.coder24.apelsintt.entity.Attachment;
import uz.coder24.apelsintt.model.Result;
import uz.coder24.apelsintt.repository.AttachmentRepository;
import uz.coder24.apelsintt.service.AttachmentService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Value("${upload.folder}")
    private String uploadPath;

    @Value("${upload.max-file-size}")
    public Long limitFileSize;


    @Autowired
    private AttachmentRepository attachmentRepository;

    public ResponseEntity<?> save(MultipartFile multipartFile) {
        Attachment file = new Attachment();
        file.setContentType(multipartFile.getContentType());
        file.setFileSize(multipartFile.getSize()/8/1024);
        file.setName(multipartFile.getOriginalFilename());
        file.setHashId(UUID.randomUUID().toString());
        file.setExtension(getExtension(file.getName()));
        Date now = new Date();
        java.io.File uploadFolder = new java.io.File(String.format("%s/%d/%d/%d/", this.uploadPath,
                1900 + now.getYear(), 1 + now.getMonth(), now.getDate()));

        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        file.setUploadPath(String.format("%d/%d/%d/%s.%s",
                1900 + now.getYear(),
                1 + now.getMonth(),
                now.getDate(),
                file.getHashId(),
                file.getExtension()));
        Attachment file2 = attachmentRepository.save(file);
        java.io.File file3 = new java.io.File(uploadFolder.getAbsoluteFile(), String.format("%s.%s", file2.getHashId(), file2.getExtension()));
        try {
            multipartFile.transferTo(file3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(file2.getHashId());
    }

    @Override
    public ResponseEntity<?> findAllBySort(String search, int page, int size, long startDate, long finishDate, Long minSize, Long maxSize) {
        PageRequest of = PageRequest.of(page, size);
        Date from = new Date(startDate);
        Date to = new Date(finishDate);
        if (maxSize == null || maxSize.equals(0L)) {
            maxSize = limitFileSize;
        }
        if (startDate == 0L){
            from = new Date(0);
        }
        if (finishDate == 0L){
            to = new Date(0);
        }

        return ResponseEntity.ok(attachmentRepository.findAllBySort(of, search.toLowerCase(), from, to, minSize, maxSize));
    }

    private String getExtension(String fileName) {
        String extension = null;
        if (fileName != null && !fileName.isEmpty()) {
            int index = fileName.lastIndexOf(".");
            extension = fileName.substring(index + 1);
        }
        return extension;
    }

    private ResponseEntity<?> findByHashId(String hashId) {
        return ResponseEntity.ok(attachmentRepository.findByHashId(hashId).orElseThrow(() -> new RuntimeException("attachment not found")));
    }

    public ResponseEntity<?> delete(String hashId) {
        try {
            Attachment file = attachmentRepository.findByHashId(hashId).orElseThrow(() -> new RuntimeException("attachment not found"));
            attachmentRepository.delete(file);
            java.io.File file1 = new java.io.File(String.format("%s/%s", uploadPath, file.getUploadPath()));
            file1.delete();
            return ResponseEntity.ok(new Result(true, "deleted", null));
        } catch (Exception e) {

        }
        return ResponseEntity.ok(new Result(false, "not deleted", HttpStatus.BAD_REQUEST));
    }

    public ResponseEntity<?> preview(String hashId) throws MalformedURLException {
        Attachment file = attachmentRepository.findByHashId(hashId).orElseThrow(() -> new RuntimeException("attachment not found"));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; fileName=" + URLEncoder.encode(file.getName()))
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(new FileUrlResource(String.format("%s/%s",
                        uploadPath,
                        file.getUploadPath())));
    }

    public ResponseEntity<?> download(String hashId) throws MalformedURLException {
        Attachment file = attachmentRepository.findByHashId(hashId).orElseThrow(() -> new RuntimeException("attachment not found"));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + URLEncoder.encode(file.getName()))
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(new FileUrlResource(String.format("%s/%s",
                        uploadPath,
                        file.getUploadPath())));
    }

}
