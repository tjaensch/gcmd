package gov.noaa.onestop.gcmd.controller;

import gov.noaa.onestop.gcmd.service.GcmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
public class GcmdController {

    @Autowired
    GcmdService service;

    @RequestMapping("/gcmd")
    @ResponseBody
    public List<String> gcmdPage(){
        return service.findXmlFiles();
    }

}
