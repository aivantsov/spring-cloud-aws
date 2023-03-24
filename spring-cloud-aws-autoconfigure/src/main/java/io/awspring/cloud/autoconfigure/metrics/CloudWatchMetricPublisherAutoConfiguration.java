/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.awspring.cloud.autoconfigure.metrics;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.metrics.publishers.cloudwatch.CloudWatchMetricPublisher;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

/**
 * Configuration to aggregate and upload service client metrics to Amazon CloudWatch.
 *
 * @author Andrei Ivantsov
 * @since 3.0.0
 */
@AutoConfiguration
@ConditionalOnClass({CloudWatchMetricPublisher.class, CloudWatchAsyncClient.class})
@ConditionalOnProperty(value = "spring.cloud.aws.cloudwatch.enabled", matchIfMissing = true)
public class CloudWatchMetricPublisherAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CloudWatchMetricPublisher cloudWatchMetricPublisher(CloudWatchAsyncClient client) {
        return CloudWatchMetricPublisher.builder()
                .cloudWatchClient(client)
                .build();
    }
}
