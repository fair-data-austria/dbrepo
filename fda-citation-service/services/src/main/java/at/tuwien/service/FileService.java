package at.tuwien.service;

import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface FileService {

    FileResponseDto createResource(Long id, String name, byte[] resource)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException;
}
