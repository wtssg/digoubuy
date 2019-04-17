package wtssg.xdly.digoubuyschedulerservice.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

public class StockSimpleJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println(shardingContext.getTaskId() +
                "----------------测试--------------" + shardingContext.getJobName());
    }
}
