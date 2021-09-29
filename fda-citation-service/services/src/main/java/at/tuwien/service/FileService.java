package at.tuwien.service;

import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.api.zenodo.files.FileUploadDto;
import at.tuwien.exception.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public interface FileService {

    FileResponseDto createResource(Long databaseId, Long tableId, FileUploadDto data, File resource)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoFileTooLargeException, MetadataDatabaseNotFoundException;

    List<FileResponseDto> listAll(Long databaseId, Long tableId) throws MetadataDatabaseNotFoundException, ZenodoAuthenticationException, ZenodoNotFoundException, ZenodoApiException;
}
