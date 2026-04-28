package tech.wetech.admin3.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.wetech.admin3.sys.model.StorageFile;
import tech.wetech.admin3.sys.service.StorageService;

/**
 * 公开文件访问接口（无需登录）
 */
@RestController
@RequestMapping("/storage/files")
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/{key:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String key) {
        if (key == null || key.contains("../")) {
            return ResponseEntity.badRequest().build();
        }
        StorageFile storageFile = storageService.getByKey(key);
        if (storageFile == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = storageService.loadAsResource(key);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = MediaType.parseMediaType(storageFile.getType());
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }
}
