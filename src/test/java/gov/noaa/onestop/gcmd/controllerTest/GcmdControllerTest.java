package gov.noaa.onestop.gcmd.controllerTest;

import gov.noaa.onestop.gcmd.controller.GcmdController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.xml.sax.SAXException;

import java.io.IOException;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class GcmdControllerTest {

    GcmdController gcmdController = new GcmdController();

    public GcmdControllerTest() throws IOException, SAXException {
    }

    @Test
    public void test_get_url_value() {

    }

    @Test
    public void test_show_theme_keywords() {

    }

}
