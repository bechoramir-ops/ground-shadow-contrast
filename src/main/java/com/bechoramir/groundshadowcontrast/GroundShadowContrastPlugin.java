/*
 * Copyright (c) 2026, Amir Bechor, Omer Levy <Bechoramir@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bechoramir.groundshadowcontrast;

import com.google.inject.Provides;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
		name = "Ground Shadow Contrast",
		description = "High-contrast circular indicators for falling attacks and floor hazards",
		tags = {"combat", "overlay", "shadow", "accessibility", "pvm", "raids", "toa", "cox", "tob"}
)
public class GroundShadowContrastPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private GroundShadowContrastConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ShadowOverlay shadowOverlay;

	private final Set<Integer> activeProjectileIds = new HashSet<>();
	private final Set<Integer> activeGraphicsObjectIds = new HashSet<>();

	public static class TimedHazard {
		private final LocalPoint location;
		private final double progress;
		private final boolean shouldGrow;

		public TimedHazard(LocalPoint location, double progress, boolean shouldGrow) {
			this.location = location;
			this.progress = progress;
			this.shouldGrow = shouldGrow;
		}

		public LocalPoint getLocation() {
			return location;
		}

		public double getProgress() {
			return progress;
		}

		public boolean shouldGrow() {
			return shouldGrow;
		}
	}

	@Override
	protected void startUp() throws Exception {
		updateActiveHazards();
		overlayManager.add(shadowOverlay);
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(shadowOverlay);
		activeProjectileIds.clear();
		activeGraphicsObjectIds.clear();
	}

	@Provides
	GroundShadowContrastConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(GroundShadowContrastConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if ("shadowindicator".equals(event.getGroup())) {
			updateActiveHazards();
		}
	}

	private void updateActiveHazards() {
		activeProjectileIds.clear();
		activeGraphicsObjectIds.clear();

		// Chambers of Xeric (Olm)
		if (config.highlightOlm()) {
			activeGraphicsObjectIds.add(1357); // Falling stalactite
		}

		// Tombs of Amascut (Warden P4)
		if (config.highlightWarden()) {
			activeGraphicsObjectIds.add(2241); // Lightning charge floor decal
		}

		// Tombs of Amascut (Ba-Ba)
		if (config.highlightBaba()) {
			activeGraphicsObjectIds.add(2250); // Falling ceiling rubble
			activeGraphicsObjectIds.add(1447); // Shockwave slam center
			activeGraphicsObjectIds.add(1446); // Shockwave slam inner ring
			activeGraphicsObjectIds.add(2111); // Shockwave slam outer ring
		}

		// Theatre of Blood (Bloat)
		if (config.highlightBloat()) {
			Collections.addAll(activeGraphicsObjectIds, 1570, 1571, 1572, 1573); // Falling limbs/flesh
		}

		// The Maggot King
		if (config.highlightMaggotKing()) {
			activeGraphicsObjectIds.add(3998); // Small carrion shadow
			activeGraphicsObjectIds.add(3999); // Large carrion shadow
		}

		// Phosani's Nightmare
		if (config.highlightPhosani()) {
			activeGraphicsObjectIds.add(1767); // Grasping claws / shadows
		}

		// Doom of Mokhaiotl
		if (config.highlightDoom()) {
			Collections.addAll(activeProjectileIds, 3388, 3390, 3391, 3392, 3393, 3394, 3395, 3398);
		}

		// Demonic Gorillas
		if (config.highlightGorillas()) {
			activeProjectileIds.add(856); // Falling boulder
		}

		// Grotesque Guardians
		if (config.highlightGrotesque()) {
			Collections.addAll(activeGraphicsObjectIds, 1889, 1890, 1938, 1449); // Debris & lightning
		}

		// The Leviathan
		if (config.highlightLeviathan()) {
			Collections.addAll(activeGraphicsObjectIds, 2475, 2476, 2477, 2478, 2479, 2480); // Enrage debris
		}

		// Tormented Demons
		if (config.highlightTormentedDemons()) {
			activeProjectileIds.add(2866); // Special firebomb projectile
		}
	}

	private int getGraphicsObjectDuration(int id) {
		switch (id) {
			case 2241: // Warden P4 Lightning (3 ticks)
			case 2250: // Ba-Ba falling rubble (3 ticks)
			case 3998:
			case 3999: // Maggot King carrion (3 ticks)
			case 2475:
			case 2476:
			case 2477:
			case 2478:
			case 2479:
			case 2480: // Leviathan debris (3 ticks)
				return 90;
			case 1357: // Olm stalactites (4 ticks)
			case 1767: // Phosani's Nightmare claws (4 ticks)
			case 1570:
			case 1571:
			case 1572:
			case 1573: // Bloat limbs (4 ticks)
			case 1889:
			case 1890:
			case 1938:
			case 1449: // Grotesque Guardians (4 ticks)
				return 120;
			default:
				return 90;
		}
	}

	public List<TimedHazard> getActiveHazards() {
		List<TimedHazard> hazards = new ArrayList<>();
		int currentCycle = client.getGameCycle();

		// Projectiles
		for (Projectile p : client.getProjectiles()) {
			if (activeProjectileIds.contains(p.getId())) {
				if (currentCycle >= p.getStartCycle()) {
					WorldPoint wp = p.getTargetPoint();
					LocalPoint lp = (wp != null) ? LocalPoint.fromWorld(client, wp) : p.getTarget();
					if (lp != null) {
						hazards.add(new TimedHazard(lp, 1.0, false));
					}
				}
			}
		}

		// Graphics Objects
		for (GraphicsObject go : client.getGraphicsObjects()) {
			if (activeGraphicsObjectIds.contains(go.getId()) && !go.finished() && currentCycle >= go.getStartCycle()) {
				LocalPoint lp = go.getLocation();
				if (lp != null) {
					boolean shouldGrow = (go.getId() == 2241); // Warden P4 lightning grows dynamically
					int duration = getGraphicsObjectDuration(go.getId());
					int elapsed = currentCycle - go.getStartCycle();
					double progress = duration > 0 ? (double) elapsed / duration : 1.0;

					hazards.add(new TimedHazard(lp, Math.max(0.0, Math.min(1.0, progress)), shouldGrow));
				}
			}
		}

		return hazards;
	}
}