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
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.StateRegistry;
import net.william278.velocitab.Velocitab;
import net.william278.velocitab.velocityinternal.packet.PacketRegistration;
import net.william278.velocitab.velocityinternal.packet.UpdateTeamsPacket;
import org.jetbrains.annotations.NotNull;

public class VelocityInternalPacketBridge {

    public void sendPacket(@NotNull Player player, @NotNull UpdateTeamsPacket packet) {
        final ConnectedPlayer connectedPlayer = (ConnectedPlayer) player;
        connectedPlayer.getConnection().write(packet);
    }

    @NotNull
    public PacketRegistration<UpdateTeamsPacket> createUpdateTeamsPacketRegistration(@NotNull Velocitab plugin) {
        return PacketRegistration.of(UpdateTeamsPacket.class)
                .direction(ProtocolUtils.Direction.CLIENTBOUND)
                .packetSupplier(() -> new UpdateTeamsPacket(plugin))
                .stateRegistry(StateRegistry.PLAY);
    }

}
