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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/images")
@Slf4j
@RequiredArgsConstructor
public class ImagesController {

    private final ImageService service;
    private final ImageMapper mapper;

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
                    .body(String.format("O tamanho do arquivo nao pode passar dos 7MB, seu arquivo tem %.2fMB",
                            getSizeFile));
        }

        log.info("Imagem recebida: nome do arquivo: {}, size: {}", file.getOriginalFilename(), file.getSize());
        log.info("Media type {}", MediaType.valueOf(file.getContentType()));
        log.info("Content type: {}", file.getContentType());

        MediaType.valueOf(file.getContentType());

        Image image = mapper.mapToImage(file, name, tags);

        Image savedImage = service.save(image);

        URI imageUri = buildImageURL(savedImage);


        return ResponseEntity.created(imageUri).build();
    }

    private URI buildImageURL(Image image){
        String imagePath = "/" + image.getId();
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(imagePath)
                .build()
                .toUri();

        log.info("URL montada: {}", uri);
        return uri;
    }
}
