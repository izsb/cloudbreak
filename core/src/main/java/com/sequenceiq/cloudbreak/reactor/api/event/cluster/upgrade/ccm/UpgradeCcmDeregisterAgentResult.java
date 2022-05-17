package com.sequenceiq.cloudbreak.reactor.api.event.cluster.upgrade.ccm;

import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;

public class UpgradeCcmDeregisterAgentResult extends StackEvent {

    public UpgradeCcmDeregisterAgentResult(Long stackId) {
        super(stackId);
    }

    public UpgradeCcmDeregisterAgentResult(String selector, Long stackId) {
        super(selector, stackId);
    }
}