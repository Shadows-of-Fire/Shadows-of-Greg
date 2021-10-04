package gregicadditions;

import java.util.ArrayList;
import java.util.List;
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
import static gregtech.api.unification.ore.OrePrefix.*;

public class GAEnums {


	public static class GAMaterialIconType {

		public final static MaterialIconType plateCurved = createMaterialIconType("plateCurved");
		public final static MaterialIconType ingotDouble = createMaterialIconType("ingotDouble");
		public final static MaterialIconType round = createMaterialIconType("round");
		public final static MaterialIconType coke = createMaterialIconType("coke");
	}

	public static void preInit() {
		EnumHelper.addEnum(Element.class, "Nt", new Class[]
				{ long.class, long.class, long.class, String.class, String.class, boolean.class },
				0L, 5000L, -1L, null, "NEUTRONIUM", false);
	}

	public static class GAOrePrefix {

		public final static OrePrefix plateCurved = createOrePrefix("plateCurved", "Curved Plate", M, null, GAMaterialIconType.plateCurved, OrePrefix.Flags.ENABLE_UNIFICATION, pred(mat -> ingot.test(mat) && mat.hasFlag(GENERATE_PLATE)));
		public final static OrePrefix ingotDouble = createOrePrefix("ingotDouble", "Double Ingot", M * 2, null, GAMaterialIconType.ingotDouble, OrePrefix.Flags.ENABLE_UNIFICATION, pred(mat -> ingot.test(mat) && mat.hasFlag(GENERATE_PLATE)));
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

	public static final List<OrePrefix> WIRE_DOUBLING_ORDER = new ArrayList<OrePrefix>(){{
		add(wireGtSingle);
		add(wireGtDouble);
		add(wireGtQuadruple);
		add(wireGtOctal);
		add(wireGtHex);
	}};

	private static Predicate<Material> pred(Predicate<Material> in) {
		return mat -> in.test(mat);
	}
}
