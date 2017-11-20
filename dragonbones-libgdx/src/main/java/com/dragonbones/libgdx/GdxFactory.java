package com.dragonbones.libgdx;

import com.dragonbones.animation.WorldClock;
import com.dragonbones.armature.Armature;
import com.dragonbones.armature.Slot;
import com.dragonbones.core.BaseObject;
import com.dragonbones.core.DragonBones;
import com.dragonbones.factory.BaseFactory;
import com.dragonbones.factory.BuildArmaturePackage;
import com.dragonbones.libgdx.compat.EgretBitmap;
import com.dragonbones.libgdx.compat.EgretGlobals;
import com.dragonbones.libgdx.compat.EgretMesh;
import com.dragonbones.libgdx.compat.EgretTexture;
import com.dragonbones.model.DisplayData;
import com.dragonbones.model.DragonBonesData;
import com.dragonbones.model.SlotData;
import com.dragonbones.model.TextureAtlasData;
import com.dragonbones.util.Array;
import com.dragonbones.util.Console;
import org.jetbrains.annotations.Nullable;

/**
 * Egret factory.
 *
 * @version DragonBones 3.0
 * @language zh_CN
 */
public class GdxFactory extends BaseFactory {
    private static float _time = 0;
    private static DragonBones _dragonBones = null;
    private static GdxFactory _factory = null;

    private static boolean _clockHandler(double time) {
        time *= 0.001f;
        double passedTime = time - GdxFactory._time;
        GdxFactory._dragonBones.advanceTime((float) passedTime);
        GdxFactory._time = (float) time;

        return false;
    }

    /**
     * A global WorldClock instance that you can use directly. (Engine driven)
     *
     * @version DragonBones 5.0
     * @language zh_CN
     */
    public static WorldClock getClock() {
        return GdxFactory._dragonBones.getClock();
    }

    /**
     * A direct use of the global factory instance.
     *
     * @version DragonBones 4.7
     * @language zh_CN
     */
    public static GdxFactory getFactory() {
        if (GdxFactory._factory == null) {
            GdxFactory._factory = new GdxFactory();
        }

        return GdxFactory._factory;
    }

    /**
     * @inheritDoc
     */
    public GdxFactory() {
        super();

        if (GdxFactory._dragonBones == null) {
            GdxArmatureDisplay eventManager = new GdxArmatureDisplay();
            GdxFactory._dragonBones = new DragonBones(eventManager);
            GdxFactory._dragonBones.getClock().time = (float) (EgretGlobals.getTimer() * 0.001);
            EgretGlobals.startTick(GdxFactory::_clockHandler);
        }

        this._dragonBones = GdxFactory._dragonBones;
    }

    /**
     * @private
     */
    protected boolean _isSupportMesh() {
        return true;
    }

    @Override
    protected TextureAtlasData _buildTextureAtlasData(@Nullable TextureAtlasData textureAtlasData, Object textureAtlas) {
        return _buildTextureAtlasData((GdxTextureAtlasData) textureAtlasData, textureAtlas);
    }

    /**
     * @private
     */
    //protected GdxTextureAtlasData _buildTextureAtlasData(@Nullable GdxTextureAtlasData textureAtlasData, Texture textureAtlas) {
    protected GdxTextureAtlasData _buildTextureAtlasData(@Nullable GdxTextureAtlasData textureAtlasData, Object textureAtlas) {
        if (textureAtlasData != null) {
            textureAtlasData.setRenderTexture((EgretTexture) textureAtlas);
        } else {
            textureAtlasData = BaseObject.borrowObject(GdxTextureAtlasData.class);
        }

        return textureAtlasData;
    }

    /**
     * @private
     */
    protected Armature _buildArmature(BuildArmaturePackage dataPackage) {
        Armature armature = BaseObject.borrowObject(Armature.class);
        GdxArmatureDisplay armatureDisplay = new GdxArmatureDisplay();

        armature.init(
                dataPackage.armature,
                armatureDisplay, armatureDisplay, this._dragonBones
        );

        return armature;
    }

    /**
     * @private
     */
    protected Slot _buildSlot(BuildArmaturePackage dataPackage, SlotData slotData, Array<DisplayData> displays, Armature armature) {
        //dataPackage;
        //armature;
        GdxSlot slot = BaseObject.borrowObject(GdxSlot.class);
        slot.init(
                slotData, displays,
                new EgretBitmap(), new EgretMesh()
        );

        return slot;
    }

    /**
     * Create a skeleton of the specified name.
     *
     * @param armatureName     Skeleton name.
     * @param dragonBonesName  If the name of the keel data is not set, all the keel data will be retrieved.
     *                         If multiple data contain the data of the same name, it may not be able to create an accurate skeleton.
     * @param skinName         Skin name, if not set, the default skin is used.
     * @param textureAtlasName Atlas data name, if not set, then keel data.
     * @returns Skeleton display container.
     * @version DragonBones 4.5
     * @language zh_CN
     * @see GdxArmatureDisplay
     */
    @Nullable
    public GdxArmatureDisplay buildArmatureDisplay(String armatureName, @Nullable String dragonBonesName, @Nullable String skinName, @Nullable String textureAtlasName) {
        Armature armature = this.buildArmature(armatureName, dragonBonesName, skinName, textureAtlasName);
        if (armature != null) {
            this._dragonBones.getClock().add(armature);
            return (GdxArmatureDisplay) armature.getDisplay();
        }

        return null;
    }

