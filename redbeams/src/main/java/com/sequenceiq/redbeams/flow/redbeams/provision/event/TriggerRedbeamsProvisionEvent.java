package com.sequenceiq.redbeams.flow.redbeams.provision.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sequenceiq.cloudbreak.common.mappable.ProviderParametersBase;
import com.sequenceiq.redbeams.flow.redbeams.common.RedbeamsEvent;

public class TriggerRedbeamsProvisionEvent extends RedbeamsEvent {

    private final ProviderParametersBase networkParameters;

    @JsonCreator
    public TriggerRedbeamsProvisionEvent(@JsonProperty("selector")String selector,
            @JsonProperty("resourceId")Long resourceId,
            @JsonProperty("networkParameters")ProviderParametersBase networkParameters) {
        super(selector, resourceId);
        this.networkParameters = networkParameters;
    }

    public ProviderParametersBase getNetworkParameters() {
        return networkParameters;
    }

    @Override
    public String toString() {
        return "TriggerRedbeamsProvisionEvent{" +
                "networkParameters=" + networkParameters +
                "} " + super.toString();
    }
}