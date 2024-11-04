package io.github.brunolopes.imageliteapi.application.images;

import io.github.brunolopes.imageliteapi.domain.entity.Image;
import io.github.brunolopes.imageliteapi.domain.enums.ImageExtension;
import io.github.brunolopes.imageliteapi.domain.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
@Tag(name = "Images", description = "Endpoint para gerenciamento de imagens")
@RestController
@RequestMapping("/v1/images")
@Slf4j
@RequiredArgsConstructor

public class ImagesController {

    private final ImageService service;
    private final ImageMapper mapper;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(
            summary = "Salva uma nova imagem",
            description = "Envia uma imagem para ser salva no servidor"
    )
    public ResponseEntity<?> save(
            @Parameter(description = "Arquivo da imagem", required = true, content = @Content(mediaType = "multipart" +
                    "/form-data"))
            @RequestPart("file") MultipartFile file,

            @Parameter(description = "Nome da imagem", required = true)
            @RequestParam("name") String name,

            @Parameter(description = "Tags associadas à imagem")
            @RequestParam("tags") List<String> tags
    ) throws IOException {

        long maxFileSize = 7 * 1024 * 1024;
        double getSizeFile = file.getSize() / (1024.0 * 1024.0);

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

    @GetMapping("{id}")
    @Operation(
            summary = "Obtém uma imagem pelo ID",
            description = "Recupera a imagem armazenada com base no ID fornecido.")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id){
        var possibleImage = service.getById(id);
        if(possibleImage.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        var image = possibleImage.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(image.getExtension().getMediaType());
        headers.setContentLength(image.getSize());
        headers.setContentDispositionFormData("inline; filename=\"" + image.getFileName() + "\"" ,
                image.getFileName());

        return new ResponseEntity<>(image.getFile(), headers, HttpStatus.OK);
    }

    @GetMapping
    @Operation(
            summary = "Pesquisa imagens",
            description = "Realiza uma busca por imagens filtrando por extensão e termo de pesquisa."
    )
    public ResponseEntity<List<ImageDTO>> search(
            @Parameter(description = "Extensão do arquivo")
            @RequestParam(value = "extension", required = false, defaultValue = "") String extension,

            @Parameter(description = "Nome/Tag da imagem a ser procurada")
            @RequestParam(value = "query", required = false) String query){

        var result = service.search(ImageExtension.ofName(extension), query);

        var images = result.stream().map(image -> {
            var url = buildImageURL(image);
            return mapper.imageToDTO(image, url.toString());
        }).collect(Collectors.toList());

        return ResponseEntity.ok(images);
    }

    private URI buildImageURL(Image image){
        String imagePath = "/" + image.getId();
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path(imagePath)
                .build()
                .toUri();

        log.info("URL montada: {}", uri);
        return uri;
    }
}
