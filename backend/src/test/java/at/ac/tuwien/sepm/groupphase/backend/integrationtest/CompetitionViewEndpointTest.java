package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CompetitionViewEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompetitionRepository competitionRepository;


    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private SessionUtils sessionUtils;
    @Autowired
    private ObjectMapper objectMapper;

    private final Competition competition = new Competition(
        "Test Competition",
        LocalDateTime.of(2022, 11, 9, 8, 0),
        LocalDateTime.of(2022, 11, 10, 23, 55),
        LocalDateTime.of(2022, 11, 11, 14, 0),
        LocalDateTime.of(2022, 11, 11, 8, 0),
        "This is a test competition",
        "",
        true,
        false,
        "test@mail.com",
        "+436666660666"
    );

    @BeforeEach
    public void beforeEach() {
        //applicationUserRepository.deleteAll();
        competitionRepository.deleteAll();
    }

    @Test
    public void competition_doesExist() throws Exception {
        competitionRepository.save(competition);

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI + "/{id}",
                competition.getId()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        CompetitionViewDto resp = objectMapper.readValue(response.getContentAsString(),
            CompetitionViewDto.class);

        assertEquals(resp.name(), competition.getName());
    }

    @Test
    public void competition_DoesntExist() throws Exception {
        competitionRepository.save(competition);

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI + "/{id}",
                -1))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void competition_notInDraft() throws Exception {
        competition.setDraft(true);
        competitionRepository.save(competition);

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI + "/{id}",
                competition.getId()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
