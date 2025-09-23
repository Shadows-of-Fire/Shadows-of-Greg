package gregicadditions.recipes;

import gregicadditions.GAConfig;
import gregicadditions.GAMaterials;
import gregicadditions.item.GAMetaItems;
import gregicadditions.item.GATransparentCasing;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.loaders.recipe.Component;
import gregtech.loaders.recipe.CraftingComponent;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.MarkerMaterials.*;

/**
 * Holder of {@link Component} used for programmatic recipe generation of voltage-tiered items.
 *
 * The entries in here are overrides relative to GTNE, or new ones only relevant to SoG machines.
 */
public class GACraftingComponents extends CraftingComponent {

	/** Better circuits for expensive recipes */
	public static final Component<UnificationEntry> BETTER_CIRCUIT = tier ->
		CIRCUIT.getIngredient(tier + 1);

	/** Quadruple Cables */
	public static final Component<UnificationEntry> CABLE_QUAD =
		bind(OrePrefix.cableGtQuadruple, CABLE_MATERIALS);

	/** Pipes used as crafting ingredients in Extruders and Pumps */
	public static final Component<UnificationEntry> PIPE = tier -> {
		OrePrefix size = switch(tier) {
			case LuV -> OrePrefix.pipeSmall;
			case ULV, LV, MV, HV, EV, IV, ZPM -> OrePrefix.pipeMedium;
			default -> OrePrefix.pipeLarge;
		};
		Material material = switch(tier) {
			case ULV, LV -> Materials.Bronze;
			case MV -> Materials.Steel;
			case HV -> Materials.StainlessSteel;
			case EV -> Materials.Titanium;
			case IV -> Materials.TungstenSteel;
			default -> Materials.Ultimet;
		};
		return new UnificationEntry(size, material);
	};

	/** Large tier-material pipes */
	public static final Component<UnificationEntry> PIPE_LARGE =
		bind(OrePrefix.pipeLarge, TIER_MATERIAL);

	/** Tiered glass */
	public static final Component<ItemStack> GLASS = tier -> switch(tier) {
		case LuV, ZPM, UV -> GATransparentCasing.CasingType.REINFORCED_GLASS.getStack();
		default -> new ItemStack(Blocks.GLASS, 1, W);
	};

	/** Plates used in select machine recipes */
	public static final Component<UnificationEntry> PLATE = tier -> {
		if(tier > ZPM)
			return new UnificationEntry(OrePrefix.plate, GAMaterials.NEUTRONIUM);
		else
			return CraftingComponent.PLATE.getIngredient(tier);
	};

	/** Heating coils used in Electric Furnaces */
	public static final Component<UnificationEntry> COIL_HEATING =
		bind(OrePrefix.wireGtDouble, CraftingComponent.COIL_MATERIAL);

	/** Double-sized heating coils used in various machine recipes. */
	public static final Component<UnificationEntry> COIL_HEATING_DOUBLE =
		bind(OrePrefix.wireGtQuadruple, CraftingComponent.COIL_MATERIAL);

	/** Radioactive rods, used for Naquadah Reactor recipes. */
	public static final Component<UnificationEntry> STICK_RADIOACTIVE = tier -> {
		Material material = switch(tier) {
			case EV -> Materials.Uranium235;
			case IV -> Materials.Plutonium241;
			case LuV -> Materials.NaquadahEnriched;
			case ZPM -> Materials.Americium;
			default -> Materials.Tritanium;
		};
		return new UnificationEntry(OrePrefix.stick, material);
	};

	/** Additional item used in place of circuits for higher tier Transformers. */
	public static final Component<MetaItem<?>.MetaValueItem> XF_ITEM = tier -> switch(tier) {
		case EV, IV -> MetaItems.SMALL_COIL;
		case LuV, ZPM -> MetaItems.POWER_INTEGRATED_CIRCUIT;
		default -> MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT;
	};

	/**
	 *  Reusable batteries including config-based overrides for ZPM and above.
	 */
	public static final Component<MetaItem<?>.MetaValueItem> GA_BATTERY = tier -> {

		if(tier == ZPM)
			if(GAConfig.GT5U.enableZPMandUVBats)
				return GAMetaItems.ENERGY_MODULE;

		if(tier == UV)
			if(GAConfig.GT5U.enableZPMandUVBats)
				return GAMetaItems.ENERGY_CLUSTER;
			else if(GAConfig.GT5U.replaceUVwithMAXBat)
				return GAMetaItems.MAX_BATTERY;

		if(tier > UV)
			if(GAConfig.GT5U.replaceUVwithMAXBat)
				return GAMetaItems.MAX_BATTERY;

		// tier below ZPM or no relevant override
		return CraftingComponent.BATTERY.getIngredient(tier);
	};

