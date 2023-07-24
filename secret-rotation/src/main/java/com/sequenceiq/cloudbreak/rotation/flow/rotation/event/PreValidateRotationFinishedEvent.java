package com.sequenceiq.cloudbreak.rotation.flow.rotation.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sequenceiq.cloudbreak.rotation.RotationFlowExecutionType;
import com.sequenceiq.cloudbreak.rotation.SecretType;
import com.sequenceiq.flow.event.EventSelectorUtil;

public class PreValidateRotationFinishedEvent extends RotationEvent {

    @JsonCreator
    public PreValidateRotationFinishedEvent(@JsonProperty("selector") String selector,
            @JsonProperty("resourceId") Long resourceId,
            @JsonProperty("resourceCrn") String resourceCrn,
            @JsonProperty("secretType") SecretType secretType,
            @JsonProperty("executionType") RotationFlowExecutionType executionType) {
        super(selector, resourceId, resourceCrn, secretType, executionType);
    }

    public static PreValidateRotationFinishedEvent fromPayload(RotationEvent payload) {
        return new PreValidateRotationFinishedEvent(EventSelectorUtil.selector(PreValidateRotationFinishedEvent.class),
                payload.getResourceId(), payload.getResourceCrn(), payload.getSecretType(), payload.getExecutionType());
    }
}