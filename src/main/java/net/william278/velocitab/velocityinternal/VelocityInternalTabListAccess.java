/*
 * This file is part of Velocitab, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

// Modified by Jens Hoffmann (Airgalaxie) in 2026. See FORK-NOTICE.md.

package net.william278.velocitab.velocityinternal;

import com.google.common.collect.Maps;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.TabListEntry;
import com.velocitypowered.proxy.tablist.KeyedVelocityTabList;
import com.velocitypowered.proxy.tablist.VelocityTabList;
import net.william278.velocitab.Velocitab;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VelocityInternalTabListAccess {

    private final Velocitab plugin;
    private final Map<Class<?>, Field> entriesFields;

    public VelocityInternalTabListAccess(@NotNull Velocitab plugin) {
        this.plugin = plugin;
        this.entriesFields = Maps.newHashMap();
        this.registerFields();
    }

    // VelocityTabListLegacy is not supported
    private void registerFields() {
        final Class<KeyedVelocityTabList> keyedVelocityTabListClass = KeyedVelocityTabList.class;
        final Class<VelocityTabList> velocityTabListClass = VelocityTabList.class;
        try {
            final Field entriesField = keyedVelocityTabListClass.getDeclaredField("entries");
            entriesField.setAccessible(true);
            this.entriesFields.put(keyedVelocityTabListClass, entriesField);
        } catch (NoSuchFieldException e) {
            plugin.log(Level.ERROR, "Failed to register KeyedVelocityTabList field", e);
        }
        try {
            final Field entriesField = velocityTabListClass.getDeclaredField("entries");
            entriesField.setAccessible(true);
            this.entriesFields.put(velocityTabListClass, entriesField);
        } catch (NoSuchFieldException e) {
            plugin.log(Level.ERROR, "Failed to register VelocityTabList field", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void fixDuplicateEntries(@NotNull Player target) {
        try {
            final Optional<Field> optionalField = Optional.ofNullable(this.entriesFields.get(target.getTabList().getClass()));
            if (optionalField.isEmpty()) {
                return;
            }
            final Field entriesField = optionalField.get();
            final Map<UUID, TabListEntry> entries = (Map<UUID, TabListEntry>) entriesField.get(target.getTabList());
            entries.entrySet().stream()
                    .filter(entry -> entry.getValue().getProfile() != null)
                    .filter(entry -> entry.getValue().getProfile().getId().equals(target.getUniqueId()))
                    .filter(entry -> !entry.getKey().equals(target.getUniqueId()))
                    .forEach(entry -> target.getTabList().removeEntry(entry.getKey()));
        } catch (Throwable error) {
            plugin.log(Level.ERROR, "Failed to fix duplicate entries for class " + target.getTabList().getClass().getName(), error);
        }
    }

}
