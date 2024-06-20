package skhu.skhuAdventure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skhu.skhuAdventure.entity.ImageEntity;
import skhu.skhuAdventure.repository.ImageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageHandlerService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageHandlerService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<String> getImageUrlsByUserId(String userId) {
        List<ImageEntity> imageEntities = imageRepository.findByUserId(userId);
        return imageEntities.stream()
                .map(ImageEntity::getImageUri)
                .collect(Collectors.toList());
    }
}