package io.github.brunolopes.imageliteapi.infra.repository;

import io.github.brunolopes.imageliteapi.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository  extends JpaRepository<Image, String> {

}
