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

import java.awt.Color;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("groundshadowcontrast")
public interface GroundShadowContrastConfig extends Config {
	@ConfigSection(
			name = "Visual Settings",
			description = "Configure circle appearance",
			position = 0
	)
	String visualSection = "visualSection";

	@Range(min = 10, max = 150)
	@ConfigItem(
			keyName = "discRadius",
			name = "Disc Radius",
			description = "Radius of the circular shadow overlay in pixels",
			position = 1,
			section = visualSection
	)
	default int discRadius() {
		return 60;
	}

	@Alpha
	@ConfigItem(
			keyName = "shadowColor",
			name = "Shadow Fill Color",
			description = "Inner fill color and transparency of the shadow indicator",
			position = 2,
			section = visualSection
	)
	default Color shadowColor() {
		return new Color(255, 0, 0, 100);
	}

	@Alpha
	@ConfigItem(
			keyName = "borderColor",
			name = "Border Ring Color",
			description = "Outline border color showing the hazard perimeter",
			position = 3,
			section = visualSection
	)
	default Color borderColor() {
		return new Color(255, 255, 255, 220);
	}

	// ----------------------------------------------------
	// Raids
	// ----------------------------------------------------

	@ConfigSection(
			name = "Raids",
			description = "Raid encounter toggles",
			position = 10
	)
	String raidsSection = "raidsSection";

	@ConfigItem(
			keyName = "highlightOlm",
			name = "Chambers of Xeric (Olm)",
			description = "Highlight falling stalactites during Great Olm",
			position = 11,
			section = raidsSection
	)
	default boolean highlightOlm() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightWarden",
			name = "Tombs of Amascut (Warden P4)",
			description = "Highlight expanding lightning floor attacks during Warden final phase",
			position = 12,
			section = raidsSection
	)
	default boolean highlightWarden() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightBaba",
			name = "Tombs of Amascut (Ba-Ba)",
			description = "Highlight falling ceiling rubble and sequential shockwave slams",
			position = 13,
			section = raidsSection
	)
	default boolean highlightBaba() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightBloat",
			name = "Theatre of Blood (Bloat)",
			description = "Highlight falling flesh and limbs in Pestilent Bloat's room",
			position = 14,
			section = raidsSection
	)
	default boolean highlightBloat() {
		return true;
	}

	// ----------------------------------------------------
	// Bosses & Monsters
	// ----------------------------------------------------

	@ConfigSection(
			name = "Bosses & Monsters",
			description = "Standard boss and monster toggles",
			position = 20
	)
	String bossSection = "bossSection";

	@ConfigItem(
			keyName = "highlightMaggotKing",
			name = "The Maggot King",
			description = "Highlight falling ceiling carrion shadows",
			position = 21,
			section = bossSection
	)
	default boolean highlightMaggotKing() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightPhosani",
			name = "Phosani's Nightmare",
			description = "Highlight grasping claws and nightmare shadow floor decals",
			position = 22,
			section = bossSection
	)
	default boolean highlightPhosani() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightDoom",
			name = "Doom of Mokhaiotl",
			description = "Highlight exploding boulder projectile destinations",
			position = 23,
			section = bossSection
	)
	default boolean highlightDoom() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightLeviathan",
			name = "The Leviathan",
			description = "Highlight enrage phase falling ceiling debris",
			position = 24,
			section = bossSection
	)
	default boolean highlightLeviathan() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightGrotesque",
			name = "Grotesque Guardians",
			description = "Highlight falling rocks and lightning zones",
			position = 25,
			section = bossSection
	)
	default boolean highlightGrotesque() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightGorillas",
			name = "Demonic Gorillas",
			description = "Highlight falling ceiling boulder attacks",
			position = 26,
			section = bossSection
	)
	default boolean highlightGorillas() {
		return true;
	}

	@ConfigItem(
			keyName = "highlightTormentedDemons",
			name = "Tormented Demons",
			description = "Highlight special firebomb projectile target locations",
			position = 27,
			section = bossSection
	)
	default boolean highlightTormentedDemons() {
		return true;
	}
}