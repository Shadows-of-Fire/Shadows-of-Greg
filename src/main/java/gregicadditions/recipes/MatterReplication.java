package gregicadditions.recipes;

import gregicadditions.GAMaterials;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;

public class MatterReplication {
	public static void init() {
		//Mass Fab
		GARecipeMaps.MASS_FAB_RECIPES.recipeBuilder()
				.fluidInputs(Materials.Hydrogen.getFluid(1000))
				.fluidOutputs(GAMaterials.POSITIVE_MATTER.getFluid(1))
				.duration((int) (Materials.Hydrogen.getMass() * 100)).EUt(32).buildAndRegister();

		for (Material m : Material.MATERIAL_REGISTRY) {
			if (m.getProtons() > 0 && m.getNeutrons() > 0 && m.getMass() != 98 && m instanceof FluidMaterial && OreDictUnifier.get(OrePrefix.dust, m).isEmpty()) {
				GARecipeMaps.MASS_FAB_RECIPES.recipeBuilder()
						.fluidInputs(((FluidMaterial) m).getFluid(1000))
						.fluidOutputs(GAMaterials.POSITIVE_MATTER.getFluid((int) m.getProtons()), GAMaterials.NEUTRAL_MATTER.getFluid((int) m.getNeutrons()))
						.duration((int) (m.getMass() * 100)).EUt(32).buildAndRegister();
			}
		}

		//Why is this separate and blacklisted?
		GARecipeMaps.MASS_FAB_RECIPES.recipeBuilder()
				.inputs(OreDictUnifier.get(OrePrefix.dust, GAMaterials.NEUTRONIUM))
				.fluidOutputs(GAMaterials.NEUTRAL_MATTER.getFluid(5000))
				.duration((int) (GAMaterials.NEUTRONIUM.getMass() * 100)).EUt(32).buildAndRegister();

		for (Material m : Material.MATERIAL_REGISTRY) {
			if (m.getProtons() >= 1 && m.getNeutrons() >= 0 && m.getMass() != 98 && m instanceof DustMaterial && m != Materials.Sphalerite && m != Materials.Naquadria && m != Materials.Ash && m != Materials.DarkAsh && m != GAMaterials.NEUTRONIUM && m != Materials.Monazite && m != Materials.Bentonite) {
				GARecipeMaps.MASS_FAB_RECIPES.recipeBuilder()
						.inputs(OreDictUnifier.get(OrePrefix.dust, m))
						.fluidOutputs(GAMaterials.POSITIVE_MATTER.getFluid((int) m.getProtons()), GAMaterials.NEUTRAL_MATTER.getFluid((int) m.getNeutrons()))
						.duration((int) (m.getMass() * 100)).EUt(32).buildAndRegister();
			}
		}

		//Replicator
		GARecipeMaps.REPLICATOR_RECIPES.recipeBuilder()
				.inputs(FluidCellIngredient.getIngredient(Materials.Hydrogen, 0))
				.fluidInputs(GAMaterials.POSITIVE_MATTER.getFluid(1))
				.fluidOutputs(Materials.Hydrogen.getFluid(1000))
				.duration((int) (Materials.Hydrogen.getMass() * 100)).EUt(32).buildAndRegister();

		for (Material m : Material.MATERIAL_REGISTRY) {
			if (m.getProtons() > 0 && m.getNeutrons() > 0 && m.getMass() != 98 && m instanceof FluidMaterial && OreDictUnifier.get(OrePrefix.dust, m).isEmpty() && m != Materials.Air && m != Materials.LiquidAir) {
				GARecipeMaps.REPLICATOR_RECIPES.recipeBuilder()
						.inputs(FluidCellIngredient.getIngredient((FluidMaterial) m, 0))
						.fluidOutputs(((FluidMaterial) m).getFluid(1000))
						.fluidInputs(GAMaterials.POSITIVE_MATTER.getFluid((int) m.getProtons()), GAMaterials.NEUTRAL_MATTER.getFluid((int) m.getNeutrons()))
						.duration((int) (m.getMass() * 100)).EUt(32).buildAndRegister();
			}
		}

		//Why is this separate and blacklisted?
		GARecipeMaps.REPLICATOR_RECIPES.recipeBuilder()
				.notConsumable(OreDictUnifier.get(OrePrefix.dust, GAMaterials.NEUTRONIUM))
				.fluidInputs(GAMaterials.NEUTRAL_MATTER.getFluid(5000))
				.outputs(OreDictUnifier.get(OrePrefix.dust, GAMaterials.NEUTRONIUM))
				.duration((int) (GAMaterials.NEUTRONIUM.getMass() * 100)).EUt(32).buildAndRegister();

		for (Material m : Material.MATERIAL_REGISTRY) {
			if (m.getProtons() >= 1 && m.getNeutrons() >= 0 && m.getMass() != 98 && m instanceof DustMaterial && m != Materials.Sphalerite && m != Materials.Naquadria && m != Materials.Ash && m != Materials.DarkAsh && m != GAMaterials.NEUTRONIUM && m != Materials.Monazite && m != Materials.Bentonite) {
				GARecipeMaps.REPLICATOR_RECIPES.recipeBuilder()
						.notConsumable(OreDictUnifier.get(OrePrefix.dust, m))
						.fluidInputs(GAMaterials.POSITIVE_MATTER.getFluid((int) m.getProtons()), GAMaterials.NEUTRAL_MATTER.getFluid((int) m.getNeutrons()))
						.outputs(OreDictUnifier.get(OrePrefix.dust, m))
						.duration((int) (m.getMass() * 100)).EUt(32).buildAndRegister();
			}
		}
	}
}
