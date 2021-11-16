package com.sequenceiq.cloudbreak.service;

import static org.slf4j.LoggerFactory.getLogger;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.DetailedStackStatus;
import com.sequenceiq.cloudbreak.api.endpoint.v4.common.Status;
import com.sequenceiq.cloudbreak.cloud.model.CloudPlatformVariant;
import com.sequenceiq.cloudbreak.cloud.store.InMemoryStateStore;
import com.sequenceiq.cloudbreak.converter.scheduler.StatusToPollGroupConverter;
import com.sequenceiq.cloudbreak.domain.SecurityConfig;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.domain.stack.StackStatus;
import com.sequenceiq.cloudbreak.service.securityconfig.SecurityConfigService;
import com.sequenceiq.cloudbreak.service.stack.StackService;

@Component
public class StackUpdater {

    private static final Logger LOGGER = getLogger(StackUpdater.class);

    @Inject
    private StackService stackService;

    @Inject
    private StatusToPollGroupConverter statusToPollGroupConverter;

    @Inject
    private SecurityConfigService securityConfigService;

    public Stack updateStackStatus(Long stackId, DetailedStackStatus detailedStatus) {
        return doUpdateStackStatus(stackId, detailedStatus, "");
    }

    public Stack updateStackStatus(Long stackId, DetailedStackStatus detailedStatus, String statusReason) {
        return doUpdateStackStatus(stackId, detailedStatus, statusReason);
    }

    public void updateStackSecurityConfig(Stack stack, SecurityConfig securityConfig) {
        securityConfig = securityConfigService.save(securityConfig);
        stack.setSecurityConfig(securityConfig);
        stackService.save(stack);
    }

    public Stack updateClusterProxyRegisteredFlag(Stack stack, boolean registered) {
        stack.setClusterProxyRegistered(registered);
        return stackService.save(stack);
    }

    public Stack updateStackVersion(Long stackId, String stackVersion) {
        Stack stack = stackService.getByIdWithTransaction(stackId);
        stack.setStackVersion(stackVersion);
        return stackService.save(stack);
    }

    private Stack doUpdateStackStatus(Long stackId, DetailedStackStatus detailedStatus, String statusReason) {
        Stack stack = stackService.getByIdWithTransaction(stackId);
        Status status = detailedStatus.getStatus();
        if (!stack.isDeleteCompleted()) {
            stack.setStackStatus(new StackStatus(stack, status, statusReason, detailedStatus));
            if (status.isRemovableStatus()) {
                InMemoryStateStore.deleteStack(stackId);
                if (stack.getCluster() != null && stack.getCluster().getStatus().isRemovableStatus()) {
                    InMemoryStateStore.deleteCluster(stack.getCluster().getId());
                }
            } else {
                InMemoryStateStore.putStack(stackId, statusToPollGroupConverter.convert(status));
            }
            stack = stackService.save(stack);
        }
        return stack;
    }

    public void updateVariant(Long resourceId, String variant) {
        CloudPlatformVariant stackVariant = stackService.getPlatformVariantByStackId(resourceId);
        if (!variant.equals(stackVariant.getVariant().value())) {
            Stack stack = stackService.get(resourceId);
            stack.setPlatformVariant(variant);
            stackService.save(stack);
        } else {
            LOGGER.info("The variant was already set to {}", variant);
        }
    }
}
