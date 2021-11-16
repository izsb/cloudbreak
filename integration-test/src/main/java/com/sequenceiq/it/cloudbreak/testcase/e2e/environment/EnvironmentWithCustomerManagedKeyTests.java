package com.sequenceiq.it.cloudbreak.testcase.e2e.environment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.sequenceiq.cloudbreak.cloud.context.CloudContext;
import com.sequenceiq.cloudbreak.cloud.model.CloudResource;
import com.sequenceiq.cloudbreak.cloud.notification.PersistenceNotifier;
import com.sequenceiq.cloudbreak.cloud.notification.model.ResourcePersisted;
import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.environment.api.v1.environment.model.response.DetailedEnvironmentResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.EnvironmentStatus;
import com.sequenceiq.it.cloudbreak.EnvironmentClient;
import com.sequenceiq.it.cloudbreak.assertion.Assertion;
import com.sequenceiq.it.cloudbreak.client.CredentialTestClient;
import com.sequenceiq.it.cloudbreak.client.EnvironmentTestClient;
import com.sequenceiq.it.cloudbreak.context.Description;
import com.sequenceiq.it.cloudbreak.context.TestContext;
import com.sequenceiq.it.cloudbreak.dto.credential.CredentialTestDto;
import com.sequenceiq.it.cloudbreak.dto.environment.EnvironmentTestDto;
import com.sequenceiq.it.cloudbreak.dto.telemetry.TelemetryTestDto;
import com.sequenceiq.it.cloudbreak.testcase.e2e.AbstractE2ETest;
import com.sequenceiq.it.cloudbreak.util.spot.UseSpotInstances;

public class EnvironmentWithCustomerManagedKeyTests extends AbstractE2ETest {

    private static final String ENCRYPTION_KEY_URL = "https://juhig-keyvault.vault.azure.net/keys/juhig-key/c648437323ad45e59041fa1d5e9307d7";

    private static final String ENCRYPTION_KEY_URL_RESOURCE_GROUP = "juhig";

    @Inject
    private EnvironmentTestClient environmentTestClient;

    @Inject
    private CredentialTestClient credentialTestClient;

    @Override
    protected void setupTest(TestContext testContext) {
        PersistenceNotifier persistenceNotifier = Mockito.mock(PersistenceNotifier.class);
        checkCloudPlatform(CloudPlatform.AZURE);
        createDefaultUser(testContext);
        when(persistenceNotifier.notifyAllocation(any(CloudResource.class), any(CloudContext.class))).thenReturn(new ResourcePersisted());
    }

    @Test(dataProvider = TEST_CONTEXT)
    @UseSpotInstances
    @Description(
            given = "there is a running cloudbreak",
            when = "create an Environment with encryption parameters where key and environment are in different Resource groups",
            then = "should use encryption parameters for resource encryption.")
    public void testEnvironmentWithCustomerManagedKeyAndDifferentResourceGroup(TestContext testContext) {

        testContext
                .given(CredentialTestDto.class)
                .when(credentialTestClient.create())
                .given("telemetry", TelemetryTestDto.class)
                .withLogging()
                .withReportClusterLogs()
                .given(EnvironmentTestDto.class)
                .withNetwork()
                .withResourceGroup("USE_MULTIPLE", null)
                .withAzureResourceEncryptionParameters(ENCRYPTION_KEY_URL, ENCRYPTION_KEY_URL_RESOURCE_GROUP)
                .withTelemetry("telemetry")
                .withCreateFreeIpa(Boolean.TRUE)
                .when(environmentTestClient.create())
                .await(EnvironmentStatus.AVAILABLE)
                .then((tc, testDto, cc) -> environmentTestClient.describe().action(tc, testDto, cc))
                .then(verifyEncryptionParameters())
                .validate();
    }

    private static Assertion<EnvironmentTestDto, EnvironmentClient> verifyEncryptionParameters() {
        return (testContext, testDto, environmentClient) -> {
            DetailedEnvironmentResponse environment = environmentClient.getDefaultClient().environmentV1Endpoint().getByName(testDto.getName());
            if (CloudPlatform.AZURE.name().equals(environment.getCloudPlatform())) {
                if (StringUtils.isEmpty(environment.getAzure().getResourceEncryptionParameters().getDiskEncryptionSetId())) {
                    throw new IllegalArgumentException("Failed to create disk encryption set.");
                }
            }
            return testDto;
        };
    }
}