	/** Tiered Screws used in crafting recipes */
	public static final Component<ItemStack> SCREW = tier ->
		OreDictUnifier.get(OrePrefix.screw, MATERIAL_COMPONENT.getIngredient(tier));

	/** Tier metals for Assembly Line recipe components */
	public static final Component<Material> AL_METAL = tier -> switch(tier) {
		case LuV -> Materials.HSSG;
		case ZPM -> Materials.HSSE;
		default -> GAMaterials.NEUTRONIUM;
	};
	public static final Component<UnificationEntry> AL_PLATE =
		bind(OrePrefix.plate, AL_METAL);

	public static final Component<UnificationEntry> AL_GEAR =
		bind(OrePrefix.gear, AL_METAL);

	public static final Component<UnificationEntry> AL_STICK =
		bind(OrePrefix.stick, AL_METAL);

	public static final Component<UnificationEntry> AL_STICK_LONG =
		bind(OrePrefix.stickLong, AL_METAL);

	public static final Component<UnificationEntry> AL_INGOT =
		bind(OrePrefix.ingot, AL_METAL);

	public static final Component<Material> AL_CABLE_MATERIAL = tier -> switch(tier) {
		case IV -> Materials.Tungsten;
		case LuV -> Materials.YttriumBariumCuprate;
		case ZPM -> Materials.VanadiumGallium;
		case UV -> Materials.NiobiumTitanium;
		default -> CABLE_MATERIALS.getIngredient(tier);
	};

	public static final Component<UnificationEntry> AL_CABLE =
		bind(OrePrefix.cableGtSingle, AL_CABLE_MATERIAL);

	public static final Component<UnificationEntry> AL_CABLE_2x =
		bind(OrePrefix.cableGtDouble, AL_CABLE_MATERIAL);

	public static final Component<UnificationEntry> AL_WIRE_2x =
		bind(OrePrefix.wireGtDouble, AL_CABLE_MATERIAL);

	public static final Component<UnificationEntry> AL_MOTOR_FINE_WIRE = tier -> switch(tier) {
		case LuV -> new UnificationEntry(OrePrefix.wireFine, Materials.AnnealedCopper);
		case ZPM -> new UnificationEntry(OrePrefix.wireFine, Materials.Platinum);
		default -> new UnificationEntry(OrePrefix.wireGtSingle, Tier.Superconductor);
	};

	public static final Component<UnificationEntry> AL_RING =
		bind(OrePrefix.ring, AL_METAL);

	public static final Component<UnificationEntry> AL_ROUND =
		bind(OrePrefix.round, AL_METAL);

	public static final Component<UnificationEntry> AL_SCREW =
		bind(OrePrefix.screw, AL_METAL);

	public static final Component<UnificationEntry> AL_ROTOR =
		bind(OrePrefix.rotor, AL_METAL);

	public static final Component<UnificationEntry> AL_GEAR_SMALL =
		bind(OrePrefix.gearSmall, AL_METAL);

	public static final Component<UnificationEntry> AL_FOIL = tier ->  {
		var material = switch(tier) {
			case LuV -> Materials.Electrum;
			case ZPM -> Materials.Platinum;
			default -> Materials.Osmiridium;
		};

		return new UnificationEntry(OrePrefix.foil, material);
	};

	public static final Component<UnificationEntry> AL_FRAME =
		bind(OrePrefix.frameGt, AL_METAL);

	public static final Component<Material> AL_GEM_MATERIAL = tier -> switch(tier) {
		case LuV -> Materials.Ruby;
		case ZPM -> Materials.Emerald;
		default -> Materials.Diamond;
	};

	public static final Component<UnificationEntry> AL_GEM =
		bind(OrePrefix.gemExquisite, AL_GEM_MATERIAL);

	public static final Component<MetaItem<?>.MetaValueItem> AL_STAR =
		tier -> switch(tier) {
			case LuV, ZPM -> MetaItems.QUANTUM_STAR;
			default -> MetaItems.GRAVI_STAR;
		};
}