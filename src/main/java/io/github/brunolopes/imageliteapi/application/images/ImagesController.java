package io.github.brunolopes.imageliteapi.application.images;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/images")
@Slf4j
public class ImagesController {
    @PostMapping
    public ResponseEntity<?> save(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("tags") List<String> tags
    ){

        long maxFileSize = 7 * 1024 * 1024;
        double getSizeFile = file.getSize() / (1024.0 * 1024.0);

        if (file.getSize() > maxFileSize) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(String.format("O tamanho máximo do arquivo deve ser de 6MB, seu arquivo tem %.2fMB",
                            getSizeFile));
        }

        log.info("Imagem recebida: nome do arquivo: {}, size: {}", file.getOriginalFilename(), file.getSize());
        log.info("Nome definido na imagem: {}", name);
        log.info("Tags: {}", tags);

        return ResponseEntity.ok().build();
    }
}
