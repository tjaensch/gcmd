package gov.noaa.onestop.gcmd.controller;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class GcmdController {

    @Autowired
    GcmdService service;
    private String xmlUrl;

    @RequestMapping("/gcmd")
    public List<String> gcmdPage(){
        return service.find_xml_files();
    }

    // Format http://localhost:8080/source_xml?url=https://data.nodc.noaa.gov/nodc/archive/metadata/approved/iso/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml
    @RequestMapping(value="source_xml", method = RequestMethod.GET)
    public void get_url_value(@RequestParam("url") String urlValue) throws IOException {

        xmlUrl = urlValue;

        /* InputStream in = new URL( urlValue ).openStream();
        try {
            IOUtils.toString(in, "UTF-8");;
            return xmlString;
        } finally {
            IOUtils.closeQuietly(in);
        } */
    }

    @RequestMapping("/blah")
    public String blahPage(){
        return xmlUrl;
    }

}
