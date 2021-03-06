package me.lordsaad.modeoff.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
@PacketRegister(Side.CLIENT)
public class PacketSyncPlots extends PacketBase {

	private Set<Plot> plots;

	public PacketSyncPlots() {}

	public PacketSyncPlots(Set<Plot> plots) {
		this.plots = plots;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveSetter(NBTTagCompound compound) {
		plots = new HashSet<>();
		if (compound == null) return;
		NBTTagList list = compound.getTagList("list", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			plots.add(Plot.deserialize(list.getCompoundTagAt(i)));
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveGetter() {
		NBTTagCompound nbt = new NBTTagCompound();

		if (plots == null) return nbt;

		NBTTagList list = new NBTTagList();
		for (Plot plot : plots) {
			list.appendTag(plot.serializeNBT());
		}
		nbt.setTag("list", list);
		return nbt;
	}

	@Override
	public void handle(MessageContext messageContext) {
		PlotRegistry.INSTANCE.plots = plots;
	}
}
