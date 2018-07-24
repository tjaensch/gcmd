package gov.noaa.onestop.gcmd.controller;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GcmdController {

    @Autowired
    GcmdService service;

    @RequestMapping("/gcmd")
    public List<String> gcmdPage(){
        return service.findXmlFiles();
    }

}
