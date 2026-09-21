import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.StateRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.kyori.adventure.text.Component;
import net.william278.velocitab.packet.Protocol770Adapter;
import net.william278.velocitab.packet.ScoreboardManager;
import net.william278.velocitab.packet.UpdateTeamsPacket;
import net.william278.velocitab.packet.UpdateTeamsPacket.CollisionRule;
import net.william278.velocitab.packet.UpdateTeamsPacket.FriendlyFlag;
import net.william278.velocitab.packet.UpdateTeamsPacket.NametagVisibility;
import net.william278.velocitab.packet.UpdateTeamsPacket.UpdateMode;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/** External regression harness for the 26.3 teams-packet adapter and registry mapping. */
public final class Velocitab263PacketHarness {
    private static final List<ProtocolVersion> VERSIONS = List.of(
            ProtocolVersion.MINECRAFT_26_1,
            ProtocolVersion.MINECRAFT_26_2,
            ProtocolVersion.MINECRAFT_26_3);

    private static int roundTrips;

    public static void main(String[] args) throws Exception {
        adapterClaims263();
        roundTripEveryMode();
        roundTripAllColorsAndEnums();
        registered263MappingIs111();
        System.out.println("PASS: " + roundTrips + " roundtrips; 26.1/26.2/26.3 and 26.3 mapping 0x6F");
    }

    private static void adapterClaims263() {
        Protocol770Adapter adapter = new Protocol770Adapter(null);
        check(adapter.getProtocolVersions().contains(ProtocolVersion.MINECRAFT_26_3),
                "Protocol770Adapter must claim MINECRAFT_26_3");
        ScoreboardManager manager = new ScoreboardManager(null, true);
        check(manager.getPacketAdapter(ProtocolVersion.MINECRAFT_26_3) instanceof Protocol770Adapter,
                "ScoreboardManager must select Protocol770Adapter for MINECRAFT_26_3");
    }

    private static void roundTripEveryMode() {
        for (ProtocolVersion version : VERSIONS) {
            for (UpdateMode mode : UpdateMode.values()) {
                UpdateTeamsPacket source = packet(mode, 12, NametagVisibility.HIDE_FOR_OTHER_TEAMS,
                        CollisionRule.PUSH_OWN_TEAM);
                UpdateTeamsPacket decoded = roundTrip(version, source);
                equal(source.teamName(), decoded.teamName(), version + " " + mode + " team name");
                equal(mode, decoded.mode(), version + " " + mode + " mode");

                if (mode == UpdateMode.CREATE_TEAM || mode == UpdateMode.UPDATE_INFO) {
                    equal(source.displayName(), decoded.displayName(), version + " " + mode + " display name");
                    equal(source.prefix(), decoded.prefix(), version + " " + mode + " prefix");
                    equal(source.suffix(), decoded.suffix(), version + " " + mode + " suffix");
                    equal(source.friendlyFlags(), decoded.friendlyFlags(), version + " " + mode + " flags");
                    equal(source.nametagVisibility(), decoded.nametagVisibility(), version + " " + mode + " visibility");
                    equal(source.collisionRule(), decoded.collisionRule(), version + " " + mode + " collision");
                    equal(source.color(), decoded.color(), version + " " + mode + " color");
                }
                if (mode == UpdateMode.CREATE_TEAM || mode == UpdateMode.ADD_PLAYERS
                        || mode == UpdateMode.REMOVE_PLAYERS) {
                    equal(source.entities(), decoded.entities(), version + " " + mode + " entities");
                }
            }
        }
    }

    private static void roundTripAllColorsAndEnums() {
        for (ProtocolVersion version : VERSIONS) {
            for (int color = 0; color <= 21; color++) {
                for (NametagVisibility visibility : NametagVisibility.values()) {
                    for (CollisionRule collision : CollisionRule.values()) {
                        UpdateTeamsPacket decoded = roundTrip(version,
                                packet(UpdateMode.CREATE_TEAM, color, visibility, collision));
                        int expectedColor = version.noLessThan(ProtocolVersion.MINECRAFT_26_2)
                                && color > 15 && color != 21 ? 15 : color;
                        equal(expectedColor, decoded.color(), version + " color " + color);
                        equal(visibility, decoded.nametagVisibility(), version + " visibility " + visibility);
                        equal(collision, decoded.collisionRule(), version + " collision " + collision);
                    }
                }
            }
        }
    }

    private static UpdateTeamsPacket packet(UpdateMode mode, int color,
                                             NametagVisibility visibility, CollisionRule collision) {
        return new UpdateTeamsPacket(null)
                .teamName("team-regression")
                .mode(mode)
                .displayName(Component.text("Display"))
                .friendlyFlags(List.of(FriendlyFlag.CAN_HURT_FRIENDLY,
                        FriendlyFlag.CAN_HURT_FRIENDLY_FIRE))
                .nametagVisibility(visibility)
                .collisionRule(collision)
                .color(color)
                .prefix(Component.text("[prefix]"))
                .suffix(Component.text("[suffix]"))
                .entities(List.of("Alice", "Bob"));
    }

    private static UpdateTeamsPacket roundTrip(ProtocolVersion version, UpdateTeamsPacket source) {
        Protocol770Adapter adapter = new Protocol770Adapter(null);
        ByteBuf encoded = Unpooled.buffer();
        try {
            adapter.encode(encoded, source, version);
            UpdateTeamsPacket decoded = new UpdateTeamsPacket(null);
            adapter.decode(encoded, decoded, version);
            check(!encoded.isReadable(), version + " " + source.mode() + " left "
                    + encoded.readableBytes() + " unread bytes");
            roundTrips++;
            return decoded;
        } finally {
            encoded.release();
        }
    }

    private static void registered263MappingIs111() throws Exception {
        ScoreboardManager manager = new ScoreboardManager(null, true);
        manager.registerPacket();

        Object clientbound = field(StateRegistry.class, "clientbound").get(StateRegistry.PLAY);
        @SuppressWarnings("unchecked")
        Map<ProtocolVersion, ?> versions = (Map<ProtocolVersion, ?>)
                field(clientbound.getClass(), "versions").get(clientbound);
        Object protocolRegistry = versions.get(ProtocolVersion.MINECRAFT_26_3);
        check(protocolRegistry != null, "PLAY clientbound registry must contain MINECRAFT_26_3");

        @SuppressWarnings("unchecked")
        Object2IntMap<Class<?>> classToId = (Object2IntMap<Class<?>>)
                field(protocolRegistry.getClass(), "packetClassToId").get(protocolRegistry);
        int id = classToId.getInt(UpdateTeamsPacket.class);
        equal(0x6F, id, "MINECRAFT_26_3 registered UpdateTeamsPacket id");
        manager.unregisterPacket();
    }

    private static Field field(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static void equal(Object expected, Object actual, String label) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(label + ": expected=" + expected + ", actual=" + actual);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
