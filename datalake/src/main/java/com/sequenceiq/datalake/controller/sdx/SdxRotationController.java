package com.sequenceiq.datalake.controller.sdx;

import static com.sequenceiq.authorization.resource.AuthorizationResourceAction.ROTATE_DL_SECRETS;
import static com.sequenceiq.authorization.resource.AuthorizationVariableType.CRN;
import static com.sequenceiq.cloudbreak.auth.crn.CrnResourceDescriptor.ENVIRONMENT;
import static com.sequenceiq.cloudbreak.auth.crn.CrnResourceDescriptor.VM_DATALAKE;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.stereotype.Controller;

import com.sequenceiq.authorization.annotation.CheckPermissionByRequestProperty;
import com.sequenceiq.authorization.annotation.CheckPermissionByResourceCrn;
import com.sequenceiq.authorization.annotation.InternalOnly;
import com.sequenceiq.authorization.annotation.RequestObject;
import com.sequenceiq.authorization.annotation.ResourceCrn;
import com.sequenceiq.cloudbreak.auth.security.internal.InitiatorUserCrn;
import com.sequenceiq.cloudbreak.auth.security.internal.TenantAwareParam;
import com.sequenceiq.cloudbreak.rotation.SecretType;
import com.sequenceiq.cloudbreak.rotation.annotation.ValidMultiSecretType;
import com.sequenceiq.cloudbreak.rotation.service.notification.SecretRotationNotificationService;
import com.sequenceiq.cloudbreak.structuredevent.rest.annotation.AccountEntityType;
import com.sequenceiq.cloudbreak.validation.ValidCrn;
import com.sequenceiq.datalake.entity.SdxCluster;
import com.sequenceiq.datalake.service.rotation.SdxRotationService;
import com.sequenceiq.flow.api.model.FlowIdentifier;
import com.sequenceiq.sdx.api.endpoint.SdxRotationEndpoint;
import com.sequenceiq.sdx.api.model.SdxChildResourceMarkingRequest;
import com.sequenceiq.sdx.api.model.SdxSecretRotationRequest;
import com.sequenceiq.sdx.api.model.SdxSecretTypeResponse;

@Controller
@AccountEntityType(SdxCluster.class)
public class SdxRotationController implements SdxRotationEndpoint {

    @Inject
    private SdxRotationService sdxRotationService;

    @Inject
    private SecretRotationNotificationService notificationService;

    @Inject
    private List<SecretType> enabledSecretTypes;

    @Override
    @CheckPermissionByRequestProperty(type = CRN, path = "crn", action = ROTATE_DL_SECRETS)
    public FlowIdentifier rotateSecrets(@TenantAwareParam @RequestObject SdxSecretRotationRequest request) {
        return sdxRotationService.triggerSecretRotation(request.getCrn(), request.getSecrets(), request.getExecutionType(), request.getAdditionalProperties());
    }

    @Override
    @InternalOnly
    public boolean checkOngoingMultiSecretChildrenRotationsByParent(@ValidCrn(resource = ENVIRONMENT) String parentCrn,
            @ValidMultiSecretType String multiSecret,
            @InitiatorUserCrn String initiatorUserCrn) {
        return sdxRotationService.checkOngoingMultiSecretChildrenRotations(parentCrn, multiSecret);
    }

    @Override
    @InternalOnly
    public void markMultiClusterChildrenResourcesByParent(@Valid SdxChildResourceMarkingRequest request,
            @InitiatorUserCrn String initiatorUserCrn) {
        sdxRotationService.markMultiClusterChildrenResources(request.getParentCrn(), request.getSecret());
    }

    @Override
    @CheckPermissionByResourceCrn(action = ROTATE_DL_SECRETS)
    public List<SdxSecretTypeResponse> listRotatableSdxSecretType(
            @ValidCrn(resource = VM_DATALAKE) @ResourceCrn @NotEmpty @TenantAwareParam String datalakeCrn) {
        // further improvement needed to query secret types for resource
        return enabledSecretTypes.stream()
                .map(type -> new SdxSecretTypeResponse(type.value(), notificationService.getMessage(type)))
                .toList();
    }
}
