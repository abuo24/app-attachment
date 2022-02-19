package uz.coder24.apelsintt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.coder24.apelsintt.service.AttachmentService;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/file")
public class AttachmentController {

    @Autowired
    private AttachmentService service;

    @PostMapping("/")
    public ResponseEntity<?> save(@RequestParam(name = "file") MultipartFile multipartFile) {
        return service.save(multipartFile);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "") String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false, name = "startDate", defaultValue = "0") long startDate, @RequestParam(required = false, name = "finishDate", defaultValue = "0") long finishDate, @RequestParam(name = "minSize", defaultValue = "0L") Long minSize, @RequestParam(name = "maxSize", defaultValue = "0", required = false) Long maxSize) {
        return service.findAllBySort(search, page, size, startDate, finishDate, minSize, maxSize);
    }


    @GetMapping("/preview/{hashId}")
    public ResponseEntity<?> preview(@PathVariable String hashId) throws MalformedURLException {
        return service.preview(hashId);
    }

    @GetMapping("/download/{hashId}")
    public ResponseEntity<?> download(@PathVariable String hashId) throws MalformedURLException {
        return service.download(hashId);
    }

    @DeleteMapping("/delete/{hashId}")
    public ResponseEntity<?> delete(@PathVariable String hashId) throws MalformedURLException {
        return service.delete(hashId);
    }
}
