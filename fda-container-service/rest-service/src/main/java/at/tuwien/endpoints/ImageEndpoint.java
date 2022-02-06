package at.tuwien.endpoints;

import at.tuwien.api.container.image.ImageBriefDto;
import at.tuwien.api.container.image.ImageChangeDto;
import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.api.container.image.ImageDto;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageAlreadyExistsException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.exception.PersistenceException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.service.impl.ImageServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@CrossOrigin(origins = "*")
@ControllerAdvice
@RequestMapping("/api/image")
public class ImageEndpoint {

    private final ImageServiceImpl imageService;
    private final ImageMapper imageMapper;

    @Autowired
    public ImageEndpoint(ImageServiceImpl imageService, ImageMapper imageMapper) {
        this.imageService = imageService;
        this.imageMapper = imageMapper;
    }

    @GetMapping
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

    @PostMapping
    @PreAuthorize("hasRole('ROLE_DEVELOPER')")
    @ApiOperation(value = "Creates a new image", notes = "Creates a new image in the metadata database.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully created a new image."),
            @ApiResponse(code = 400, message = "Malformed payload."),
            @ApiResponse(code = 401, message = "Not authorized to create a image."),
            @ApiResponse(code = 406, message = "Image already exists in metadata database."),
            @ApiResponse(code = 404, message = "The image does not exist in the repository."),
    })
    public ResponseEntity<ImageDto> create(@Valid @RequestBody ImageCreateDto data) throws ImageNotFoundException, ImageAlreadyExistsException, DockerClientException {
        final ContainerImage image = imageService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageMapper.containerImageToImageDto(image));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get all informations about a image", notes = "Since we follow the REST-principle, this method provides more information than the findAll method.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get information about container."),
            @ApiResponse(code = 401, message = "Not authorized to get information about a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<ImageDto> findById(@NotNull @PathVariable Long id) throws ImageNotFoundException {
        final ContainerImage image = imageService.find(id);
        return ResponseEntity.ok()
                .body(imageMapper.containerImageToImageDto(image));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DEVELOPER')")
    @ApiOperation(value = "Update image information", notes = "Polls new information about an image")
    @ApiResponses({
            @ApiResponse(code = 202, message = "Updated the information of a image."),
            @ApiResponse(code = 401, message = "Not authorized to update a container."),
            @ApiResponse(code = 404, message = "No container found with this id in metadata database."),
    })
    public ResponseEntity<ImageDto> update(@NotNull @PathVariable Long id, @RequestBody @Valid ImageChangeDto changeDto)
            throws ImageNotFoundException, DockerClientException {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(imageMapper.containerImageToImageDto(imageService.update(id, changeDto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEVELOPER')")
    @ApiOperation(value = "Delete a image")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deleted the image."),
            @ApiResponse(code = 401, message = "Not authorized to delete a image."),
            @ApiResponse(code = 401, message = "Not authorized to delete a image."),
            @ApiResponse(code = 404, message = "No image found with this id in metadata database."),
    })
    public ResponseEntity<?> delete(@NotNull @PathVariable Long id) throws ImageNotFoundException,
            PersistenceException {
        imageService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
