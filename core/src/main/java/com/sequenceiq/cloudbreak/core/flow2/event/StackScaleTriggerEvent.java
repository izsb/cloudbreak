package com.sequenceiq.cloudbreak.core.flow2.event;

import java.util.Collections;
import java.util.Set;

import com.sequenceiq.cloudbreak.common.event.AcceptResult;
import com.sequenceiq.cloudbreak.core.flow2.dto.NetworkScaleDetails;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;

import reactor.rx.Promise;

public class StackScaleTriggerEvent extends StackEvent {

    private final String instanceGroup;

    private final Integer adjustment;

    private final Set<String> hostNames;

    private final String triggeredStackVariant;

    private boolean repair;

    private NetworkScaleDetails networkScaleDetails;

    public StackScaleTriggerEvent(String selector, Long stackId, String instanceGroup, Integer adjustment, String triggeredStackVariant) {
        this(selector, stackId, instanceGroup, adjustment, Collections.emptySet(), NetworkScaleDetails.getEmpty(), triggeredStackVariant);
    }

    public StackScaleTriggerEvent(String selector, Long stackId, String instanceGroup, Integer adjustment,
            String triggeredStackVariant, Promise<AcceptResult> accepted) {
        this(selector, stackId, instanceGroup, adjustment, Collections.emptySet(), triggeredStackVariant, accepted);
    }

    public StackScaleTriggerEvent(String selector, Long stackId, String instanceGroup, Integer adjustment, Set<String> hostNames,
            NetworkScaleDetails networkScaleDetails, String triggeredStackVariant) {
        super(selector, stackId);
        this.instanceGroup = instanceGroup;
        this.adjustment = adjustment;
        this.hostNames = hostNames;
        this.networkScaleDetails = networkScaleDetails;
        this.triggeredStackVariant = triggeredStackVariant;
    }

    public StackScaleTriggerEvent(String selector, Long stackId, String instanceGroup, Integer adjustment, Set<String> hostNames,
            String triggeredStackVariant, Promise<AcceptResult> accepted) {
        super(selector, stackId, accepted);
        this.instanceGroup = instanceGroup;
        this.adjustment = adjustment;
        this.hostNames = hostNames;
        this.networkScaleDetails = new NetworkScaleDetails();
        this.triggeredStackVariant = triggeredStackVariant;
    }

    public StackScaleTriggerEvent setRepair() {
        repair = true;
        return this;
    }

    public String getInstanceGroup() {
        return instanceGroup;
    }

    public Integer getAdjustment() {
        return adjustment;
    }

    public Set<String> getHostNames() {
        return hostNames;
    }

    public boolean isRepair() {
        return repair;
    }

    public NetworkScaleDetails getNetworkScaleDetails() {
        return networkScaleDetails;
    }

    public String getTriggeredStackVariant() {
        return triggeredStackVariant;
    }
}
