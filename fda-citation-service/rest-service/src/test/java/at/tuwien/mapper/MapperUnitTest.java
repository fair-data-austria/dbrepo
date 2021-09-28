package at.tuwien.mapper;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.DepositChangeResponseDto;
import at.tuwien.api.zenodo.deposit.DepositResponseDto;
import at.tuwien.config.ReadyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MapperUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void listDeposits_success() throws IOException {
        final String json = FileUtils.readFileToString(ResourceUtils.getFile("classpath:zenodo-deposits-list.json"));

        /* test */
        final DepositResponseDto[] response = objectMapper.readValue(json, DepositResponseDto[].class);
        assertEquals(10, response.length);
    }

    @Test
    public void storeDeposits_success() throws IOException, URISyntaxException {
        final String json = FileUtils.readFileToString(ResourceUtils.getFile("classpath:zenodo-deposits-store.json"));

        /* test */
        final DepositChangeResponseDto response = objectMapper.readValue(json, DepositChangeResponseDto.class);
        assertEquals(926290, response.getConceptRecId());
        assertEquals(0, response.getFiles().size());
        assertEquals(926291, response.getId());
        assertEquals(new URI("https://sandbox.zenodo.org/api/files/dbea7621-8308-45af-b6af-9e12394dcc1b"), response.getLinks().getBucket());
        assertEquals(new URI("https://sandbox.zenodo.org/deposit/926291"), response.getLinks().getHtml());
        assertEquals(new URI("https://sandbox.zenodo.org/api/deposit/depositions/926291/actions/discard"), response.getLinks().getDiscard());
        assertEquals(new URI("https://sandbox.zenodo.org/api/deposit/depositions/926291/actions/edit"), response.getLinks().getEdit());
        assertEquals(new URI("https://sandbox.zenodo.org/api/deposit/depositions/926291/files"), response.getLinks().getFiles());
        assertEquals(new URI("https://sandbox.zenodo.org/api/deposit/depositions/926291/actions/publish"), response.getLinks().getPublish());
        assertEquals(new URI("https://sandbox.zenodo.org/api/deposit/depositions/926291"), response.getLinks().getSelf());
        assertEquals("10.5072/zenodo.926291", response.getMetadata().getPrereserveDoi().getDoi());
        assertEquals(93513L, response.getOwner());
        assertEquals(926291L, response.getRecordId());
        assertEquals("unsubmitted", response.getState());
        assertEquals(false, response.getSubmitted());
        assertEquals("", response.getTitle());
    }


}