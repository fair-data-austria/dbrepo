package at.tuwien.service;

import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MetadataService {

    /**
     * List all deposits (e.g. datasets) available
     *
     * @return The deposists
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     */
    List<DepositResponseDto> listCitations() throws ZenodoAuthenticationException, ZenodoApiException;

    /**
     * Create a new deposit
     *
     * @return The created deposit
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     */
    DepositChangeResponseDto storeCitation() throws ZenodoAuthenticationException, ZenodoApiException;

    /**
     * Update a deposit with new metadata for a given id
     *
     * @param id   The id
     * @param data The new metadata
     * @return The updated deposit
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     * @throws ZenodoNotFoundException       The deposit id was not found on the remote server
     */
    DepositChangeResponseDto updateCitation(Long id, DepositChangeRequestDto data) throws ZenodoAuthenticationException,
            ZenodoApiException, ZenodoNotFoundException;

    /**
     * Delete a deposit from a given id
     *
     * @param id The id
     * @throws ZenodoAuthenticationException Token invalid
     * @throws ZenodoApiException            Something other went wrong
     */
    void deleteCitation(Long id) throws ZenodoAuthenticationException, ZenodoApiException;
}
