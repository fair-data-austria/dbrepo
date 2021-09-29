package at.tuwien.service;

import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoFileTooLargeException;
import at.tuwien.exception.ZenodoNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public interface FileService {

    FileResponseDto createResource(Long id, String name, File resource)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoFileTooLargeException;
}
