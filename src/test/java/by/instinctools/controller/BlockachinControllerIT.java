package by.instinctools.controller;

import by.instinctools.rest.dto.RawTransactionDto;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.web3j.protocol.ObjectMapperFactory.getObjectMapper;

public class BlockachinControllerIT extends AbstractControllerIT {

    private static final String URL_REQUEST_TEMPLATE = "http://localhost:8545";

    @Test
    public void restInsertRecords_call_wrongWfTrigger_fromLeads_expect_500_Exception() throws Exception {

        final RawTransactionDto dto = new RawTransactionDto();

        getMockMvc().perform(post(URL_REQUEST_TEMPLATE)
                .content(getObjectMapper().writeValueAsString(dto))
                .accept(APPLICATION_JSON_UTF8_VALUE)
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().is(INTERNAL_SERVER_ERROR.value()))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", containsString("An error occurred. Please contact support. Error-ID:")));
    }
}
