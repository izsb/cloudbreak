package com.sequenceiq.environment.environment.service;

import static com.sequenceiq.common.model.OsType.CENTOS7;
import static com.sequenceiq.common.model.OsType.RHEL8;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.auth.altus.EntitlementService;
import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.common.provider.ProviderPreferencesService;
import com.sequenceiq.common.model.OsType;
import com.sequenceiq.environment.api.v1.environment.OsTypeToOsTypeResponseConverter;
import com.sequenceiq.environment.api.v1.environment.model.response.OsTypeResponse;
import com.sequenceiq.environment.api.v1.environment.model.response.SupportedOperatingSystemResponse;

@Service
public class SupportedOperatingSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportedOperatingSystemService.class);

    private final EntitlementService entitlementService;

    private final ProviderPreferencesService providerPreferencesService;

    private final OsTypeToOsTypeResponseConverter osTypeToOsTypeResponseConverter;

    public SupportedOperatingSystemService(EntitlementService entitlementService, ProviderPreferencesService providerPreferencesService,
            OsTypeToOsTypeResponseConverter osTypeToOsTypeResponseConverter) {
        this.entitlementService = entitlementService;
        this.providerPreferencesService = providerPreferencesService;
        this.osTypeToOsTypeResponseConverter = osTypeToOsTypeResponseConverter;
    }

    public SupportedOperatingSystemResponse listSupportedOperatingSystem(String accountId, String cloudPlatform) {
        SupportedOperatingSystemResponse response = new SupportedOperatingSystemResponse();

        if (providerPreferencesService.isGovCloudDeployment()) {
            response.setOsTypes(List.of(osTypeToOsTypeResponseConverter.convert(RHEL8)));
            response.setDefaultOs(RHEL8.getOs());
            LOGGER.info("List of supported OS for gov cloud response: {}", response);
        } else {
            boolean rhel8Enabled = entitlementService.isRhel8ImageSupportEnabled(accountId)
                    && enableRhel8OnAzureForInternalTenants(accountId, cloudPlatform);
            List<OsTypeResponse> supportedOs = Arrays.stream(OsType.values()).filter(r -> rhel8Enabled || r != RHEL8)
                    .map(osTypeToOsTypeResponseConverter::convert).collect(Collectors.toList());
            response.setOsTypes(supportedOs);

            boolean rhel8Default = entitlementService.isRhel8ImagePreferred(accountId);
            if (rhel8Enabled && rhel8Default) {
                response.setDefaultOs(RHEL8.getOs());
            } else {
                response.setDefaultOs(CENTOS7.getOs());
            }
            LOGGER.info("List of supported OS. rhel8Enabled: {}, rhel8Default: {}, response: {}", rhel8Enabled, rhel8Default, response);
        }

        return response;
    }

    // This is temporary, until we publish RHEL 8 images to marketplace
    private boolean enableRhel8OnAzureForInternalTenants(String accountId, String cloudPlatform) {
        return !CloudPlatform.AZURE.equalsIgnoreCase(cloudPlatform) || entitlementService.internalTenant(accountId);
    }
}