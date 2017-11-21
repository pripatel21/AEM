package com.dish.components.core.services.cronjobs;

import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by narreddy on 11/4/17.
 */
@Component(metatype = true, label = "Test Scheduler")
@Service(value = Runnable.class)
@Properties({
        @Property(name = "scheduler.expression",
                value = "0 * * * * ?"),
        @Property(label = "Job Enabled?",
                description = "Turn on or off the Dish Header/Footer sync job",
                name = "sync.enabled",
                boolValue = false
        )
})
public class ScheduleCronJob implements Runnable {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String JOB_ENABLED = "sync.enabled";
    private boolean jobEnabled = false;

    String headerNav = null;
    String headerBodyEle = null;
    String footerBodyEle = null;

    public void run() {
        if(jobEnabled) {
            log.info("Executing a cron job");
            log.info("Successfully called Navigation Class");
        }
    }

    @Activate
    protected void activate(ComponentContext context) {
        log.info("Inside Activate Method==");
        try {
            jobEnabled = Boolean.parseBoolean(context.getProperties().get(JOB_ENABLED).toString());
            log.info("Is Job enabled ==> " + jobEnabled);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Unable to Run the Job.  Exception: " + e.getMessage());
        }
    }

}