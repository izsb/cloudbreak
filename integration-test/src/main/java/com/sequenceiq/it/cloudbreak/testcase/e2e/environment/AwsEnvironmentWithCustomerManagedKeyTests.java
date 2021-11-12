package com.sequenceiq.it.cloudbreak.testcase.e2e.environment;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.environment.api.v1.environment.model.response.DetailedEnvironmentResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.EnvironmentStatus;
import com.sequenceiq.it.cloudbreak.EnvironmentClient;
import com.sequenceiq.it.cloudbreak.assertion.Assertion;
import com.sequenceiq.it.cloudbreak.client.CredentialTestClient;
import com.sequenceiq.it.cloudbreak.client.EnvironmentTestClient;
import com.sequenceiq.it.cloudbreak.cloud.v4.aws.AwsProperties;
import com.sequenceiq.it.cloudbreak.context.Description;
import com.sequenceiq.it.cloudbreak.context.TestContext;
import com.sequenceiq.it.cloudbreak.dto.credential.CredentialTestDto;
import com.sequenceiq.it.cloudbreak.dto.environment.EnvironmentTestDto;
import com.sequenceiq.it.cloudbreak.dto.telemetry.TelemetryTestDto;
import com.sequenceiq.it.cloudbreak.testcase.e2e.AbstractE2ETest;
import com.sequenceiq.it.cloudbreak.util.spot.UseSpotInstances;

public class AwsEnvironmentWithCustomerManagedKeyTests extends AbstractE2ETest {

    @Inject
    private EnvironmentTestClient environmentTestClient;

    @Inject
    private CredentialTestClient credentialTestClient;

    @Inject
    private AwsProperties awsProperties;

    @Override
    protected void setupTest(TestContext testContext) {
        checkCloudPlatform(CloudPlatform.AWS);
        createDefaultUser(testContext);
    }

    @Test(dataProvider = TEST_CONTEXT)
    @UseSpotInstances
    @Description(
            given = "there is a running cloudbreak",
            when = "create an Environment with encryption parameters",
            then = "should use encryption parameters for resource encryption.")
    public void testEnvironmentWithCustomerManagedKey(TestContext testContext) {
        String encryptionKeyArn = awsProperties.getDiskEncryptionKey();
        testContext
                .given(CredentialTestDto.class)
                .when(credentialTestClient.create())
                .given("telemetry", TelemetryTestDto.class)
                .withLogging()
                .withReportClusterLogs()
                .given(EnvironmentTestDto.class)
                .withNetwork()
                .withAwsResourceEncryptionParameters(encryptionKeyArn)
                .withTelemetry("telemetry")
                .withCreateFreeIpa(Boolean.FALSE)
                .when(environmentTestClient.create())
                .await(EnvironmentStatus.AVAILABLE)
                .then((tc, testDto, cc) -> environmentTestClient.describe().action(tc, testDto, cc))
                .then(verifyEncryptionParameters())
                .validate();
    }

    private static Assertion<EnvironmentTestDto, EnvironmentClient> verifyEncryptionParameters() {
        return (testContext, testDto, environmentClient) -> {
            DetailedEnvironmentResponse environment = environmentClient.getDefaultClient().environmentV1Endpoint().getByName(testDto.getName());
            if (CloudPlatform.AWS.name().equals(environment.getCloudPlatform())) {
                if (StringUtils.isEmpty(environment.getAws().getAwsDiskEncryptionParameters().getEncryptionKeyArn())) {
                    throw new IllegalArgumentException("Failed to get encryption key details");
                }
            }
            return testDto;
        };
    }
}