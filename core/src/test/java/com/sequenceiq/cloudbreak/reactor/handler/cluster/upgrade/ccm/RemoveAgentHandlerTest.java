package com.sequenceiq.cloudbreak.reactor.handler.cluster.upgrade.ccm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.cloudbreak.core.flow2.cluster.ccm.upgrade.UpgradeCcmService;
import com.sequenceiq.cloudbreak.orchestrator.exception.CloudbreakOrchestratorException;
import com.sequenceiq.cloudbreak.orchestrator.exception.CloudbreakOrchestratorFailedException;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.upgrade.ccm.UpgradeCcmRemoveAgentRequest;
import com.sequenceiq.common.api.type.Tunnel;
import com.sequenceiq.flow.reactor.api.handler.HandlerEvent;

@ExtendWith(MockitoExtension.class)
class RemoveAgentHandlerTest {

    private static final Long STACK_ID = 12L;

    private static final Long CLUSTER_ID = 34L;

    @Mock
    private UpgradeCcmService upgradeCcmService;

    @Mock
    private HandlerEvent<UpgradeCcmRemoveAgentRequest> event;

    @InjectMocks
    private RemoveAgentHandler underTest;

    @BeforeEach
    void setUp() {
    }

    @Test
    void selector() {
        assertThat(underTest.selector()).isEqualTo("UPGRADECCMREMOVEAGENTREQUEST");
    }

    @Test
    void doAccept() throws CloudbreakOrchestratorException {
        UpgradeCcmRemoveAgentRequest request = new UpgradeCcmRemoveAgentRequest(STACK_ID, CLUSTER_ID, Tunnel.CCM);
        when(event.getData()).thenReturn(request);

        Selectable result = underTest.doAccept(event);
        verify(upgradeCcmService).removeAgent(STACK_ID, Tunnel.CCM);
        assertThat(result.selector()).isEqualTo("UPGRADECCMREMOVEAGENTRESULT");
    }

    @Test
    void orchestrationException() throws CloudbreakOrchestratorException {
        UpgradeCcmRemoveAgentRequest request = new UpgradeCcmRemoveAgentRequest(STACK_ID, CLUSTER_ID, Tunnel.CCM);
        when(event.getData()).thenReturn(request);
        doThrow(new CloudbreakOrchestratorFailedException("salt error")).when(upgradeCcmService).removeAgent(any(), any());

        Selectable result = underTest.doAccept(event);
        verify(upgradeCcmService).removeAgent(STACK_ID, Tunnel.CCM);
        assertThat(result.selector()).isEqualTo("UPGRADECCMFAILEDEVENT");
    }

}