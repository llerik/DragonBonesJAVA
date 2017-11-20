package com.dragonbones.factory;

import com.dragonbones.armature.Armature;
import com.dragonbones.armature.Bone;
import com.dragonbones.armature.IKConstraint;
import com.dragonbones.armature.Slot;
import com.dragonbones.core.BaseObject;
import com.dragonbones.core.DisplayType;
import com.dragonbones.core.DragonBones;
import com.dragonbones.model.*;
import com.dragonbones.parser.BinaryDataParser;
import com.dragonbones.parser.DataParser;
import com.dragonbones.parser.ObjectDataParser;
import com.dragonbones.util.Array;
import com.dragonbones.util.Console;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Foundations of the skeleton of the factory. (Usually only need a global factory instance)
 *
 * @version DragonBones 3.0
 * @see DragonBonesData
 * @see TextureAtlasData
 * @see ArmatureData
 * @see Armature
 */
public abstract class BaseFactory {
    /**
     * @private
     */
    protected static ObjectDataParser _objectParser = null;
    /**
     * @private
     */
    protected static BinaryDataParser _binaryParser = null;
    /**
     * Whether to open the shared search.
     * When on, when creating a skeleton,
     * you can look for skeletal data from multiple keel data or find texture data from the texture data.
     * (Usually open when sharing exported data)
     *
     * @version DragonBones 4.5
     * @language zh_CN
     * @see DragonBonesData#autoSearch
     * @see com.dragonbones.model.TextureAtlasData#autoSearch
     */
    public boolean autoSearch = false;
    /**
     * @private
     */
    protected final Map<String, DragonBonesData> _dragonBonesDataMap = new HashMap<>();
    /**
     * @private
     */
    protected final Map<String, Array<TextureAtlasData>> _textureAtlasDataMap = new HashMap<>();
    /**
     * @private
     */
    protected DragonBones _dragonBones = null;
    /**
     * @private
     */
    protected DataParser _dataParser = null;

    public BaseFactory() {
        this(null);
    }

    /**
     * Create a factory. (Usually only need a global factory instance)
     *
     * @param dataParser Keel data parser, if not set, then use the default parser.
     * @version DragonBones 3.0
     * @language zh_CN
     */
    public BaseFactory(@Nullable DataParser dataParser) {
        if (BaseFactory._objectParser == null) {
            BaseFactory._objectParser = new ObjectDataParser();
        }

        if (BaseFactory._binaryParser == null) {
            BaseFactory._binaryParser = new BinaryDataParser();
        }

        this._dataParser = dataParser != null ? dataParser : BaseFactory._objectParser;
    }

    /**
     * @private
     */
    protected boolean _isSupportMesh() {
        return true;
    }

