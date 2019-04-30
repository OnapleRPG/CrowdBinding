package com.onaple.crowdbinding.event;

import com.onaple.crowdbinding.data.Group;

public interface TargetGroupEvent {

    /**
     * Gets the target {@link Group}.
     *
     * @return The group
     */
    Group getTargetGroup();
}
