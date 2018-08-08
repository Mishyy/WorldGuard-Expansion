/*
 *
 * WorldGuard-Expansion
 * Copyright (C) 2018 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.Set;

public class WorldGuardExpansion extends PlaceholderExpansion {

  private final String NAME = "WorldGuard", IDENTIFIER = NAME.toLowerCase();
  private final String VERSION = getClass().getPackage().getImplementationVersion();
  private WorldGuardPlugin worldguard;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getAuthor() {
    return "clip";
  }

  @Override
  public String getVersion() {
    return VERSION;
  }

  @Override
  public String getIdentifier() {
    return IDENTIFIER;
  }

  @Override
  public boolean canRegister() {
    if (!Bukkit.getPluginManager().isPluginEnabled(NAME)) return false;
    worldguard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin(NAME);
    return worldguard != null && super.register();
  }

  @Override
  public String onRequest(OfflinePlayer player, String params) {
    ProtectedRegion region;
    String[] args;
    if (params.contains(":")) {
      args = params.split(":");
      params = args[0];
      region = getRegion(deserializeLoc(args[1]));
    } else {
      if (player == null || !player.isOnline()) return "";
      region = getRegion(player.getPlayer().getLocation());
    }

    if (region == null) return "";
    switch (params) {
      case "region_name":
        return region.getId();
      case "region_owner":
        Set<String> owners = region.getOwners().getPlayerDomain().getPlayers();
        return owners == null ? "" : String.join(", ", owners);
      case "region_owner_groups":
        return region.getOwners().toGroupsString();
      case "region_members":
        Set<String> members = region.getMembers().getPlayers();
        return members == null ? "" : String.join(", ", members);
      case "region_members_groups":
        return region.getMembers().toGroupsString();
      case "region_flags":
        return region.getFlags().entrySet().toString();
    }
    return null;
  }

  private ProtectedRegion getRegion(Location location) {
    if (location == null) return null;
    ApplicableRegionSet regionSet = worldguard.getRegionManager(location.getWorld()).getApplicableRegions(location);
    return regionSet.getRegions().stream().findFirst().orElse(null);
  }

  private Location deserializeLoc(String string) {
    if (!string.contains(",")) return null;
    String[] splits = string.split(",");
    try {
      return new Location(
          Bukkit.getWorld(splits[0]),
          parseDouble(splits[1]),
          parseDouble(splits[2]),
          parseDouble(splits[3])
      );
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private double parseDouble(String string) throws NumberFormatException {
    return Double.parseDouble(string);
  }

}