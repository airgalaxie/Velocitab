<!--suppress ALL -->
<p align="center">
    <img src="images/banner.png" alt="Velocitab" />
    <a href="https://github.com/WiIIiam278/Velocitab/actions/workflows/ci.yml">
        <img src="https://img.shields.io/github/actions/workflow/status/WiIIiam278/Velocitab/ci.yml?branch=master&logo=github"/>
    </a> 
    <a href="https://repo.william278.net/#/releases/net/william278/velocitab/">
        <img src="https://repo.william278.net/api/badge/latest/releases/net/william278/velocitab?color=00fb9a&name=Maven&prefix=v" />
    </a>
    <a href="https://discord.gg/tVYhJfyDWG">
        <img src="https://img.shields.io/discord/818135932103557162.svg?label=&logo=discord&logoColor=fff&color=7389D8&labelColor=6A7EC2" />
    </a> 
    <br/>
    <b>
        <a href="https://modrinth.com/plugin/velocitab">Modrinth</a>
    </b> —
    <b>
        <a href="https://william278.net/docs/velocitab/setup">Setup</a>
    </b> — 
    <b>
        <a href="https://william278.net/docs/velocitab/">Docs</a>
    </b> — 
    <b>
        <a href="https://github.com/WiIIiam278/Velocitab/issues">Issues</a>
    </b>
</p>
<br/>

## Airgalaxie Velocity 4.1 compatibility fork

This repository is an experimental development fork of [Velocitab](https://github.com/WiIIiam278/Velocitab), originally created by William278 and its contributors.

The fork maintains Velocitab compatibility with Velocity 4.1 development builds. It isolates the required internal Velocity proxy integration behind a dedicated `velocityinternal` compatibility layer while preserving the existing packet, Netty, protocol, sorting, and nametag behavior.

This fork is maintained independently by Airgalaxie and is not an official Velocitab release. For the original project, official releases, documentation, and support, please use the upstream Velocitab repository.

See [FORK-NOTICE.md](FORK-NOTICE.md) for fork attribution, modification, and support details.

### Development status

- Target runtime: Velocity 4.1 development builds
- Public integration: `velocity-api`
- Required internal compatibility dependency: `velocity-proxy`
- `velocity-proxy` remains a `compileOnly` dependency
- The internal proxy artifact must currently be available through Maven Local
- The compatibility work is maintained on the `airgalaxie/velocity-4.1` branch

### Additional fork changes

- Updated Gradle build tooling
- Replaced the Cadix license plugin with Yumi Gradle Licenser
- Added timestamped identifiers for internal development builds

**Velocitab** is a super-simple Velocity TAB menu plugin that uses scoreboard team client-bound packets to actually sort player lists without the need for a backend plugin. Supporting modern RGB formatting, animations, comprehensive placeholder support and defining multiple TAB menus for different groups of servers, Velocitab is a versatile plugin, useful for any Velocity-based proxy network.

![Showcase of different TAB menus made with Velocitab.png](images/showcase.png)

## Features
**⭐ Flexible list sorting** &mdash; Customizable TAB [list sorting](https://william278.net/docs/velocitab/sorting) based on user role, server, placeholder, and more.

**⭐ Versatile formatting support** &mdash; Make your TAB list beautiful with full RGB color support, supporting MiniMessage, MineDown and legacy [formatting modes](https://william278.net/docs/velocitab/formatting).

**⭐ Multiple TAB menus for different servers** &mdash; Create [server groups](https://william278.net/docs/velocitab/server-groups) and configure different TAB lists to show for each group!

**⭐ Animations support** &mdash; Add extra flair to your TAB list or display additional information by creating pretty looking [animations](https://william278.net/docs/velocitab/animations).

**⭐ Player nametags** &mdash; Customize how over-the-head [nametags](https://william278.net/docs/velocitab/nametags) look to help players stand out in-game.

**⭐ Full placeholder support** &mdash; Comes with a robust set of built-in [placeholders](https://william278.net/docs/velocitab/placeholders), MiniPlaceholders support, as well as PAPIProxyBridge for PlaceholderAPI support

**Ready?** [Get started in a new TAB&hellip;](https://william278.net/docs/velocitab/setup)

## Setup
We suggest installing [LuckPerms](https://luckperms.net) on your Velocity proxy and backend (Spigot, Paper, Fabric, etc.) servers for prefix/suffix formatting right out of the box.

1. Turn off your Velocity proxy server
2. [Download](https://github.com/WiIIiam278/Velocitab/releases/latest) and place the plugin jar file in the `/plugins/` folder of your Velocity proxy server.
3. Start your Velocity proxy, and allow the plugin to generate its config file
4. Edit the [`config.yml` file](https://william278.net/docs/velocitab/config-file) to your liking
5. Restart your Velocity proxy again

Need to make a quick config change? You can use the in-game `/velocitab reload` (permission: `velocitab.command.reload`) command, though we recommend restarting your proxy server for any major changes.

## Building
To build Velocitab, run the following in the root of the repository:
```bash
./gradlew clean build
```
The build will be output as `/target/Velocitab-x.xx.jar`.

Building this fork requires locally publishing the matching `com.velocitypowered:velocity-proxy:4.1.0-SNAPSHOT` artifact and making `mavenLocal()` available before the external repositories.

### License
Velocitab is licensed under the Apache 2.0 license.

- [License](https://github.com/WiIIiam278/Velocitab/blob/master/LICENSE)

## Links
* **[Website](https://william278.net/project/velocitab)** — Visit my website!
* **[Docs](https://william278.net/docs/velocitab)** — Read the plugin docs!
* **[Modrinth](https://modrinth.com/plugin/velocitab)** — View the plugin Modrinth page (Also: [Hangar](https://hangar.papermc.io/William278/Velocitab))
* **[Issues](https://github.com/WiIIiam278/Velocitab/issues)** — File a bug report or feature request
* **[Discord](https://discord.com/invite/tVYhJfyDWG)** — Get support, ask questions!

---
&copy; [William278](https://william278.net/), 2025. Licensed under the Apache-2.0 License.
