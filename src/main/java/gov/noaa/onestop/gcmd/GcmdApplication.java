package gov.noaa.onestop.gcmd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GcmdApplication {

    public static void main(String[] args) {
        SpringApplication.run(GcmdApplication.class, args);
    }

}
