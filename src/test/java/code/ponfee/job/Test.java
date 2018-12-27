/* __________              _____                                          *\
** \______   \____   _____/ ____\____   ____        Ponfee's code         **
**  |     ___/  _ \ /    \   __\/ __ \_/ __ \       (c) 2017-2018, MIT    **
**  |    |  (  <_> )   |  \  | \  ___/\  ___/       http://www.ponfee.cn  **
**  |____|   \____/|___|  /__|  \___  >\___  >                            **
**                      \/          \/     \/                             **
\*                                                                        */

package code.ponfee.job;

import java.lang.reflect.Constructor;

import code.ponfee.job.enums.TriggerType;
import code.ponfee.job.model.SchedJob;

/**
 * 
 * @author Ponfee
 */
public class Test {

    public static void main(String[] args) throws Exception {
        Constructor<TriggerType> c = TriggerType.class.getDeclaredConstructor(int.class, String.class);
        c.setAccessible(true);
        c.newInstance(1, "abc");
        
        
       /* Constructor<SchedJob> c2 = SchedJob.class.getDeclaredConstructor(long.class, int.class);
        c2.newInstance(1, 2);*/
        
    }
}
