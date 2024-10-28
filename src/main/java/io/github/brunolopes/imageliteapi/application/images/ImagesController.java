package io.github.brunolopes.imageliteapi.application.images;

import io.github.brunolopes.imageliteapi.domain.entity.Image;
import io.github.brunolopes.imageliteapi.domain.enums.ImageExtension;
import io.github.brunolopes.imageliteapi.domain.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/images")
@Slf4j
@RequiredArgsConstructor
public class ImagesController {

    private final ImageService service;

    @PostMapping
    public ResponseEntity<?> save(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("tags") List<String> tags
    ) throws IOException {

        long maxFileSize = 7 * 1024 * 1024; //Tamanho máximo do arquivo permitido
        double getSizeFile = file.getSize() / (1024.0 * 1024.0); //Converte em MB

        //Verifica se o tamanho não é maior que o maxFileSize definido
        if (file.getSize() > maxFileSize) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(String.format("O tamanho máximo do arquivo deve ser de 6MB, seu arquivo tem %.2fMB",
                            getSizeFile));
        }

        log.info("Imagem recebida: nome do arquivo: {}, size: {}", file.getOriginalFilename(), file.getSize());
        log.info("Media type {}", MediaType.valueOf(file.getContentType()));
        log.info("Content type: {}", file.getContentType());

        MediaType.valueOf(file.getContentType());

        Image image = Image.builder()
                        .name(name)
                        .tags(String.join(",", tags))
                        .size(file.getSize())
                        .extension(ImageExtension.valueOf(MediaType.valueOf(file.getContentType())))
                        .file(file.getBytes())
                        .build();

        service.save(image);

        return ResponseEntity.ok().build();
    }
}
