package gov.noaa.onestop.gcmd.controllerTest;

import static org.junit.Assert.*;

import gov.noaa.onestop.gcmd.controller.GcmdController;
import org.junit.Test;

import java.util.List;

public class GcmdControllerTest {

    @Test
    public void test_gcmdPage() {
        GcmdController gcmdController = new GcmdController();

        List<String> fileList = gcmdController.gcmdPage();
        assertFalse(fileList.isEmpty());
    }



}
