package cz.zcu.fav.kiv.authenticator.jobs;

import cz.zcu.fav.kiv.authenticator.entit.JwtTokenProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Jiri Trefil
 * Job that deletes invalid tokens from token map
 * we dont want to hold expired tokens in memory for no reason
 * removing tokens from has to happen in monithor metod - large overhead
 * this job should not be scheduled to run too often - can potentially cause performance issues
 */
@Configuration
@EnableScheduling
public class TokenRemovalScheduler {
    //run once every 1 hour
    //cron expressions are weird, just google the syntax and semantics
    //this expression says - run this job at 00 seconds and 00 minutes every hour
    @Scheduled(cron = "0 0 * * * ?")
    public void removeTokens(){
        JwtTokenProvider.removeExpiredTokens();
    }


}
