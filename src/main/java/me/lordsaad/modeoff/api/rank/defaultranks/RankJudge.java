package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.permissions.Permission;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

public class RankJudge implements IRank {

	private HashSet<Permission> permissions = new HashSet<>();

	public RankJudge() {
		permissions.add(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN);
	}

	@Override
	@Nullable
	public String getName() {
		return "Judge";
	}

	@Override
	public TextFormatting getColor() {
		return TextFormatting.GOLD;
	}

	@Override
	public boolean displaySeparately() {
		return true;
	}

	@Override
	public Collection<Permission> getPermissions() {
		return permissions;
	}

	@Override
	public void addPermission(Permission permission) {

	}

	@Override
	public void removePermission(Permission permission) {

	}
}
