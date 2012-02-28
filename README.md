# CompassEx Bukkit Plugin
_<span style="font-size:150%;">Your compass, but heavily improved.</span>_

<img src="http://dl.dropbox.com/u/8199290/Compass.png" style="float:right;" />

This plugin allows you to easily set your compass to different positions. You can set it to coordinates, players and of course, back to spawn.

## Features

* Set your compass to players, coordinates and your current position.
* Follow players static and live (updates the position).
* Set yourself as hidden, so that you cant be tracked by others.
* Admins can follow even hidden users.

## Commands
The original CompassEx command `/compass` has also aliases: `/cp` and `/compassex`.
<table>
	<tr>
		<th>Command</th><th>Alias</th><th>Description</th>
	</tr>
	<tr>
		<td><code>/compass reset</code></td>
		<td><code>/compass spawn</code></td>
		<td>Sets the compass back to spawn.</td>
	</tr>
	<tr>
		<td><code>/compass here</code></td>
		<td></td>
		<td>Sets the compass to the player's current position.</td>
	</tr>
	<tr>
		<td><code>/compass north/east/south/west</code></td>
		<td><code>/compass n/e/s/w</code></td>
		<td>Sets the compass to a specific direction.</td>
	</tr>
	<tr>
		<td><code>/compass live PLAYER</code></td>
		<td></td>
		<td>Sets the compass to the player player and updates it.</td>
	</tr>
	<tr>
		<td><code>/compass height</code></td>
		<td><code>/compass h</code></td>
		<td>Shows the height difference between you and the compasses target.</td>
	</tr>
	<tr>
		<td><code>/compass hide</code></td>
		<td></td>
		<td>Toggles the player between hidden and not hidden. This prevents the player from being tracked by others.</td>
	</tr>
	<tr>
		<td><code>/compass hidden</code></td>
		<td></td>
		<td>Shows if the player is hidden right now.</td>
	</tr>
	<tr>
		<td><code>/compass deathpoint</code></td>
		<td><code>/compass dp</code></td>
		<td>Sets the compass to the player's latest deathpoint.</td>
	</tr>
	<tr>
		<td><code>/compass X Y Z</code></td>
		<td><code>/compass pos X Y Z</code></td>
		<td>Sets the compass to the given coordinates.</td>
	</tr>
	<tr>
		<td><code>/compass PLAYER</code></td>
		<td><code>/compass player PLAYER</code></td>
		<td>Sets the compass to the given player.</td>
	</tr>
</table>

## Installation:

Just copy the jar file into your plugins folder, start your server so that the config gets generated and stop it. Then edit the config file at will. Now start your server again.

## Permissions

<table>
	<tr>
		<th>Permission node</th>
		<th>Description</th>
	</tr>
	<tr>
		<td><code>compassex.help</code></td>
		<td>Allow users to display the help.</td>
	</tr>
	<tr>
		<td><code>compassex.reset</code></td>
		<td>Access to <code>/compass spawn</code>.</td>
	</tr>
	<tr>
		<td><code>compassex.here</code></td>
		<td>Access to <code>/compass here</code>.</td>
	</tr>
	<tr>
		<td><code>compassex.direction</code></td>
		<td>Access to <code>/compass north/east/south/west</code>.</td>
	</tr>
	<tr>
		<td><code>compassex.live</code></td>
		<td>Access to <code>/compass live</code>.</td>
	</tr>
	<tr>
		<td><code>compassex.height</code></td>
		<td>Access to <code>/compass height</code>.</td>
	</tr>
	<tr>
		<td><code>compassex.hide</code></td>
		<td>Access to <code>/compass hide</code>.</td>
	</tr>
	<tr>
		<td><code>compassex.deathpoint</code></td>
		<td>Access to <code>/compass deathpoint</code>.</td>
	</tr>
	<tr>
		<td><code>compassex.pos</code></td>
		<td>Access to <code>/compass pos</code>.</td>
	</tr>
	<tr>
		<td><code>compassex.player</code></td>
		<td>Access to <code>/compass player</code>.</td>
	</tr>
	<tr>
		<td><code>compassex.admin</code></td>
		<td>Allows to track even hidden users.</td>
	</tr>
	
</table>

## Configuration
The configuration is really simple:

* `live-update-rate`: Sets how fast the position of the live tracked player get refreshed. Setting it to a small number may result in poor server performance. Recommended value: 200 (2 seconds).

## More

Do you think you found any bugs? [Submit one here.](https://github.com/Philipp15b/minecraft-bukkit-compassex/issues)

You think you can help by adding a feature or fixing a bug? Go to the [GitHub repository](https://github.com/Philipp15b/minecraft-bukkit-compassex).

<small>Compass image from [MinecraftWiki](http://www.minecraftwiki.net/wiki/File:Compass.png)</small>
