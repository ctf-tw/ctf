package kl.tw.ctf.web.rest;

import kl.tw.ctf.CtfApp;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Test class for the DataUpload REST controller.
 *
 * @see FilesResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CtfApp.class)
@Ignore
public class FilesResourceIntTest {

    private MockMvc restMockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        FilesResource filesResource = new FilesResource();
        restMockMvc = MockMvcBuilders
            .standaloneSetup(filesResource)
            .build();
    }

    /**
    * Test fileUpload
    */
    @Test
    public void testFileUpload() throws Exception {
        restMockMvc.perform(post("/api/files"))
            .andExpect(status().isOk());
    }
    /**
    * Test fileDownload
    */
    @Test
    public void testFileDownload() throws Exception {
        restMockMvc.perform(get("/api/files"))
            .andExpect(status().isOk());
    }

}
