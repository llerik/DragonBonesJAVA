package com.dragonbones.model;

import com.dragonbones.animation.AnimationState;
import com.dragonbones.armature.Armature;
import com.dragonbones.armature.Bone;
import com.dragonbones.core.AnimationFadeOutMode;
import com.dragonbones.core.BaseObject;
import com.dragonbones.core.TweenType;
import com.dragonbones.util.Array;

/**
 * 动画配置，描述播放一个动画所需要的全部信息。
 *
 * @version DragonBones 5.0
 * @beta
 * @see AnimationState
 */
public class AnimationConfig extends BaseObject {
    /**
     * Whether to pause the faded animation.
     *
     * @default true
     * @version DragonBones 5.0
     */
    public boolean pauseFadeOut;

    /**
     * Fade out mode.
     *
     * @default dragonBones.AnimationFadeOutMode.All
     * @version DragonBones 5.0
     * @see AnimationFadeOutMode
     *  
     */
    public AnimationFadeOutMode fadeOutMode;

    /**
     * Fade easing mode.
     *
     * @default TweenType.Line
     * @version DragonBones 5.0
     * @see TweenType
     *  
     */
    public TweenType fadeOutTweenType;

    /**
     * Fade out time. [-1: Synchronize with fade-in time, [0 ~ N]: Fade-out time] (in seconds)
     *
     * @default -1
     * @version DragonBones 5.0
     */
    public float fadeOutTime;

    /**
     * Can trigger behavior.
     *
     * @default true
     * @version DragonBones 5.0
     */
    public boolean actionEnabled;

    /**
     * Whether or not to mix in an incremental manner.
     *
     * @default false
     * @version DragonBones 5.0
     */
    public boolean additiveBlending;

    /**
     * Has control over the display object of the slot.
     *
     * @default true
     * @version DragonBones 5.0
     */
    public boolean displayControl;

    /**
     * Whether to pause the fade-in animation until the fade-in process is over.
     *
     * @default true
     * @version DragonBones 5.0
     */
    public boolean pauseFadeIn;

    /**
     * Whether to reset the animated object to the initial value.
     *
     * @default true
     * @version DragonBones 5.1
     * @language zh_CN
     *  
     */
    public boolean resetToPose;

    /**
     * Fade in slow mode.
     *
     * @default TweenType.Line
     * @version DragonBones 5.0
     * @see TweenType
     */
    public TweenType fadeInTweenType;

    /**
     * Views. [-1: Using movie data Default value, 0: infinite loop playback, [1 ~ N]: N times looping]
     *
     * @default -1
     * @version DragonBones 5.0
     */
    public int playTimes;

    /**
     * Mixed layers, layers will give priority to high mixing weight.
     *
     * @default 0
     * @version DragonBones 5.0
     */
    public float layer;

    /**
     * Starting time. (In seconds)
     *
     * @default 0
     * @version DragonBones 5.0
     */
    public float position;

    /**
     * duration. [-1: Using movie data Default, 0: Movie stopped, (0 ~ N]: Duration] (in seconds)
     *
     * @default -1
     * @version DragonBones 5.0
     */
    public float duration;

    /**
     * Play speed. [(-N ~ 0): Reverse Play, 0: Stop Play, (0 ~ 1): Slow Play, 1: Normal Play, (1 ~ N): Quick Play]
     *
     * @default 1
     * @version DragonBones 3.0
     */
    public float timeScale;

    /**
     * Fade in time. [-1: Using Animation Data Defaults, [0 ~ N]: Fade In Time] (in seconds)
     *
     * @default -1
     * @version DragonBones 5.0
     */
    public float fadeInTime;

    /**
     * Automatically fade out the time. [-1: do not fade out automatically, [0 ~ N]: fade out time] (in seconds)
     *
     * @default -1
     * @version DragonBones 5.0
     */
    public float autoFadeOutTime;

    /**
     * Mixed weight.
     *
     * @default 1
     * @version DragonBones 5.0
     */
    public float weight;

    /**
     * Animation status name.
     *
     * @version DragonBones 5.0
     */
    public String name;

    /**
     * Animation data name.
     *
     * @version DragonBones 5.0
     * @language zh_CN
     *  
     */
    public String animation;

