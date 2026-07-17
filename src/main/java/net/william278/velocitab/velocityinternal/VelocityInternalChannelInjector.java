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

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.network.Connections;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.DefaultChannelPipeline;
import net.william278.velocitab.Velocitab;
import net.william278.velocitab.velocityinternal.packet.PlayerChannelHandler;
import org.jetbrains.annotations.NotNull;

public class VelocityInternalChannelInjector {

    private static final String KEY = "velocitab";

    private final Velocitab plugin;

    public VelocityInternalChannelInjector(@NotNull Velocitab plugin) {
        this.plugin = plugin;
    }

    public void injectPlayer(@NotNull Player player) {
        final PlayerChannelHandler handler = new PlayerChannelHandler(plugin, player);
        final ConnectedPlayer connectedPlayer = (ConnectedPlayer) player;
        removePlayer(player);
        connectedPlayer.getConnection()
                .getChannel()
                .pipeline()
                .addBefore(Connections.HANDLER, KEY, handler);
    }

    public void removePlayer(@NotNull Player player) {
        final ConnectedPlayer connectedPlayer = (ConnectedPlayer) player;
        final Channel channel = connectedPlayer.getConnection().getChannel();
        final ChannelHandler handler = channel.pipeline().get(KEY);
        if (handler == null) {
            return;
        }
        if (channel.pipeline() instanceof DefaultChannelPipeline defaultChannelPipeline) {
            defaultChannelPipeline.removeIfExists(KEY);
            return;
        }

        plugin.getLogger().warn("Failed to remove player {} from Velocitab packet handler {}",
                player.getUsername(), channel.pipeline().getClass().getName());
    }

}
