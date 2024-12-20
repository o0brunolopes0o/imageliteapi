package io.github.brunolopes.imageliteapi.application.images;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class ImageDTO {
    private String id;
    private String url;
    private  String name;
    private String extension;
    private Long size;
    private LocalDate uploadDate;
}
