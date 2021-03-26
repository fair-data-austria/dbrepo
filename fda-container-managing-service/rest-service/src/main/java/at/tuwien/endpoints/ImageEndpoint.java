package at.tuwien.endpoints;

import at.tuwien.api.dto.container.ContainerBriefDto;
import at.tuwien.api.dto.container.ContainerChangeDto;
import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.api.dto.container.ContainerDto;
import at.tuwien.api.dto.image.ImageBriefDto;
import at.tuwien.api.dto.image.ImageCreateDto;
import at.tuwien.api.dto.image.ImageDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.ContainerImage;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.ContainerMapper;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.service.ImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static at.tuwien.api.dto.container.ContainerActionTypeDto.*;

@Log4j2
@RestController
@RequestMapping("/api/image")
public class ImageEndpoint {

    private final ImageService imageService;
    private final ContainerMapper containerMapper;
    private final ImageMapper imageMapper;

    @Autowired
    public ImageEndpoint(ImageService imageService, ContainerMapper containerMapper, ImageMapper imageMapper) {
        this.imageService = imageService;
        this.containerMapper = containerMapper;
        this.imageMapper = imageMapper;
    }

    @GetMapping("/")
    @ApiOperation(value = "List all images", notes = "Lists the images in the metadata database.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "All images are listed."),
            @ApiResponse(code = 401, message = "Not authorized to list all images."),
    })
    public ResponseEntity<List<ImageBriefDto>> findAll() {
        final List<ContainerImage> containers = imageService.getAll();
        return ResponseEntity.ok()
                .body(containers.stream()
                        .map(imageMapper::containerImageToImageBriefDto)
                        .collect(Collectors.toList()));
    }

    @PostMapping("/")
    @ApiOperation(value = "Creates a new image", notes = "Creates a new image in the metadata database.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created a new image."),
            @ApiResponse(code = 400, message = "Malformed payload."),
            @ApiResponse(code = 401, message = "Not authorized to create a image."),
    })
    public ResponseEntity<ImageDto> create(@Valid @RequestBody ImageCreateDto data) {
        final ContainerImage image = imageService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageMapper.containerImageToImageDto(image));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get all informations about a container", notes = "Since we follow the REST-principle, this method provides more information than the findAll method.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get information about container."),
            @ApiResponse(code = 401, message = "Not authorized to get information about a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<ImageDto> findById(@NotNull @RequestParam Long id) throws ImageNotFoundException {
        final ContainerImage image = imageService.getById(id);
        return ResponseEntity.ok()
                .body(imageMapper.containerImageToImageDto(image));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Change the state of a container", notes = "The new state can only be one of START/STOP/REMOVE.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Changed the state of a container."),
            @ApiResponse(code = 400, message = "Malformed payload."),
            @ApiResponse(code = 401, message = "Not authorized to modify a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<?> update(@NotNull @RequestParam Long id) throws ContainerNotFoundException, DockerClientException {

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .build();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a image")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deleted the image."),
            @ApiResponse(code = 401, message = "Not authorized to delete a image."),
            @ApiResponse(code = 404, message = "No image found with this id in metadata database."),
    })
    public ResponseEntity delete(@NotNull @RequestParam Long id) throws ImageNotFoundException {
        imageService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
