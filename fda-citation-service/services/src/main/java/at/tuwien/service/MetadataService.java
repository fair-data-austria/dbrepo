package at.tuwien.service;

import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MetadataService {
    List<DepositResponseDto> listCitations() throws ZenodoAuthenticationException, ZenodoApiException;

    DepositChangeResponseDto storeCitation() throws ZenodoAuthenticationException, ZenodoApiException;

    DepositChangeResponseDto updateCitation(Long id, DepositChangeRequestDto data) throws ZenodoAuthenticationException,
            ZenodoApiException, ZenodoNotFoundException;

    void deleteCitation(Long id) throws ZenodoAuthenticationException, ZenodoApiException;
}
