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

import io.awspring.cloud.autoconfigure.core.AwsClientBuilderConfigurer;
import io.awspring.cloud.autoconfigure.core.AwsClientCustomizer;
import io.awspring.cloud.autoconfigure.core.CredentialsProviderAutoConfiguration;
import io.awspring.cloud.autoconfigure.core.RegionProviderAutoConfiguration;
import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import software.amazon.awssdk.metrics.publishers.cloudwatch.CloudWatchMetricPublisher;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClientBuilder;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for exporting metrics to CloudWatch.
 *
 * @author Jon Schneider
 * @author Dawid Kublik
 * @author Jan Sauer
 * @author Eddú Meléndez
 * @since 2.0.0
 */
@AutoConfiguration
@AutoConfigureBefore({ CompositeMeterRegistryAutoConfiguration.class, SimpleMetricsExportAutoConfiguration.class })
@AutoConfigureAfter({ CredentialsProviderAutoConfiguration.class, RegionProviderAutoConfiguration.class,
		MetricsAutoConfiguration.class })
@EnableConfigurationProperties({ CloudWatchRegistryProperties.class, CloudWatchProperties.class })
@ConditionalOnProperty(value = "spring.cloud.aws.cloudwatch.enabled", matchIfMissing = true)
@ConditionalOnClass({ CloudWatchAsyncClient.class, CloudWatchMeterRegistry.class, AwsRegionProvider.class })
public class CloudWatchExportAutoConfiguration {
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(prefix = "management.metrics.export.cloudwatch", name = "namespace")
	static class CloudWatchMeterRegistryConfiguration {

		@Bean
		public CloudWatchMeterRegistry cloudWatchMeterRegistry(CloudWatchConfig config, Clock clock,
				CloudWatchAsyncClient client) {
			return new CloudWatchMeterRegistry(config, clock, client);
		}

		@Bean
		@ConditionalOnMissingBean
		public CloudWatchConfig cloudWatchConfig(CloudWatchRegistryProperties cloudWatchProperties) {
			return new CloudWatchPropertiesConfigAdapter(cloudWatchProperties);
		}

		@Bean
		@ConditionalOnMissingBean
		public Clock micrometerClock() {
			return Clock.SYSTEM;
		}

	}

	@Bean
	@ConditionalOnMissingBean
	public CloudWatchAsyncClient cloudWatchAsyncClient(CloudWatchProperties properties,
			AwsClientBuilderConfigurer awsClientBuilderConfigurer,
			ObjectProvider<AwsClientCustomizer<CloudWatchAsyncClientBuilder>> configurer) {
		return awsClientBuilderConfigurer
				.configure(CloudWatchAsyncClient.builder(), properties, configurer.getIfAvailable()).build();
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(value = "spring.cloud.aws.cloudwatch.metric-publisher.enabled", matchIfMissing = true)
	static class CloudWatchMetricPublisherConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public CloudWatchMetricPublisher cloudWatchMetricPublisher(CloudWatchAsyncClient client) {
			return CloudWatchMetricPublisher.builder().cloudWatchClient(client).build();
		}

		@Bean
		@ConditionalOnMissingBean
		public CloudWatchMetricPublisherConfigurer metricPublisherConfigurer(CloudWatchMetricPublisher metricPublisher) {
			return new CloudWatchMetricPublisherConfigurer(metricPublisher);
		}

	}

}
