package com.synet.server.gateway.configuration;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import feign.MethodMetadata;
import feign.Target;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.cloud.CloudReactiveFeign;

import static com.netflix.hystrix.HystrixCommandKey.Factory.asKey;

@Configuration
public class FeignDefaultConfiguration {

    static final int VOLUME_THRESHOLD = 20;
    public static final int UPDATE_INTERVAL = 5; // 健康监测中断频率
    public static final int SLEEP_WINDOW = 1000; // 即在熔断开关打开后，在该时间窗口允许有一次重试
    public static int SEMAPHORE_MAX = 3000;
    public static int EXECUTION_TIMEOUT = 5000;

    @Bean
    public CloudReactiveFeign.SetterFactory setterFactory() {
        return new CloudReactiveFeign.SetterFactory() {
            @Override
            public HystrixObservableCommand.Setter create(Target<?> target, MethodMetadata methodMetadata) {
                return HystrixObservableCommand.Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(target.name()))
                        .andCommandKey(asKey(methodMetadata.configKey()))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                //test parameter to make circuit breaker status updated frequently
                                .withMetricsHealthSnapshotIntervalInMilliseconds(UPDATE_INTERVAL)
                                //test parameter to make circuit breaker opened after small number of errors
                                .withCircuitBreakerRequestVolumeThreshold(VOLUME_THRESHOLD)
                                .withCircuitBreakerSleepWindowInMilliseconds(SLEEP_WINDOW)
                                .withExecutionTimeoutInMilliseconds(EXECUTION_TIMEOUT)
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(SEMAPHORE_MAX)
                        );
            }
        };
    }
}
