/*
 * Copyright 2013-2020 the original author or authors.
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
package io.awspring.cloud.autoconfigure.core;

import io.awspring.cloud.autoconfigure.metrics.CloudWatchMetricPublisherAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.metrics.publishers.cloudwatch.CloudWatchMetricPublisher;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for the {@link AwsAutoConfiguration}.
 *
 * @author Andrei Ivantsov
 */
class AwsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withPropertyValues("spring.cloud.aws.region.static:eu-central-1")
            .withConfiguration(AutoConfigurations.of(AwsAutoConfiguration.class, CredentialsProviderAutoConfiguration.class,
                    RegionProviderAutoConfiguration.class, CloudWatchMetricPublisherAutoConfiguration.class));

    @Test
    void awsClientBuilderConfigurer() {
        this.contextRunner.withPropertyValues("spring.cloud.aws.cloudwatch.enabled:false")
                .run(context -> {
            assertThat(context).doesNotHaveBean(CloudWatchMetricPublisher.class);
            var configurer = context.getBean(AwsClientBuilderConfigurer.class);
            assertThat(configurer).isNotNull();
            var clientOverrideConfiguration = (ClientOverrideConfiguration) ReflectionTestUtils.getField(configurer,
                    "clientOverrideConfiguration");
            assertThat(clientOverrideConfiguration.advancedOption(SdkAdvancedClientOption.USER_AGENT_SUFFIX))
                    .matches(p -> p.isPresent() && p.get().startsWith("spring-cloud-aws"));
        });
    }
}
