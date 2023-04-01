package io.awspring.cloud.autoconfigure.metrics;

import io.awspring.cloud.autoconfigure.ConfiguredAwsClient;
import io.awspring.cloud.core.SpringCloudClientConfiguration;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.metrics.publishers.cloudwatch.CloudWatchMetricPublisher;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;

class CloudWatchMetricPublisherConfigurerTest {

	@Test
	void shouldConfigureCloudWatchMetricPublisher() {
		CloudWatchMetricPublisherConfigurer sut = new CloudWatchMetricPublisherConfigurer(
				CloudWatchMetricPublisher.create());
		ClientOverrideConfiguration clientOverrideConfiguration = new SpringCloudClientConfiguration()
				.clientOverrideConfiguration();
		DynamoDbClient client = sut.configure(DynamoDbClient.builder()
				.overrideConfiguration(clientOverrideConfiguration)).build();
		ConfiguredAwsClient configuredAwsClient = new ConfiguredAwsClient(client);

		assertThat(configuredAwsClient.getClientUserAgentSuffix()).startsWith("spring-cloud-aws");
		assertThat(configuredAwsClient.getMetricPublishers()).hasSize(1)
				.hasOnlyElementsOfType(CloudWatchMetricPublisher.class);
	}
}
