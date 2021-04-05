package gregicadditions.machines;

import gregicadditions.GACapabilities;
import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.common.metatileentities.electric.multiblockpart.MetaTileEntityItemBus;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class MetaTileEntityMachineHolder extends MetaTileEntityItemBus implements IMultiblockAbilityPart<IItemHandlerModifiable> {

    protected IItemHandlerModifiable machineItemHandler;

    public MetaTileEntityMachineHolder(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 0, false);
        machineItemHandler = new MachineImportItemHandler();
        initializeInventory();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityMachineHolder(metaTileEntityId);
    }

    @Override
    public MultiblockAbility<IItemHandlerModifiable> getAbility() {
        return GACapabilities.PA_MACHINE_CONTAINER;
    }

    @Override
    public void registerAbilities(List<IItemHandlerModifiable> abilityList) {
        abilityList.add(machineItemHandler);
    }

    @Override
    public IItemHandlerModifiable getImportItems() {
        return machineItemHandler;
    }

    @Override
    public IItemHandler getItemInventory() {
        return machineItemHandler;
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return machineItemHandler;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        TileEntityProcessingArray controller = (TileEntityProcessingArray) getController();
        if(controller != null && controller.getWorkable().isActive()) {
            return false;
        }
        return true;
    }

    private static class MachineImportItemHandler extends ItemStackHandler {

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

            if(!isItemValid(slot, stack)) {
                return stack;
            }

            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

            String unlocalizedName = stack.getItem().getUnlocalizedNameInefficiently(stack);

            if(unlocalizedName.contains("gregtech.machine") || unlocalizedName.contains("gtadditions.machine")) {
                MetaTileEntity mte = GregTechAPI.META_TILE_ENTITY_REGISTRY.getObjectById(stack.getItemDamage());
                
                if(mte != null && mte instanceof ITieredMetaTileEntity) {
                    return true;
                }
            }

            return false;
        }

    }
}
