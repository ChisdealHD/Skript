/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Copyright 2011-2017 Peter Güttinger and contributors
 */
package ch.njol.skript.util;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.PlayerUtils;
import ch.njol.skript.registrations.Classes;

/**
 * @author Peter Güttinger
 */
public class EquipmentSlot extends Slot {
	
	// according to mcstats there are only 2 servers running 1.2.5 or 1.3.1 respectively
//	public final static Version EQUIPMENT_VERSION = new Version(1,4,5);
//	public final static boolean SUPPORTS_EQUIPMENT = Skript.isRunningMinecraft(EQUIPMENT_VERSION);
	
	public static enum EquipSlot {
		TOOL {
			@SuppressWarnings("deprecation")
			@Override
			@Nullable
			public ItemStack get(final EntityEquipment e) {
				if (Skript.isRunningMinecraft(1, 9)) {
					return e.getItemInMainHand();
				}
				return e.getItemInHand();
			}
			
			@SuppressWarnings("deprecation")
			@Override
			public void set(final EntityEquipment e, final @Nullable ItemStack item) {
				if (Skript.isRunningMinecraft(1, 9)) {
					e.setItemInMainHand(item);
				} else {
					e.setItemInHand(item);
				}
			}
		},
		OFF_HAND { // Since Minecraft 1.9 (defaults to empty if earlier version)

			@Override
			@Nullable
			public ItemStack get(EntityEquipment e) {
				if (Skript.isRunningMinecraft(1, 9)) {
					return e.getItemInOffHand();
				}
				Skript.warning("No off hand support, but a skript would need that!");
				return new ItemStack(Material.AIR);
			}

			@Override
			public void set(EntityEquipment e, @Nullable ItemStack item) {
				if (Skript.isRunningMinecraft(1, 9)) {
					e.setItemInOffHand(item);
				} else {
					Skript.warning("No off hand support, but a skript would need that!");
				}
			}
			
		},
		HELMET(39) {
			@Override
			@Nullable
			public ItemStack get(final EntityEquipment e) {
				return e.getHelmet();
			}
			
			@Override
			public void set(final EntityEquipment e, final @Nullable ItemStack item) {
				e.setHelmet(item);
			}
		},
		CHESTPLATE(38) {
			@Override
			@Nullable
			public ItemStack get(final EntityEquipment e) {
				return e.getChestplate();
			}
			
			@Override
			public void set(final EntityEquipment e, final @Nullable ItemStack item) {
				e.setChestplate(item);
			}
		},
		LEGGINGS(37) {
			@Override
			@Nullable
			public ItemStack get(final EntityEquipment e) {
				return e.getLeggings();
			}
			
			@Override
			public void set(final EntityEquipment e, final @Nullable ItemStack item) {
				e.setLeggings(item);
			}
		},
		BOOTS(36) {
			@Override
			@Nullable
			public ItemStack get(final EntityEquipment e) {
				return e.getBoots();
			}
			
			@Override
			public void set(final EntityEquipment e, final @Nullable ItemStack item) {
				e.setBoots(item);
			}
		};
		
		public final int slotNumber;
		
		EquipSlot() {
			slotNumber = -1;
		}
		
		EquipSlot(int number) {
			slotNumber = number;
		}
		
		@Nullable
		public abstract ItemStack get(EntityEquipment e);
		
		public abstract void set(EntityEquipment e, @Nullable ItemStack item);
		
	}
	
	private final EntityEquipment e;
	private final EquipSlot slot;
	
	public EquipmentSlot(final EntityEquipment e, final EquipSlot slot) {
		this.e = e;
		this.slot = slot;
	}
	
	@Override
	@Nullable
	public ItemStack getItem() {
		return slot.get(e);
	}
	
	@Override
	public void setItem(final @Nullable ItemStack item) {
		slot.set(e, item);
		if (e.getHolder() instanceof Player)
			PlayerUtils.updateInventory((Player) e.getHolder());
	}
	
	@Override
	public String toString_i() {
		return "the " + slot.name().toLowerCase(Locale.ENGLISH) + " of " + Classes.toString(e.getHolder()); // TODO localise?
	}
	
	/**
	 * Gets underlying armor slot enum.
	 * @return Armor slot.
	 */
	public EquipSlot getEquipSlot() {
		return slot;
	}
	
	@Override
	public boolean isSameSlot(Slot o) {
		if (o instanceof InventorySlot) {
			if (slot == EquipSlot.TOOL)
				return false; // TODO maybe fix this
			return this.slot.slotNumber == ((InventorySlot) o).getIndex();
		}
		
		return this.slot == ((EquipmentSlot) o).getEquipSlot();
	}
	
}
