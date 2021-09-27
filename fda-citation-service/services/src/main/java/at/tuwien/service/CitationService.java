package at.tuwien.service;

import at.tuwien.api.zenodo.deposit.DepositDto;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CitationService {
    List<DepositDto> listStoredCitations() throws ZenodoAuthenticationException, ZenodoApiException;

    DepositDto storeCitation() throws ZenodoAuthenticationException, ZenodoApiException;
}
