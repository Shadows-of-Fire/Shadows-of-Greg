package gregicadditions;

import java.util.HashMap;
import java.util.function.Predicate;

import gregtech.api.unification.Element;
import gregtech.api.unification.material.MaterialIconType;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.api.unification.material.type.IngotMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import net.minecraftforge.common.util.EnumHelper;

import static gregtech.api.GTValues.M;
import static gregtech.api.unification.material.type.DustMaterial.MatFlags.GENERATE_PLATE;
import static gregtech.api.unification.material.type.DustMaterial.MatFlags.NO_SMASHING;

public class GAEnums {


	public static class GAMaterialIconType {

		public final static MaterialIconType plateCurved = createMaterialIconType("plateCurved");
		public final static MaterialIconType ingotDouble = createMaterialIconType("ingotDouble");
		public final static MaterialIconType round = createMaterialIconType("round");
		//public final static MaterialIconType coke = createMaterialIconType("coke");
	}

	public static void preInit() { //TODO remove this
		EnumHelper.addEnum(Element.class, "Nt", new Class[]
				{ long.class, long.class, long.class, String.class, String.class, boolean.class },
				0L, 5000L, -1L, null, "NEUTRONIUM", false);

		/*EnumHelper.addEnum(MaterialIconSet.class, "COKE", new Class[0]);

		if (GAConfig.GT6.addCurvedPlates) {
			EnumHelper.addEnum(MaterialIconType.class, "plateCurved", new Class[0]);
			EnumHelper.addEnum(OrePrefix.class, "plateCurved", new Class[] { String.class, long.class, Material.class, MaterialIconType.class, long.class, Predicate.class }, "Curved Plate", M, null, MaterialIconType.valueOf("plateCurved"), OrePrefix.Flags.ENABLE_UNIFICATION, pred(mat -> ingot.test(mat) && mat.hasFlag(GENERATE_PLATE)));
		}

		if (GAConfig.GT6.addDoubleIngots) {
			EnumHelper.addEnum(MaterialIconType.class, "ingotDouble", new Class[0]);
			EnumHelper.addEnum(OrePrefix.class, "ingotDouble", new Class[] { String.class, long.class, Material.class, MaterialIconType.class, long.class, Predicate.class }, "Double Ingot", M, null, MaterialIconType.valueOf("ingotDouble"), OrePrefix.Flags.ENABLE_UNIFICATION, pred(mat -> ingot.test(mat) && mat.hasFlag(GENERATE_PLATE)));
		}

		EnumHelper.addEnum(MaterialIconType.class, "round", new Class[0]);
		EnumHelper.addEnum(OrePrefix.class, "round", new Class[] { String.class, long.class, Material.class, MaterialIconType.class, long.class, Predicate.class }, "Round", M, null, MaterialIconType.valueOf("round"), OrePrefix.Flags.ENABLE_UNIFICATION, pred(mat -> ingot.test(mat) && mat.hasFlag(IngotMaterial.MatFlags.GENERATE_SMALL_GEAR)));*/
	}

	public static class GAOrePrefix {

		public final static OrePrefix plateCurved = createOrePrefix("plateCurved", "Curved Plate", M, null, GAMaterialIconType.plateCurved, OrePrefix.Flags.ENABLE_UNIFICATION, pred(mat -> ingot.test(mat) && mat.hasFlag(GENERATE_PLATE)));
		//TODO, generate double ingots for No Smashing Materials as done previously?
		public final static OrePrefix ingotDouble = createOrePrefix("ingotDouble", "Double Ingot", M * 2, null, GAMaterialIconType.ingotDouble, OrePrefix.Flags.ENABLE_UNIFICATION, pred(mat -> ingot.test(mat) && mat.hasFlag(GENERATE_PLATE) && !mat.hasFlag(NO_SMASHING)));
		public final static OrePrefix round = createOrePrefix("round", "Round", M / 9, null, GAMaterialIconType.round, OrePrefix.Flags.ENABLE_UNIFICATION, pred(mat -> ingot.test(mat) && mat.hasFlag(IngotMaterial.MatFlags.GENERATE_SMALL_GEAR)));

	}


	public static MaterialIconType createMaterialIconType(String name) {
		EnumHelper.addEnum(MaterialIconType.class, name, new Class[0]);
		return MaterialIconType.valueOf(name);
	}


	public static OrePrefix createOrePrefix(String orePrefix, String categoryName, long materialAmount, Material material, MaterialIconType materialIconType, long flags, Predicate<Material> condition) {
		EnumHelper.addEnum(OrePrefix.class, orePrefix,
				new Class[]{String.class, long.class, Material.class, MaterialIconType.class, long.class, Predicate.class},
				categoryName, materialAmount, material, materialIconType, flags, condition);

		return OrePrefix.valueOf(orePrefix);

	}

	public static final Predicate<Material> dust = mat -> mat instanceof DustMaterial;
	public static final Predicate<Material> ingot = mat -> mat instanceof IngotMaterial;
	public static final HashMap<String, Integer> voltageMap = new HashMap<String, Integer>() {
		{
			put("lv", 32);
			put("mv", 128);
			put("hv", 512);
			put("ev", 2048);
			put("iv", 8192);
			put("luv", 32768);
			put("zpm", 131072);
			put("uv", 524288);
			put("max", Integer.MAX_VALUE);

		}
	};

	private static Predicate<Material> pred(Predicate<Material> in) {
		return mat -> in.test(mat);
	}
}
