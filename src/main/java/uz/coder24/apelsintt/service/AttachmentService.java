package uz.coder24.apelsintt.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.coder24.apelsintt.entity.Attachment;

import java.net.MalformedURLException;

public interface AttachmentService {

    public ResponseEntity<?> save(MultipartFile multipartFile);

    public ResponseEntity<?> findAllBySort(String search, int page, int size, long startDate, long finishDate, Long minSize, Long maxSize);

    public ResponseEntity<?> preview(String hashId) throws MalformedURLException;

    public ResponseEntity<?> download(String hashId) throws MalformedURLException;

    public ResponseEntity<?> delete(String hashId);

}
