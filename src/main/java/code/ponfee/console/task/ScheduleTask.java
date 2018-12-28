/* __________              _____                                          *\
** \______   \____   _____/ ____\____   ____        Ponfee's code         **
**  |     ___/  _ \ /    \   __\/ __ \_/ __ \       (c) 2017-2018, MIT    **
**  |    |  (  <_> )   |  \  | \  ___/\  ___/       http://www.ponfee.cn  **
**  |____|   \____/|___|  /__|  \___  >\___  >                            **
**                      \/          \/     \/                             **
\*                                                                        */

package code.ponfee.console.task;

import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Ponfee
 */
@Configuration
public class ScheduleTask {

    //@Scheduled(cron = "0/2 * * * * ?")
    public void test1() {
        System.out.println("任务1：执行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("任务1：执行完成");
    }

}
