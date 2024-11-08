package com.sequenceiq.cloudbreak.reactor.handler.cluster;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.eventbus.Event;
import com.sequenceiq.cloudbreak.eventbus.EventBus;
import com.sequenceiq.cloudbreak.reactor.api.event.cluster.DeregisterServicesRequest;
import com.sequenceiq.cloudbreak.sdx.common.PlatformAwareSdxConnector;
import com.sequenceiq.cloudbreak.sdx.common.model.SdxBasicView;
import com.sequenceiq.cloudbreak.service.stack.StackDtoService;
import com.sequenceiq.cloudbreak.view.StackView;

@ExtendWith(MockitoExtension.class)
class DeregisterServicesHandlerTest {

    private static final Long TEST_STACK_ID = 1L;

    @Mock
    private EventBus eventBus;

    @Mock
    private PlatformAwareSdxConnector platformAwareSdxConnector;

    @Mock
    private StackDtoService stackDtoService;

    @InjectMocks
    private DeregisterServicesHandler underTest;

    private Event<DeregisterServicesRequest> event = new Event<>(new DeregisterServicesRequest(TEST_STACK_ID));

    @Test
    void testAcceptEvent() {
        when(platformAwareSdxConnector.getSdxBasicViewByEnvironmentCrn(anyString())).thenReturn(Optional.of(SdxBasicView.builder().withCrn("sdxCrn").build()));
        StackView stack = mock(StackView.class);
        when(stackDtoService.getStackViewById(any())).thenReturn(stack);
        when(stack.getResourceCrn()).thenReturn("dhCrn");
        when(stack.getEnvironmentCrn()).thenReturn("envCrn");
        doNothing().when(platformAwareSdxConnector).tearDownDatahub(any(), any());

        underTest.accept(event);

        verify(platformAwareSdxConnector).tearDownDatahub(any(), any());
    }

    @Test
    void testAcceptEventWhenNoSdx() {
        when(platformAwareSdxConnector.getSdxBasicViewByEnvironmentCrn(anyString())).thenReturn(Optional.empty());
        StackView stack = mock(StackView.class);
        when(stackDtoService.getStackViewById(any())).thenReturn(stack);
        when(stack.getEnvironmentCrn()).thenReturn("envCrn");

        underTest.accept(event);

        verify(platformAwareSdxConnector, never()).tearDownDatahub(any(), any());
    }

    @Test
    void testAcceptEventThrowsException() {
        when(stackDtoService.getStackViewById(anyLong())).thenReturn(new Stack());
        when(platformAwareSdxConnector.getSdxBasicViewByEnvironmentCrn(any())).thenThrow(new RuntimeException());

        underTest.accept(event);

        verify(eventBus, times(1)).notify(any(), any(Event.class));
    }
}