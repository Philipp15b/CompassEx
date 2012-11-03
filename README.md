# CompassEx Bukkit Plugin
_<span style="font-size:150%;">Your compass, but heavily improved.</span>_

<img src="http://dl.dropbox.com/u/8199290/Compass.png" style="float:right;" />

This plugin allows you to easily set your compass to different positions. You can set it to coordinates, players and of course, back to spawn.

## Features

* Set your compass to players, coordinates and your current position.
* Follow players static and live (updates the position).
* Set yourself as hidden, so that you cant be tracked by others.
* Admins can follow even hidden users.
* Save your location, or compass target for later use.
* Make saved locations private or public.
* Vault support for `save`, `private`, and `public` commands.

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
		<td><code>/compass bed</code></td>
		<td><code>/compass</code></td>
		<td>Sets the compass to your bed.</td>
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
	<tr>
		<td><code>/compass save ID</code></td>
		<td></td>
		<td>Save your current compass target, so you can use it later.</td>
	</tr>
	<tr>
		<td><code>/compass save here ID</code></td>
		<td></td>
		<td>Save your current location, so you can use it as compass target later.</td>
	</tr>
	<tr>
		<td><code>/compass load ID</code></td>
		<td></td>
		<td>Set your compass to a previously saved location.</td>
	</tr>
	<tr>
		<td><code>/compass remove ID</code></td>
		<td></td>
		<td>Remove a saved compass target</td>
	</tr>
	<tr>
		<td><code>/compass list private PAGE-NUMBER</code></td>
		<td></td>
		<td>List of private compass targets</td>
	</tr>
	<tr>
		<td><code>/compass list public PAGE-NUMBER</code></td>
		<td><code>/compass list PAGE-NUMBER</code></td>
		<td>List of public compass targets</td>
	</tr>
	<tr>
		<td><code>/compass info</code></td>
		<td></td>
		<td>Show the coordinates of your current compass target</td>
	</tr>
	<tr>
		<td><code>/compass info ID</code></td>
		<td></td>
		<td>Show the coordinates of a saved compass target</td>
	</tr>
	<tr>
		<td><code>/compass publicize ID</code></td>
		<td><code>/compass public ID</code></td>
		<td>Convert a private compass target, to a public compass target.</td>
	</tr>
	<tr>
		<td><code>/compass privatize ID</code></td>
		<td><code>/compass private ID</code></td>
		<td>Convert a public compass target, back to a private compass target.</td>
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
		<td><code>compassex.bed</code></td>
		<td>Access to <code>/compass bed</code>.</td>
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
	<tr>
		<td><code>compassex.save</code></td>
		<td>Allows access to the save command</td>
	</tr>
	<tr>
		<td><code>compassex.save.free</code></td>
		<td>Users with this permission won't have to pay for saving a target</td>
	</tr>
	<tr>
		<td><code>compassex.load</code></td>
		<td>Allows access to the load command</td>
	</tr>
	<tr>
		<td><code>compassex.remove.private</code></td>
		<td>Allows you to remove owned private compass targets</td>
	</tr>
	<tr>
		<td><code>compassex.remove.private.any</code></td>
		<td>Allows you to remove any private compass target, owned and unowned</td>
	</tr>
	<tr>
		<td><code>compassex.remove.public</code></td>
		<td>Allows you to remove owned public compass targets</td>
	</tr>
	<tr>
		<td><code>compassex.remove.public.any</code></td>
		<td>Allows you to remove any public compass target, owned and unowned</td>
	</tr>
	<tr>
		<td><code>compassex.list</code></td>
		<td>Allows you to list all public, and owned private compass targets</td>
	</tr>
	<tr>
		<td><code>compassex.list.any</code></td>
		<td>Allows you to list all public, and private compass targets, owned and unowned</td>
	</tr>
	<tr>
		<td><code>compassex.info</code></td>
		<td>Allows access to get info about public, and owned private compass targets</td>
	</tr>
	<tr>
		<td><code>compassex.info.any</code></td>
		<td>Allows you to get info about all public and private compass target</td>
	</tr>
	<tr>
		<td><code>compassex.privatize</code></td>
		<td>Allows you to convert owned compass targets to private</td>
	</tr>
	<tr>
		<td><code>compassex.privatize.any</code></td>
		<td>Allows you to convert any compass target to private</td>
	</tr>
	<tr>
		<td><code>compassex.privatize.free</code></td>
		<td>Users with this permission won't have to pay for making a target private</td>
	</tr>
	<tr>
		<td><code>compassex.publicize</code></td>
		<td>Allows you to convert owned compass targets to public</td>
	</tr>
	<tr>
		<td><code>compassex.publicize.any</code></td>
		<td>Allows you to convert any compass target to public</td>
	</tr>
	<tr>
		<td><code>compassex.publicize.free</code></td>
		<td>Users with this permission won't have to pay for making a target public</td>
	</tr>
	
</table>

## Configuration
The configuration is really simple:

* `live-update-rate`: Sets how fast the position of the live tracked player get refreshed. Setting it to a small number may result in poor server performance. Recommended value: 200 (2 seconds).
* `save-cost`: How much a user without <code>compassex.save.free</code> permission has to pay to save a target.
* `privatize-cost`: How much a user without <code>compassex.privatize.free</code> permission has to pay to privatize a target.
* `publicize-cost`: How much a user without <code>compassex.publicize.free</code> permission has to pay to publicize a target.

## More

Do you think you found any bugs? [Submit one here.](https://github.com/Philipp15b/minecraft-bukkit-compassex/issues)

You think you can help by adding a feature or fixing a bug? Go to the [GitHub repository](https://github.com/Philipp15b/minecraft-bukkit-compassex).

<small>Compass image from [MinecraftWiki](http://www.minecraftwiki.net/wiki/File:Compass.png)</small>
