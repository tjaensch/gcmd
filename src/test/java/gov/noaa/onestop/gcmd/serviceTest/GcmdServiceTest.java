package gov.noaa.onestop.gcmd.serviceTest;

import static org.junit.Assert.*;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.util.List;


public class GcmdServiceTest {
    GcmdService gcmdService = new GcmdService();

    @Test
    public void test_findXmlFiles() {
        List<String> results = gcmdService.findXmlFiles();
        assertThat(results, hasSize(5));

    }


}
