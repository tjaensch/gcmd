package gov.noaa.onestop.gcmd.controller;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URL;
import java.util.List;

@RestController
public class GcmdController {

    @Autowired
    GcmdService service;

    @RequestMapping("/gcmd")
    public List<String> gcmdPage(){
        return service.findXmlFiles();
    }

    @RequestMapping("/download_xml_file")
    public void download_xml_file() throws IOException {
        URL url = new URL("https://data.nodc.noaa.gov/nodc/archive/metadata/approved/iso/GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km.xml");
        InputStream io = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(io));
        FileOutputStream fio = new FileOutputStream("file.xml");
        PrintWriter pr = new PrintWriter(fio, true);
        String data = "";
        while ((data = br.readLine()) != null) {

            pr.println(data);
        }
    }

    @RequestMapping(value="source_xml", method = RequestMethod.GET)
    public @ResponseBody String get_xml_url(@RequestParam("url") String urlValue){
        return urlValue;
    }

}
