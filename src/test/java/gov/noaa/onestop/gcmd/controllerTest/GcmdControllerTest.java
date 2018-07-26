package gov.noaa.onestop.gcmd.controllerTest;

import gov.noaa.onestop.gcmd.controller.GcmdController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class GcmdControllerTest {

    @Autowired
    private WebApplicationContext ctx;
    private MockMvc mockMvc;

    @Before
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx).build();
    }

    GcmdController gcmdController = new GcmdController();

    @Test
    public void test_gcmdPage() {
        List<String> fileList = gcmdController.gcmdPage();
        assertFalse(fileList.isEmpty());
    }

    public void test_get_url_value() throws Exception {
        String urlValue = "https://data.nodc.noaa.gov/nodc/archive/metadata/approved/iso/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml";
        mockMvc.perform(get("/source_xml?url=https://data.nodc.noaa.gov/nodc/archive/metadata/approved/iso/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml")
        ).andDo(print())
                .andExpect(status().isOk());
    }

}
