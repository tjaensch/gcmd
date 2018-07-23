package gov.noaa.onestop.gcmd.serviceTest;

import static org.junit.Assert.*;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.junit.Test;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.util.List;


public class GcmdServiceTest {

    @Test
    public void test_findXmlFiles() {
        GcmdService gcmdService = new GcmdService();
        List<String> results = gcmdService.findXmlFiles();
        assertThat(results, hasSize(5));

    }


}
