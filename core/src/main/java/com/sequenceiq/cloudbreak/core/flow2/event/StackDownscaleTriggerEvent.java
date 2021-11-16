package com.sequenceiq.cloudbreak.core.flow2.event;

import java.util.Set;

import com.sequenceiq.cloudbreak.common.event.AcceptResult;

import reactor.rx.Promise;

public class StackDownscaleTriggerEvent extends StackScaleTriggerEvent {

    private final Set<Long> privateIds;

    public StackDownscaleTriggerEvent(String selector, Long stackId, String hostGroup, Integer adjustment, String triggeredStackVariant) {
        super(selector, stackId, hostGroup, adjustment, triggeredStackVariant);
        privateIds = null;
    }

    public StackDownscaleTriggerEvent(String selector, Long stackId, String instanceGroup, Set<Long> privateIds,
            String triggeredStackVariant) {
        super(selector, stackId, instanceGroup, null, triggeredStackVariant);
        this.privateIds = privateIds;
    }

    public StackDownscaleTriggerEvent(String selector, Long stackId, String instanceGroup, Set<Long> privateIds, String triggeredStackVariant,
            Promise<AcceptResult> accepted) {
        super(selector, stackId, instanceGroup, null, triggeredStackVariant, accepted);
        this.privateIds = privateIds;
    }

    @Override
    public StackDownscaleTriggerEvent setRepair() {
        super.setRepair();
        return this;
    }

    public Set<Long> getPrivateIds() {
        return privateIds;
    }
}
