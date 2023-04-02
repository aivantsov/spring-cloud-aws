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
package io.awspring.cloud.autoconfigure;

import io.awspring.cloud.autoconfigure.metrics.CloudWatchMetricPublisherConfigurer;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.metrics.publishers.cloudwatch.CloudWatchMetricPublisher;

import org.springframework.boot.test.context.assertj.AssertableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricPublisherAssertions {
	private MetricPublisherAssertions() {
		throw new AssertionError("Not instantiable");
	}

	public static <T extends SdkClient> void assertMetricPublisherConfigured(
			AssertableApplicationContext context, Class<T> clientClass) {
		assertThat(context).hasSingleBean(CloudWatchMetricPublisherConfigurer.class);
		ConfiguredAwsClient client = new ConfiguredAwsClient(context.getBean(clientClass));
		assertThat(client.getMetricPublishers()).hasSize(1)
				.hasOnlyElementsOfType(CloudWatchMetricPublisher.class);
	}

	public static <T extends SdkClient> void assertNoMetricPublishers(AssertableApplicationContext context,
			Class<T> clientClass) {
		assertThat(new ConfiguredAwsClient(context.getBean(clientClass)).getMetricPublishers()).isEmpty();
	}
}
