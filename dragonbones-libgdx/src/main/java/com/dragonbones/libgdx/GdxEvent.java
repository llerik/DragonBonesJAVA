package com.dragonbones.libgdx;

import com.dragonbones.animation.AnimationState;
import com.dragonbones.armature.Armature;
import com.dragonbones.armature.Bone;
import com.dragonbones.armature.Slot;
import com.dragonbones.event.EventObject;
import com.dragonbones.event.EventStringType;
import com.dragonbones.libgdx.compat.EgretEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Egret event.
 *
 * @version DragonBones 4.5
 * @language zh_CN
 */
class GdxEvent extends EgretEvent {
    /**
     * 事件对象。
     *
     * @version DragonBones 4.5
     * @language zh_CN
     * @see EventObject
     */
    public EventObject getEventObject() {
        return this.data;
    }

    /**
     * @see #getEventObject()
     * @see EventObject#animationState
     * @deprecated Obsolete, please refer to @see
     */
    public String getAnimationName() {
        AnimationState animationState = this.getEventObject().animationState;
        return animationState != null ? animationState.name : "";
    }

    /**
     * @see #getEventObject()
     * @see EventObject#armature
     * @deprecated Obsolete, please refer to @see
     */
    public Armature getArmature() {
        return this.getEventObject().armature;
    }

    /**
     * @see #getEventObject()
     * @see EventObject#bone
     * @deprecated Obsolete, please refer to @see
     */
    @Nullable
    public Bone getBone() {
        return this.getEventObject().bone;
    }

    /**
     * @see #getEventObject()
     * @see EventObject#slot
     * @deprecated Obsolete, please refer to @see
     */
    @Nullable
    public Slot getSlot() {
        return this.getEventObject().slot;
    }

    /**
     * @see #getEventObject()
     * @see EventObject#animationState
     * @deprecated Obsolete, please refer to @see
     */
    @Nullable
    public AnimationState getAnimationState() {
        return this.getEventObject().animationState;
    }

    /**
     * @see EventObject#name
     * @deprecated Obsolete, please refer to @see
     */
    public String getFrameLabel()

    {
        return this.getEventObject().name;
    }

    /**
     * @see EventObject#name
     * @deprecated Obsolete, please refer to @see
     */
    public String getSound()

    {
        return this.getEventObject().name;
    }

    /**
     * @see #getAnimationName()
     * @deprecated Obsolete, please refer to @see
     */
    public String getMovementID()

    {
        return this.getAnimationName();
    }
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#START
     */
    public static EventStringType START =EventObject.START;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#LOOP_COMPLETE
     */
    public static EventStringType LOOP_COMPLETE =EventObject.LOOP_COMPLETE;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#COMPLETE
     */
    public static EventStringType COMPLETE =EventObject.COMPLETE;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#FADE_IN
     */
    public static EventStringType FADE_IN =EventObject.FADE_IN;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#FADE_IN_COMPLETE
     */
    public static EventStringType FADE_IN_COMPLETE =EventObject.FADE_IN_COMPLETE;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#FADE_OUT
     */
    public static EventStringType FADE_OUT =EventObject.FADE_OUT;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#FADE_OUT_COMPLETE
     */
    public static EventStringType FADE_OUT_COMPLETE =EventObject.FADE_OUT_COMPLETE;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#FRAME_EVENT
     */
    public static EventStringType FRAME_EVENT =EventObject.FRAME_EVENT;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#SOUND_EVENT
     */
    public static EventStringType SOUND_EVENT =EventObject.SOUND_EVENT;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#FRAME_EVENT
     */
    public static EventStringType ANIMATION_FRAME_EVENT =EventObject.FRAME_EVENT;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#FRAME_EVENT
     */
    public static EventStringType BONE_FRAME_EVENT =EventObject.FRAME_EVENT;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#FRAME_EVENT
     */
    public static EventStringType MOVEMENT_FRAME_EVENT =EventObject.FRAME_EVENT;
    /**
     * @deprecated
     * Obsolete, please refer to @see
     * @see EventObject#SOUND_EVENT
     */
    public static final EventStringType SOUND =EventObject.SOUND_EVENT;
}