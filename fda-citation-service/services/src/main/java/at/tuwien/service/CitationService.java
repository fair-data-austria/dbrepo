package at.tuwien.service;

import at.tuwien.api.zenodo.deposit.DepositDto;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CitationService {
    List<DepositDto> listCitations() throws ZenodoAuthenticationException, ZenodoApiException;

    DepositDto storeCitation() throws ZenodoAuthenticationException, ZenodoApiException;

    DepositDto deleteCitation(Long id) throws ZenodoAuthenticationException, ZenodoApiException;
}
