package org.springframework.samples.petclinic.owner;

import org.assertj.core.util.Lists;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for {@link OwnerController}
 *
 * @author Apurv Kamalapuri
 */
@RunWith(SpringRunner.class)
@WebMvcTest(OwnerController.class)
public class OwnerControllerTests {

    private static final int TEST_OWNER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner george;

    @Before
    public void setup() {
        george = new Owner();
        george.setId(TEST_OWNER_ID);
        george.setFirstName("George");
        george.setLastName("Franklin");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");
        given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
    }

    @Test
    public void testProcessCreationFormSuccess() throws Exception {

        String json = "{\n" +
            "\"firstName\": \"Joe\",\n" +
            "\"lastName\": \"Bloggs\",\n" +
            "\"address\": \"123 Caramer Street\",\n" +
            "\"city\": \"London\",\n" +
            "\"telephone\": \"21341234\"\n" +
            "}";

        mockMvc.perform(post("/owners/new")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());
    }

    @Test
    public void testProcessFindFormSuccess() throws Exception {
        given(this.owners.findByLastName("Franklin")).willReturn(Lists.newArrayList(george, new Owner(), george));

        mockMvc.perform(get("/owners/Franklin")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].firstName", Matchers.is("George")))
            .andExpect(jsonPath("$.[0].lastName", Matchers.is("Franklin")))
            .andExpect(jsonPath("$.[0].address", Matchers.is("110 W. Liberty St.")))
            .andExpect(jsonPath("$.[0].city", Matchers.is("Madison")))
            .andExpect(jsonPath("$.[0].telephone", Matchers.is("6085551023")))
            .andExpect(jsonPath("$.*", Matchers.hasSize(3)));
    }
}