    /**
     * @private
     */
    @Nullable
    protected TextureData _getTextureData(String textureAtlasName, String textureName) {
        if (this._textureAtlasDataMap.containsKey(textureAtlasName)) {
            for (TextureAtlasData textureAtlasData : this._textureAtlasDataMap.get(textureAtlasName)) {
                TextureData textureData = textureAtlasData.getTexture(textureName);
                if (textureData != null) {
                    return textureData;
                }
            }
        }

        if (this.autoSearch) { // Will be search all data, if the autoSearch is true.
            for (String k : this._textureAtlasDataMap.keySet()) {
                for (TextureAtlasData textureAtlasData : this._textureAtlasDataMap.get(k)) {
                    if (textureAtlasData.autoSearch) {
                        TextureData textureData = textureAtlasData.getTexture(textureName);
                        if (textureData != null) {
                            return textureData;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * @private
     */
    protected boolean _fillBuildArmaturePackage(
            BuildArmaturePackage dataPackage,
            String dragonBonesName, String armatureName, String skinName, String textureAtlasName
    ) {
        DragonBonesData dragonBonesData = null;
        ArmatureData armatureData = null;

        if (dragonBonesName.length() > 0) {
            if (this._dragonBonesDataMap.containsKey(dragonBonesName)) {
                dragonBonesData = this._dragonBonesDataMap.get(dragonBonesName);
                armatureData = dragonBonesData.getArmature(armatureName);
            }
        }

        if (armatureData == null && (dragonBonesName.length() == 0 || this.autoSearch)) { // Will be search all data, if do not give a data name or the autoSearch is true.
            for (String k : this._dragonBonesDataMap.keySet()) {
                dragonBonesData = this._dragonBonesDataMap.get(k);
                if (dragonBonesName.length() == 0 || dragonBonesData.autoSearch) {
                    armatureData = dragonBonesData.getArmature(armatureName);
                    if (armatureData != null) {
                        dragonBonesName = k;
                        break;
                    }
                }
            }
        }

        if (armatureData != null) {
            dataPackage.dataName = dragonBonesName;
            dataPackage.textureAtlasName = textureAtlasName;
            dataPackage.data = dragonBonesData;
            dataPackage.armature = armatureData;
            dataPackage.skin = null;

            if (skinName.length() > 0) {
                dataPackage.skin = armatureData.getSkin(skinName);
                if (dataPackage.skin == null && this.autoSearch) {
                    for (String k : this._dragonBonesDataMap.keySet()) {
                        DragonBonesData skinDragonBonesData = this._dragonBonesDataMap.get(k);
                        ArmatureData skinArmatureData = skinDragonBonesData.getArmature(skinName);
                        if (skinArmatureData != null) {
                            dataPackage.skin = skinArmatureData.defaultSkin;
                            break;
                        }
                    }
                }
            }

            if (dataPackage.skin == null) {
                dataPackage.skin = armatureData.defaultSkin;
            }

            return true;
        }

        return false;
    }

    /**
     * @private
     */
    protected void _buildBones(BuildArmaturePackage dataPackage, Armature armature) {
        Array<BoneData> bones = dataPackage.armature.sortedBones;
        for (int i = 0; i < bones.size(); ++i) {
            BoneData boneData = bones.get(i);
            Bone bone = BaseObject.borrowObject(Bone.class);
            bone.init(boneData);

            if (boneData.parent != null) {
                armature.addBone(bone, boneData.parent.name);
            } else {
                armature.addBone(bone);
            }

            Array<ConstraintData> constraints = boneData.constraints;
            for (int j = 0; j < constraints.size(); ++j) {
                ConstraintData constraintData = constraints.get(j);
                Bone target = armature.getBone(constraintData.target.name);
                if (target == null) {
                    continue;
                }

                // TODO more constraint type.
                IKConstraintData ikConstraintData = (IKConstraintData) constraintData;
                IKConstraint constraint = BaseObject.borrowObject(IKConstraint.class);
                Bone root = ikConstraintData.root != null ? armature.getBone(ikConstraintData.root.name) : null;
                constraint.target = target;
                constraint.bone = bone;
                constraint.root = root;
                constraint.bendPositive = ikConstraintData.bendPositive;
                constraint.scaleEnabled = ikConstraintData.scaleEnabled;
                constraint.weight = ikConstraintData.weight;

                if (root != null) {
                    root.addConstraint(constraint);
                } else {
                    bone.addConstraint(constraint);
                }
            }
        }
    }

    /**
     * @private
     */
    protected void _buildSlots(BuildArmaturePackage dataPackage, Armature armature) {
        SkinData currentSkin = dataPackage.skin;
        SkinData defaultSkin = dataPackage.armature.defaultSkin;
        if (currentSkin == null || defaultSkin == null) {
            return;
        }

        Map<String, Array<DisplayData>> skinSlots = new HashMap<>();
        for (String k : defaultSkin.displays.keySet()) {
            Array<DisplayData> displays = defaultSkin.displays.get(k);
            skinSlots.put(k, displays);
        }

        if (currentSkin != defaultSkin) {
            for (String k : currentSkin.displays.keySet()) {
                Array<DisplayData> displays = currentSkin.displays.get(k);
                skinSlots.put(k, displays);
            }
        }

        for (SlotData slotData : dataPackage.armature.sortedSlots) {
            if (!(skinSlots.containsKey(slotData.name))) {
                continue;
            }

            Array<DisplayData> displays = skinSlots.get(slotData.name);
            Slot slot = this._buildSlot(dataPackage, slotData, displays, armature);
            Array<Object> displayList = new Array<>();
            for (DisplayData displayData : displays) {
                if (displayData != null) {
                    displayList.push(this._getSlotDisplay(dataPackage, displayData, null, slot));
                } else {
                    displayList.push(null);
                }
            }

            armature.addSlot(slot, slotData.parent.name);
            slot._setDisplayList(displayList);
            slot._setDisplayIndex(slotData.displayIndex, true);
        }
    }

    /**
     * @private
     */
    protected Object _getSlotDisplay(@Nullable BuildArmaturePackage dataPackage, DisplayData displayData, @Nullable DisplayData rawDisplayData, Slot slot) {
        String dataName = dataPackage != null ? dataPackage.dataName : displayData.parent.parent.name;
        Object display = null;
        switch (displayData.type) {
            case Image:
                ImageDisplayData imageDisplayData = (ImageDisplayData) displayData;
                if (imageDisplayData.texture == null) {
                    imageDisplayData.texture = this._getTextureData(dataName, displayData.path);
                } else if (dataPackage != null && dataPackage.textureAtlasName.length() > 0) {
                    imageDisplayData.texture = this._getTextureData(dataPackage.textureAtlasName, displayData.path);
                }

                if (rawDisplayData != null && rawDisplayData.type == DisplayType.Mesh && this._isSupportMesh()) {
                    display = slot.getMeshDisplay();
                } else {
                    display = slot.getRawDisplay();
                }
                break;

            case Mesh:
                MeshDisplayData meshDisplayData = (MeshDisplayData) displayData;
                if (meshDisplayData.texture == null) {
                    meshDisplayData.texture = this._getTextureData(dataName, meshDisplayData.path);
                } else if (dataPackage != null && dataPackage.textureAtlasName.length() > 0) {
                    meshDisplayData.texture = this._getTextureData(dataPackage.textureAtlasName, meshDisplayData.path);
                }

                if (this._isSupportMesh()) {
                    display = slot.getMeshDisplay();
                } else {
                    display = slot.getRawDisplay();
                }
                break;

            case Armature:
                ArmatureDisplayData armatureDisplayData = (ArmatureDisplayData) displayData;
                Armature childArmature = this.buildArmature(armatureDisplayData.path, dataName, null, dataPackage != null ? dataPackage.textureAtlasName : null);
                if (childArmature != null) {
                    childArmature.inheritAnimation = armatureDisplayData.inheritAnimation;
                    if (!childArmature.inheritAnimation) {
                        Array<ActionData> actions = armatureDisplayData.actions.size() > 0 ? armatureDisplayData.actions : childArmature.armatureData.defaultActions;
                        if (actions.size() > 0) {
                            for (ActionData action : actions) {
                                childArmature._bufferAction(action, true);
                            }
                        } else {
                            childArmature.getAnimation().play();
                        }
                    }

                    armatureDisplayData.armature = childArmature.armatureData; //
                }

                display = childArmature;
                break;
        }

        return display;
    }

    /**
     * @private
     */
    protected void _replaceSlotDisplay(BuildArmaturePackage dataPackage, @Nullable DisplayData displayData, Slot slot, int displayIndex)

    {
        if (displayIndex < 0) {
            displayIndex = slot.getDisplayIndex();
        }

        if (displayIndex < 0) {
            displayIndex = 0;
        }

        Array<Object> displayList = slot.getDisplayList(); // Copy.
        if (displayList.size() <= displayIndex) {
            displayList.setLength(displayIndex + 1);

            // @TODO: Not required in java
            for (int i = 0, l = displayList.size(); i < l; ++i) { // Clean undefined.
                if (displayList.get(i) == null) {
                    displayList.set(i, null);
                }
            }
        }

        if (slot._displayDatas.size() <= displayIndex) {
            displayList.setLength(displayIndex + 1);

            // @TODO: Not required in java
            for (int i = 0, l = slot._displayDatas.size(); i < l; ++i) { // Clean undefined.
                if (slot._displayDatas.get(i) == null) {
                    slot._displayDatas.set(i, null);
                }
            }
        }

        slot._displayDatas.set(displayIndex, displayData);
        if (displayData != null) {
            displayList.set(displayIndex, this._getSlotDisplay(
                    dataPackage,
                    displayData,
                    displayIndex < slot._rawDisplayDatas.size() ? slot._rawDisplayDatas.get(displayIndex) : null,
                    slot
            ));
        } else {
            displayList.set(displayIndex, null);
        }

        slot.setDisplayList(displayList);
    }

    /**
     * @private
     */
    protected abstract TextureAtlasData _buildTextureAtlasData(@Nullable TextureAtlasData textureAtlasData, Object textureAtlas);

    /**
     * @private
     */
    protected abstract Armature _buildArmature(BuildArmaturePackage dataPackage);

    /**
     * @private
     */
    protected abstract Slot _buildSlot(BuildArmaturePackage dataPackage, SlotData slotData, Array<DisplayData> displays, Armature armature);

    public DragonBonesData parseDragonBonesData(Object rawData) {
        return parseDragonBonesData(rawData, null, 1f);
    }

    /**
     * Analyze and add keel data.
     *
     * @param rawData Raw data to parse
     * @param name    Give the data a name so that data can be obtained by this name, or, if not, the name in the data.
     * @returns DragonBonesData
     * @version DragonBones 4.5
     * @language zh_CN
     * @see #getDragonBonesData(String)
     * @see #addDragonBonesData(DragonBonesData, String)
     * @see #removeDragonBonesData(String, boolean)
     * @see DragonBonesData
     */
    @Nullable
    public DragonBonesData parseDragonBonesData(Object rawData, @Nullable String name, float scale) {
        DragonBonesData dragonBonesData = null;
        if (rawData instanceof byte[]) {
            dragonBonesData = BaseFactory._binaryParser.parseDragonBonesData(rawData, scale);
        } else {
            dragonBonesData = this._dataParser.parseDragonBonesData(rawData, scale);
        }

        while (true) {
            TextureAtlasData textureAtlasData = this._buildTextureAtlasData(null, null);
            if (this._dataParser.parseTextureAtlasData(null, textureAtlasData, scale)) {
                this.addTextureAtlasData(textureAtlasData, name);
            } else {
                textureAtlasData.returnToPool();
                break;
            }
        }

        if (dragonBonesData != null) {
            this.addDragonBonesData(dragonBonesData, name);
        }

        return dragonBonesData;
    }

    public TextureAtlasData parseTextureAtlasData(Object rawData, Object textureAtlas) {
        return parseTextureAtlasData(rawData, textureAtlas, null, 0f);
    }

    /**
     * Parse and add mapset data.
     *
     * @param rawData      Raw data to parse (JSON)
     * @param textureAtlas Stickers.
     * @param name         Give the data a name so that data can be obtained by this name, or, if not, the name in the data.
     * @param scale        Set a zoom value for the sticker album.
     * @returns Atlas data
     * @version DragonBones 4.5
     * @language zh_CN
     * @see #getTextureAtlasData(String)
     * @see #addTextureAtlasData(TextureAtlasData, String)
     * @see #removeTextureAtlasData(String, boolean)
     * @see TextureAtlasData
     */
    public TextureAtlasData parseTextureAtlasData(Object rawData, Object textureAtlas, @Nullable String name, float scale) {
        TextureAtlasData textureAtlasData = this._buildTextureAtlasData(null, null);
        this._dataParser.parseTextureAtlasData(rawData, textureAtlasData, scale);
        this._buildTextureAtlasData(textureAtlasData, textureAtlas);
        this.addTextureAtlasData(textureAtlasData, name);

        return textureAtlasData;
    }

    /**
     * @version DragonBones 5.1
     * @language zh_CN
     */
    public void updateTextureAtlasData(String name, Array<Object> textureAtlases) {
        Array<TextureAtlasData> textureAtlasDatas = this.getTextureAtlasData(name);
        if (textureAtlasDatas != null) {
            for (int i = 0, l = textureAtlasDatas.size(); i < l; ++i) {
                if (i < textureAtlases.size()) {
                    this._buildTextureAtlasData(textureAtlasDatas.get(i), textureAtlases.get(i));
                }
            }
        }
    }

    /**
     * Get the keel data for the specified name.
     *
     * @param name Data name.
     * @returns DragonBonesData
     * @version DragonBones 3.0
     * @language zh_CN
     * @see #parseDragonBonesData(Object, String, float)
     * @see #addDragonBonesData(DragonBonesData, String)
     * @see #removeDragonBonesData(String, boolean)
     * @see DragonBonesData
     */
    @Nullable
    public DragonBonesData getDragonBonesData(String name) {
        return this._dragonBonesDataMap.get(name);
    }

    public void addDragonBonesData(DragonBonesData data) {
        addDragonBonesData(data, null);
    }

    /**
     * Add keel data.
     *
     * @param data Keel data.
     * @param name Give the data a name so that data can be obtained by this name, or, if not, the name in the data.
     * @version DragonBones 3.0
     * @language zh_CN
     * @see #parseDragonBonesData(Object, String, float)
     * @see #getDragonBonesData(String)
     * @see #removeDragonBonesData(String, boolean)
     * @see DragonBonesData
     */
    public void addDragonBonesData(DragonBonesData data, @Nullable String name) {
        name = name != null ? name : data.name;
        if (this._dragonBonesDataMap.containsKey(name)) {
            if (this._dragonBonesDataMap.get(name) == data) {
                return;
            }

            Console.warn("Replace data: " + name);
            this._dragonBonesDataMap.get(name).returnToPool();
        }

        this._dragonBonesDataMap.put(name, data);
    }

    public void removeDragonBonesData(String name) {
        removeDragonBonesData(name, true);
    }

    /**
     * Remove keel data.
     *
     * @param name        Data name.
     * @param disposeData Whether to release the data.
     * @version DragonBones 3.0
     * @language zh_CN
     * @see #parseDragonBonesData(Object, String, float)
     * @see #getDragonBonesData(String)
     * @see #addDragonBonesData(DragonBonesData, String)
     * @see DragonBonesData
     */
    public void removeDragonBonesData(String name, boolean disposeData) {
        if (this._dragonBonesDataMap.containsKey(name)) {
            if (disposeData) {
                this._dragonBones.bufferObject(this._dragonBonesDataMap.get(name));
            }

            this._dragonBonesDataMap.remove(name);
        }
    }

    /**
     * Gets the map data for the specified name.
     *
     * @param name Data name.
     * @returns Atlas data list.
     * @version DragonBones 3.0
     * @language zh_CN
     * @see #parseTextureAtlasData(Object, Object, String, float)
     * @see #addTextureAtlasData(TextureAtlasData, String)
     * @see #removeTextureAtlasData(String, boolean)
     * @see TextureAtlasData
     */
    public Array<TextureAtlasData> getTextureAtlasData(String name) {
        return this._textureAtlasDataMap.get(name);
    }

    public void addTextureAtlasData(TextureAtlasData data) {
        addTextureAtlasData(data, null);
    }

    /**
     * Add map data.
     *
     * @param data Atlas data.
     * @param name Give the data a name so that data can be obtained by this name, or, if not, the name in the data.
     * @version DragonBones 3.0
     * @language zh_CN
     * @see #parseTextureAtlasData(Object, Object, String, float)
     * @see #getTextureAtlasData(String)
     * @see #removeTextureAtlasData(String, boolean)
     * @see TextureAtlasData
     */
    public void addTextureAtlasData(TextureAtlasData data, @Nullable String name) {
        name = name != null ? name : data.name;
        if (!this._textureAtlasDataMap.containsKey(name)) {
            this._textureAtlasDataMap.put(name, new Array<>());
        }
        Array<TextureAtlasData> textureAtlasList = this._textureAtlasDataMap.get(name);

        if (textureAtlasList.indexOf(data) < 0) {
            textureAtlasList.add(data);
        }
    }

    public void removeTextureAtlasData(String name) {
        removeTextureAtlasData(name, true);
    }

    /**
     * Remove the artwork data.
     *
     * @param name        Data name.
     * @param disposeData Whether to release the data.
     * @version DragonBones 3.0
     * @language zh_CN
     * @see #parseTextureAtlasData(Object, Object, String, float)
     * @see #getTextureAtlasData(String)
     * @see #addTextureAtlasData(TextureAtlasData, String)
     * @see TextureAtlasData
     */
    public void removeTextureAtlasData(String name, boolean disposeData) {
        if (this._textureAtlasDataMap.containsKey(name)) {
            Array<TextureAtlasData> textureAtlasDataList = this._textureAtlasDataMap.get(name);
            if (disposeData) {
                for (TextureAtlasData textureAtlasData : textureAtlasDataList) {
                    this._dragonBones.bufferObject(textureAtlasData);
                }
            }

            this._textureAtlasDataMap.remove(name);
        }
    }

    @Nullable
    public ArmatureData getArmatureData(String name) {
        return getArmatureData(name, "");
    }

    /**
     * Get skeleton data.
     *
     * @param name            Skeleton data name.
     * @param dragonBonesName Keel data name.
     * @version DragonBones 5.1
     * @language zh_CN
     * @see ArmatureData
     */
    @Nullable
    public ArmatureData getArmatureData(String name, String dragonBonesName) {
        BuildArmaturePackage dataPackage = new BuildArmaturePackage();
        if (!this._fillBuildArmaturePackage(dataPackage, dragonBonesName, name, "", "")) {
            return null;
        }

        return dataPackage.armature;
    }

    public void clear() {
        clear(true);
    }

    /**
     * Clear all data.
     *
     * @param disposeData Whether to release the data.
     * @version DragonBones 4.5
     * @language zh_CN
     */
    public void clear(boolean disposeData) {
        for (String k : this._dragonBonesDataMap.keySet()) {
            if (disposeData) {
                this._dragonBones.bufferObject(this._dragonBonesDataMap.get(k));
            }

            this._dragonBonesDataMap.remove(k);
        }

        for (String k : this._textureAtlasDataMap.keySet()) {
            if (disposeData) {
                Array<TextureAtlasData> textureAtlasDataList = this._textureAtlasDataMap.get(k);
                for (TextureAtlasData textureAtlasData : textureAtlasDataList) {
                    this._dragonBones.bufferObject(textureAtlasData);
                }
            }

            this._textureAtlasDataMap.remove(k);
        }
    }

    @Nullable
    public Armature buildArmature(String armatureName) {
        return buildArmature(armatureName, null, null, null);
    }

    /**
     * Create a skeleton.
     *
     * @param armatureName     Skeleton data name.
     * @param dragonBonesName  If the name of the keel data is not set, all the keel data will be retrieved.
     *                         When multiple keel data contain the data of the same name,
     *                         it may not be able to create an accurate skeleton.
     * @param skinName         Skin name, if not set, the default skin is used.
     * @param textureAtlasName Atlas data name, if not set, then use keel data name.
     * @returns skeleton
     * @version DragonBones 3.0
     * @language zh_CN
     * @see ArmatureData
     * @see Armature
     */
    @Nullable
    public Armature buildArmature(String armatureName, @Nullable String dragonBonesName, @Nullable String skinName, @Nullable String textureAtlasName) {
        BuildArmaturePackage dataPackage = new BuildArmaturePackage();
        if (!this._fillBuildArmaturePackage(dataPackage, (dragonBonesName != null) ? dragonBonesName : "", armatureName, (skinName != null) ? skinName : "", (textureAtlasName != null) ? textureAtlasName : "")) {
            Console.warn("No armature data. " + armatureName + ", " + (dragonBonesName != null ? dragonBonesName : ""));
            return null;
        }

        Armature armature = this._buildArmature(dataPackage);
        this._buildBones(dataPackage, armature);
        this._buildSlots(dataPackage, armature);
        // armature.invalidUpdate(null, true); TODO
        armature.invalidUpdate("", true);
        armature.advanceTime(0f); // Update armature pose.

        return armature;
    }

    public void replaceSlotDisplay(
            @Nullable String dragonBonesName,
            String armatureName, String slotName, String displayName,
            Slot slot
    ) {
        replaceSlotDisplay(dragonBonesName, armatureName, slotName, displayName, slot, -1);
    }

    /**
     * Replaces the display object for the specified slot with the specified resource.
     * (Replace the display object of "slot" with a resource of "dragonBonesName/armatureName/slotName/displayName")
     *
     * @param dragonBonesName The specified keel data name.
     * @param armatureName    The specified skeleton name.
     * @param slotName        Specified slot name.
     * @param displayName     The specified display object name.
     * @param slot            Specified slot instance.
     * @param displayIndex    The index of the display object to be replaced, if not set,
     *                        the display object currently being displayed is replaced.
     * @version DragonBones 4.5
     * @language zh_CN
     */
    public void replaceSlotDisplay(
            @Nullable String dragonBonesName,
            String armatureName, String slotName, String displayName,
            Slot slot, int displayIndex
    )

    {
        BuildArmaturePackage dataPackage = new BuildArmaturePackage();
        if (
                !this._fillBuildArmaturePackage(
                        dataPackage,
                        (dragonBonesName != null) ? dragonBonesName : "",
                        armatureName, "", ""
                ) || dataPackage.skin == null
                ) {
            return;
        }

        Array<DisplayData> displays = dataPackage.skin.getDisplays(slotName);
        if (displays == null) {
            return;
        }

        for (DisplayData display : displays) {
            if (display != null && Objects.equals(display.name, displayName)) {
                this._replaceSlotDisplay(dataPackage, display, slot, displayIndex);
                break;
            }
        }
    }

    /**
     * Replaces the slot's display object list with the specified resource list.
     *
     * @param dragonBonesName The specified DragonBonesData name.
     * @param armatureName    The specified skeleton name.
     * @param slotName        Specified slot name.
     * @param slot            Specified slot instance.
     * @version DragonBones 4.5
     * @language zh_CN
     */
    public void replaceSlotDisplayList(
            @Nullable String dragonBonesName,
            String armatureName,
            String slotName,
            Slot slot
    )

    {
        BuildArmaturePackage dataPackage = new BuildArmaturePackage();
        if (
                !this._fillBuildArmaturePackage(
                        dataPackage,
                        (dragonBonesName != null) ? dragonBonesName : "",
                        armatureName, "", ""
                ) || dataPackage.skin == null
                ) {
            return;
        }

        Array<DisplayData> displays = dataPackage.skin.getDisplays(slotName);
        if (displays == null) {
            return;
        }

        int displayIndex = 0;
        for (DisplayData displayData : displays) {
            this._replaceSlotDisplay(dataPackage, displayData, slot, displayIndex++);
        }
    }

    public void changeSkin(Armature armature, SkinData skin) {
        changeSkin(armature, skin, null);
    }

    /**
     * Change the skeleton skin.
     *
     * @param armature skeleton.
     * @param skin     Skin data.
     * @param exclude  No need for updated slots.
     * @version DragonBones 5.1
     * @language zh_CN
     * @see Armature
     * @see SkinData
     */
    public void changeSkin(Armature armature, SkinData skin, @Nullable Array<String> exclude) {
        for (Slot slot : armature.getSlots()) {
            if (!(skin.displays.containsKey(slot.name)) || (exclude != null && exclude.indexOf(slot.name) >= 0)) {
                continue;
            }

            Array<DisplayData> displays = skin.displays.get(slot.name);
            Array<Object> displayList = slot.getDisplayList(); // Copy.
            displayList.setLength(displays.size()); // Modify displayList length.
            for (int i = 0, l = displays.size(); i < l; ++i) {
                DisplayData displayData = displays.get(i);
                if (displayData != null) {
                    displayList.set(i, this._getSlotDisplay(null, displayData, null, slot));
                } else {
                    displayList.set(i, null);
                }
            }

            slot._rawDisplayDatas = displays;
            slot._displayDatas.setLength(displays.size());
            for (int i = 0, l = slot._displayDatas.size(); i < l; ++i) {
                slot._displayDatas.set(i, displays.get(i));
            }

            slot.setDisplayList(displayList);
        }
    }

    public boolean copyAnimationsToArmature(Armature toArmature, String fromArmatreName) {
        return copyAnimationsToArmature(toArmature, fromArmatreName, null, null, true);
    }

    /**
     * Animations that replace skeleton animation with other skeletons.
     * (Usually these skeletons should have the same skeleton structure)
     *
     * @param toArmature               Specified skeleton.
     * @param fromArmatreName          Other skeleton's name.
     * @param fromSkinName             The skin name of the other skeleton, if not set, the default skin is used.
     * @param fromDragonBonesDataName  Other skeletons belong to the keel data name, if not set, then retrieve all the keel data.
     * @param replaceOriginalAnimation Whether to replace the original animation of the same name.
     * @returns Whether the replacement is successful.
     * @version DragonBones 4.5
     * @language zh_CN
     * @see Armature
     * @see ArmatureData
     */
    public boolean copyAnimationsToArmature(
            Armature toArmature,
            String fromArmatreName,
            @Nullable String fromSkinName,
            @Nullable String fromDragonBonesDataName,
            boolean replaceOriginalAnimation
    )

    {
        BuildArmaturePackage dataPackage = new BuildArmaturePackage();
        if (this._fillBuildArmaturePackage(
                dataPackage,
                (fromDragonBonesDataName != null) ? fromDragonBonesDataName : "",
                fromArmatreName,
                (fromSkinName != null) ? fromSkinName : "",
                ""
        )) {
            ArmatureData fromArmatureData = dataPackage.armature;
            if (replaceOriginalAnimation) {
                toArmature.getAnimation().setAnimations(fromArmatureData.animations);
            } else {
                Map<String, AnimationData> animations = new HashMap<>();

                for (String animationName : toArmature.getAnimation().getAnimations().keySet()) {
                    animations.put(animationName, toArmature.getAnimation().getAnimations().get(animationName));
                }

                for (String animationName : fromArmatureData.animations.keySet()) {
                    animations.put(animationName, fromArmatureData.animations.get(animationName));
                }

                toArmature.getAnimation().setAnimations(animations);
            }

            if (dataPackage.skin != null) {
                Array<Slot> slots = toArmature.getSlots();
                for (int i = 0, l = slots.size(); i < l; ++i) {
                    Slot toSlot = slots.get(i);
                    Array<Object> toSlotDisplayList = toSlot.getDisplayList();
                    for (int j = 0, lJ = toSlotDisplayList.size(); j < lJ; ++j) {
                        Object toDisplayObject = toSlotDisplayList.get(j);
                        if (toDisplayObject instanceof Armature) {
                            Array<DisplayData> displays = dataPackage.skin.getDisplays(toSlot.name);
                            if (displays != null && j < displays.size()) {
                                DisplayData fromDisplayData = displays.get(j);
                                if (fromDisplayData != null && fromDisplayData.type == DisplayType.Armature) {
                                    this.copyAnimationsToArmature((Armature) toDisplayObject, fromDisplayData.path, fromSkinName, fromDragonBonesDataName, replaceOriginalAnimation);
                                }
                            }
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    /**
     * @private
     */
    public Map<String, DragonBonesData> getAllDragonBonesData() {
        return this._dragonBonesDataMap;
    }

    /**
     * @private
     */
    public Map<String, Array<TextureAtlasData>> getAllTextureAtlasData() {
        return this._textureAtlasDataMap;
    }
}
