# CompassEx Bukkit Plugin
_<span style="font-size:150%;">Your compass, but heavily improved.</span>_

<img src="http://dl.dropbox.com/u/8199290/Compass.png" style="float:right;" />

## Commands
The original CompassEx command `/compass` has also aliases: `/cp` and `/compassex`.
<table>
	<tr>
		<th>Command</th><th>Alias</th><th>Description</th>
	</tr>
	<tr>
		<td>`/compass reset`</td>
		<td>`/spawn`</td>
		<td>Sets the compass back to spawn.</td>
	</tr>
	<tr>
		<td>`/compass here`</td>
		<td></td>
		<td>Sets the compass to the player's current position.</td>
	</tr>
	<tr>
		<td>`/compass live *PLAYER*`</td>
		<td></td>
		<td>Sets the compass to the player `player` and updates it.</td>
	</tr>
	<tr>
		<td>`/compass height`</td>
		<td>`/compass h`</td>
		<td>Shows the height difference between you and the compasses target.</td>
	</tr>
	<tr>
		<td>`/compass hide`</td>
		<td></td>
		<td>Toggles the player between hidden and not hidden. This prevents the player from being tracked by others.</td>
	</tr>
	<tr>
		<td>`/compass hidden`</td>
		<td></td>
		<td>Shows if the player is hidden right now.</td>
	</tr>
	<tr>
		<td>`/compass deathpoint`</td>
		<td>`/compass dp`</td>
		<td>Sets the compass to the player's latest deathpoint.</td>
	</tr>
	<tr>
		<td>`/compass *X* *Y* *Z*`</td>
		<td>`/compass pos *X* *Y* *Z*`</td>
		<td>Sets the compass to the given coordinates.</td>
	</tr>
	<tr>
		<td>`/compass *PLAYER*`</td>
		<td>`/compass player *PLAYER*`</td>
		<td>Sets the compass to the given player.</td>
	</tr>
</table>

## Installation:

Just copy the jar file into your plugins folder, start your server so that the config gets generated and stop it. Then edit the config file at will. Now start your server again.

## Permissions:

* `compassex.*` - Access to all CompassEx commands.
   * `compassex.here` - Access to here command.
   * `compassex.set` - Access to set command.
   * `compassex.reset` - Access to reset command.
   * `compassex.player` - Access to player command
   * `compassex.live` - Access to live command.
   * `compassex.hide` - Access to hide and hidden command.
   * `compassex.admin` - Allows the user to track hidden users.

## Configuration:

* `live-update-rate`: Sets how fast the position of the live tracked player get refreshed. Setting it to a small number may result in poor server performance. Recommended value: 200

## More:

Do you think you found any bugs? [Submit one here.](https://github.com/Philipp15b/minecraft-bukkit-compassex/issues)

You think you can help by adding a feature or fixing a bug? Go to the [GitHub repository](https://github.com/Philipp15b/minecraft-bukkit-compassex).

<small>Compass image from [MinecraftWiki](http://www.minecraftwiki.net/wiki/File:Compass.png)</small>
