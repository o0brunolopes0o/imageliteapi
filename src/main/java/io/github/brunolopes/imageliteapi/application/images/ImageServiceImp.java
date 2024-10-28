package io.github.brunolopes.imageliteapi.application.images;

import io.github.brunolopes.imageliteapi.domain.entity.Image;
import io.github.brunolopes.imageliteapi.domain.service.ImageService;
import io.github.brunolopes.imageliteapi.infra.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService {

    private final ImageRepository repository;

    @Override
    @Transactional
    public Image save(Image image) {
        return repository.save(image);
    }
}