    @Nullable
    public GdxArmatureDisplay buildArmatureDisplay(String armatureName) {
        return buildArmatureDisplay(armatureName, null, null, null);
    }

    /**
     * Gets the display object with the specified map.
     *
     * @param textureName      The name of the specified texture.
     * @param textureAtlasName The specified album data name, if not set, will retrieve all the album data.
     * @version DragonBones 3.0
     * @language zh_CN
     */
    @Nullable
    public EgretBitmap getTextureDisplay(String textureName, @Nullable String textureAtlasName) {
        GdxTextureData textureData = (GdxTextureData) this._getTextureData(textureAtlasName != null ? textureAtlasName : "", textureName);
        if (textureData != null && textureData.renderTexture != null) {
            return new EgretBitmap(textureData.renderTexture);
        }

        return null;
    }

    public EgretBitmap getTextureDisplay(String textureName) {
        return getTextureDisplay(textureName, null);
    }

    /**
     * Get global sound event manager.
     *
     * @version DragonBones 4.5
     * @language zh_CN
     */
    public GdxArmatureDisplay getSoundEventManager() {
        return (GdxArmatureDisplay) this._dragonBones.getEventManager();
    }

    /**
     * @see BaseFactory#addDragonBonesData(DragonBonesData, String)
     * @deprecated Obsolete, please refer to @see
     */
    public void addSkeletonData(DragonBonesData dragonBonesData, @Nullable String dragonBonesName) {
        Console.warn("Obsolete, please refer to @see");
        this.addDragonBonesData(dragonBonesData, dragonBonesName);
    }

    public void addSkeletonData(DragonBonesData dragonBonesData) {
        addSkeletonData(dragonBonesData, null);
    }

    /**
     * @see BaseFactory#getDragonBonesData(String)
     * @deprecated Obsolete, please refer to @see
     */
    public DragonBonesData getSkeletonData(String dragonBonesName) {
        Console.warn("Obsolete, please refer to @see");
        return this.getDragonBonesData(dragonBonesName);
    }

    /**
     * @see BaseFactory#removeDragonBonesData(String)
     * @deprecated Obsolete, please refer to @see
     */
    public void removeSkeletonData(String dragonBonesName) {
        Console.warn("Obsolete, please refer to @see");
        this.removeDragonBonesData(dragonBonesName);
    }

    /**
     * @see BaseFactory#addTextureAtlasData(TextureAtlasData, String)
     * @deprecated Obsolete, please refer to @see
     */
    public void addTextureAtlas(TextureAtlasData textureAtlasData, @Nullable String dragonBonesName) {
        Console.warn("Obsolete, please refer to @see");
        this.addTextureAtlasData(textureAtlasData, dragonBonesName);
    }

    public void addTextureAtlas(TextureAtlasData textureAtlasData) {
        addTextureAtlas(textureAtlasData, null);
    }

    /**
     * @see BaseFactory#getTextureAtlasData(String)
     * @deprecated Obsolete, please refer to @see
     */
    public Array<TextureAtlasData> getTextureAtlas(String dragonBonesName) {
        Console.warn("Obsolete, please refer to @see");
        return this.getTextureAtlasData(dragonBonesName);
    }

    /**
     * @see BaseFactory#removeTextureAtlasData(String, boolean)
     * @deprecated Obsolete, please refer to @see
     */
    public void removeTextureAtlas(String dragonBonesName) {
        Console.warn("Obsolete, please refer to @see");
        this.removeTextureAtlasData(dragonBonesName);
    }

    /**
     * @see BaseFactory#buildArmature(String)
     * @deprecated Obsolete, please refer to @see
     */
    @Nullable
    public Armature buildFastArmature(String armatureName, @Nullable String dragonBonesName, @Nullable String skinName) {
        Console.warn("Obsolete, please refer to @see");
        return this.buildArmature(armatureName, dragonBonesName, skinName, null);
    }

    @Nullable
    public Armature buildFastArmature(String armatureName) {
        return buildFastArmature(armatureName, null, null);
    }

    /**
     * @see BaseFactory#clear()
     * @deprecated Obsolete, please refer to @see
     */
    public void dispose() {
        Console.warn("Obsolete, please refer to @see");
        this.clear();
    }

    /**
     * @see GdxFactory#getSoundEventManater()
     * @deprecated Obsolete, please refer to @see
     */
    public GdxArmatureDisplay getSoundEventManater() {
        return this.getSoundEventManager();
    }
}