    /**
     * Mixed group, used for animation state grouping, convenient control fade out.
     *
     * @version DragonBones 5.0
     *  
     */
    public String group;

    /**
     * Bone mask.
     *
     * @version DragonBones 5.0
     */
    public final Array<String> boneMask = new Array<>();

    /**
     * @private
     */
    protected void _onClear() {
        this.pauseFadeOut = true;
        this.fadeOutMode = AnimationFadeOutMode.All;
        this.fadeOutTweenType = TweenType.Line;
        this.fadeOutTime = -1f;

        this.actionEnabled = true;
        this.additiveBlending = false;
        this.displayControl = true;
        this.pauseFadeIn = true;
        this.resetToPose = true;
        this.fadeInTweenType = TweenType.Line;
        this.playTimes = -1;
        this.layer = 0;
        this.position = 0f;
        this.duration = -1f;
        this.timeScale = -100f;
        this.fadeInTime = -1f;
        this.autoFadeOutTime = -1f;
        this.weight = 1f;
        this.name = "";
        this.animation = "";
        this.group = "";
        this.boneMask.clear();
    }

    public void clear() {
        this._onClear();
    }

    public void copyFrom(AnimationConfig value) {
        this.pauseFadeOut = value.pauseFadeOut;
        this.fadeOutMode = value.fadeOutMode;
        this.autoFadeOutTime = value.autoFadeOutTime;
        this.fadeOutTweenType = value.fadeOutTweenType;

        this.actionEnabled = value.actionEnabled;
        this.additiveBlending = value.additiveBlending;
        this.displayControl = value.displayControl;
        this.pauseFadeIn = value.pauseFadeIn;
        this.resetToPose = value.resetToPose;
        this.playTimes = value.playTimes;
        this.layer = value.layer;
        this.position = value.position;
        this.duration = value.duration;
        this.timeScale = value.timeScale;
        this.fadeInTime = value.fadeInTime;
        this.fadeOutTime = value.fadeOutTime;
        this.fadeInTweenType = value.fadeInTweenType;
        this.weight = value.weight;
        this.name = value.name;
        this.animation = value.animation;
        this.group = value.group;

        this.boneMask.setLength(value.boneMask.size());
        for (int i = 0, l = this.boneMask.size(); i < l; ++i) {
            this.boneMask.set(i, value.boneMask.get(i));
        }
    }

    public boolean containsBoneMask(String name) {
        return this.boneMask.size() == 0 || this.boneMask.indexOfObject(name) >= 0;
    }

    public void addBoneMask(Armature armature, String name) {
        addBoneMask(armature, name, true);
    }

    public void addBoneMask(Armature armature, String name, boolean recursive) {
        Bone currentBone = armature.getBone(name);
        if (currentBone == null) {
            return;
        }

        if (this.boneMask.indexOfObject(name) < 0) { // Add mixing
            this.boneMask.add(name);
        }

        if (recursive) { // Add recursive mixing.
            for (Bone bone : armature.getBones()) {
                if (this.boneMask.indexOfObject(bone.name) < 0 && currentBone.contains(bone)) {
                    this.boneMask.add(bone.name);
                }
            }
        }
    }

    public void removeBoneMask(Armature armature, String name) {
        removeBoneMask(armature, name, true);
    }

    public void removeBoneMask(Armature armature, String name, boolean recursive) {
        int index = this.boneMask.indexOfObject(name);
        if (index >= 0) { // Remove mixing.
            this.boneMask.splice(index, 1);
        }

        if (recursive) {
            Bone currentBone = armature.getBone(name);
            if (currentBone != null) {
                if (this.boneMask.size() > 0) { // Remove recursive mixing.
                    for (Bone bone : armature.getBones()) {
                        int index2 = this.boneMask.indexOfObject(bone.name);
                        if (index2 >= 0 && currentBone.contains(bone)) {
                            this.boneMask.splice(index2, 1);
                        }
                    }
                } else { // Add unrecursive mixing.
                    for (Bone bone : armature.getBones()) {
                        if (bone == currentBone) {
                            continue;
                        }

                        if (!currentBone.contains(bone)) {
                            this.boneMask.add(bone.name);
                        }
                    }
                }
            }
        }
    }
}
