package gov.noaa.onestop.gcmd.controller;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class GcmdController {

    @Autowired
    GcmdService service;
    public Document xmlDocument;

    public GcmdController() {
    }

    // Format http://localhost:8080/gcmd_keywords?url=https://data.nodc.noaa
    // .gov/nodc/archive/metadata/approved/iso/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml
    @RequestMapping(value = "/gcmd_keywords", method = RequestMethod.GET, produces = "application/json")
    public List<HashMap<String, ArrayList>> get_url_value(@RequestParam("url") URL urlValue) throws Exception {
        URL xmlUrl = urlValue;
        xmlDocument = service.get_xml_document(xmlUrl);
        List<HashMap<String, ArrayList>> all = new ArrayList<HashMap<String, ArrayList>>();

        all.add(service.get_all_keywords());
        all.add(service.get_all_invalid_keywords());
        all.add(service.get_suggestions_for_invalid_theme_keywords());
        all.add(service.get_suggestions_for_invalid_datacenter_keywords());
        all.add(service.get_suggestions_for_invalid_place_keywords());
        all.add(service.get_suggestions_for_invalid_platform_keywords());
        all.add(service.get_suggestions_for_invalid_instrument_keywords());
        all.add(service.get_suggestions_for_invalid_project_keywords());

        return all;
    }

}
