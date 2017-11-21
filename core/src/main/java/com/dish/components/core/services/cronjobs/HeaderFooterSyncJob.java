package com.dish.components.core.services.cronjobs;

import com.dish.components.core.services.GetHeaderFooterNav;
import com.dish.components.core.services.PersistNavToJCR;
import org.apache.felix.scr.annotations.*;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by narreddy on 11/1/17.
 */

@Component(label = "Dish Header/Footer Sync Job",
            description = "Dish Header/Footer Sync Job",
            metatype = true)
@Service(value = Runnable.class)
@Properties({
        @Property(label = "Quartz Cron Expression",
                    description = "Quartz Scheduler specific cron expression(http://www.docjar.com/docs/api/org/quartz/CronTrigger.html)",
                    name = "scheduler.expression",
                    value = "0 0 8,20 * * ?"),
        @Property(
                label = "Allow concurrent executions",
                description = "Allow concurrent executions of this Scheduled Service",
                name = "scheduler.concurrent",
                boolValue = false,
                propertyPrivate = true
        ),
        @Property(label = "Job Enabled?", description = "Turn on or off the Dish Header/Footer sync job",
                name = "header.footer.sync.enabled",
                boolValue = false
        )
})

/**
 * This job will sync the Header and Footer from the existing Dish environment.
 * We can force it to run anytime we want to by adjusting the cron expression. To make this job run
 * and sync the header and footer we need to enable the Job.
 */
public class HeaderFooterSyncJob implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(HeaderFooterSyncJob.class);
    private static final String JOB_ENABLED = "header.footer.sync.enabled";
    private boolean jobEnabled = false;
    String headerNav = null;
    String headerBodyEle = null;
    String footerBodyEle = null;

    @Reference
    PersistNavToJCR persistNavToJCR;

    @Reference
    GetHeaderFooterNav getMenu;

    /**
     * The job will run and sets the header and footer navigation menu.
     */
    public void run() {
        log.info("Inside Run Method==");
        if (jobEnabled) {
            log.info("Running Dish Header/Footer Sync Job");
            headerNav = getMenu.getHeaderNavigationMenu();
            headerBodyEle = getMenu.getHeaderBodyElements();
            footerBodyEle =getMenu.getFooterBodyElements();
            if( (headerNav != null) && (headerBodyEle != null) && (footerBodyEle != null) ) {
                log.debug("== Inside Not Null ANd Moving to Persist Data == ");
                persistNavToJCR.persistNav(headerNav, headerBodyEle, footerBodyEle);
                log.debug(" == Data Persisted == ");
            }

        }
    }

    /**
     * This overide activate method will fetch the component context of this service to read the
     * Job Enabled variable to run it.
     * @param context
     */
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
