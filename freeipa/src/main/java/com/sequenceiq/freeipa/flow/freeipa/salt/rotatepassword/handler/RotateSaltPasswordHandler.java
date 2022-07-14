package com.sequenceiq.freeipa.flow.freeipa.salt.rotatepassword.handler;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.common.event.Selectable;
import com.sequenceiq.flow.event.EventSelectorUtil;
import com.sequenceiq.flow.reactor.api.handler.ExceptionCatcherEventHandler;
import com.sequenceiq.flow.reactor.api.handler.HandlerEvent;
import com.sequenceiq.freeipa.entity.Stack;
import com.sequenceiq.freeipa.flow.freeipa.salt.rotatepassword.event.RotateSaltPasswordFailureResponse;
import com.sequenceiq.freeipa.flow.freeipa.salt.rotatepassword.event.RotateSaltPasswordRequest;
import com.sequenceiq.freeipa.flow.freeipa.salt.rotatepassword.event.RotateSaltPasswordSuccessResponse;
import com.sequenceiq.freeipa.orchestrator.RotateSaltPasswordService;
import com.sequenceiq.freeipa.service.stack.StackService;

import reactor.bus.Event;

@Component
public class RotateSaltPasswordHandler extends ExceptionCatcherEventHandler<RotateSaltPasswordRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RotateSaltPasswordHandler.class);

    @Inject
    private StackService stackService;

    @Inject
    private RotateSaltPasswordService rotateSaltPasswordService;

    @Override
    public String selector() {
        return EventSelectorUtil.selector(RotateSaltPasswordRequest.class);
    }

    @Override
    protected Selectable defaultFailureEvent(Long resourceId, Exception e, Event<RotateSaltPasswordRequest> event) {
        LOGGER.warn("Fallback to default failure event for exception", e);
        return new RotateSaltPasswordFailureResponse(resourceId, e);
    }

    @Override
    protected Selectable doAccept(HandlerEvent<RotateSaltPasswordRequest> event) {
        Long stackId = event.getData().getResourceId();
        try {
            Stack stack = stackService.getStackById(stackId);
            rotateSaltPasswordService.rotateSaltPassword(stack);
            return new RotateSaltPasswordSuccessResponse(stackId);
        } catch (Exception e) {
            LOGGER.warn("Failed to rotate salt password", e);
            return new RotateSaltPasswordFailureResponse(stackId, e);
        }
    }
}