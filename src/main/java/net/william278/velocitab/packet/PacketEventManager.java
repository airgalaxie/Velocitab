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

package net.william278.velocitab.packet;

import com.velocitypowered.api.event.AwaitingEventExecutor;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.william278.velocitab.Velocitab;
import net.william278.velocitab.velocityinternal.VelocityInternalChannelInjector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PacketEventManager {

    private final Velocitab plugin;
    private final VelocityInternalChannelInjector channelInjector;

    public PacketEventManager(@NotNull Velocitab plugin) {
        this.plugin = plugin;
        this.channelInjector = new VelocityInternalChannelInjector(plugin);
        this.loadPlayers();
        this.loadListeners();
    }

    private void loadPlayers() {
        plugin.getServer().getScheduler()
                .buildTask(plugin, () -> plugin.getServer().getAllPlayers().forEach(this::injectPlayer))
                .delay(100, TimeUnit.MILLISECONDS)
                .schedule();
    }

    private void loadListeners() {
        plugin.getServer().getEventManager().register(plugin, PostLoginEvent.class,
                (AwaitingEventExecutor<PostLoginEvent>) postLoginEvent -> EventTask.withContinuation(continuation -> {
                    injectPlayer(postLoginEvent.getPlayer());
                    continuation.resume();
                }));

        plugin.getServer().getEventManager().register(plugin, DisconnectEvent.class,
                (AwaitingEventExecutor<DisconnectEvent>) disconnectEvent ->
                        disconnectEvent.getLoginStatus() == DisconnectEvent.LoginStatus.CONFLICTING_LOGIN
                                ? null
                                : EventTask.async(() -> removePlayer(disconnectEvent.getPlayer())));
    }

    public void injectPlayer(@NotNull Player player) {
        channelInjector.injectPlayer(player);
    }

    public void removeAllPlayers() {
        plugin.getServer().getAllPlayers().forEach(this::removePlayer);
    }

    public void removePlayer(@NotNull Player player) {
        channelInjector.removePlayer(player);
    }

}
