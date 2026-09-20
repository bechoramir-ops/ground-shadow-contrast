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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.List;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ShadowOverlay extends Overlay {
	private final Client client;
	private final GroundShadowContrastPlugin plugin;
	private final GroundShadowContrastConfig config;

	@Inject
	public ShadowOverlay(Client client, GroundShadowContrastPlugin plugin, GroundShadowContrastConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		List<GroundShadowContrastPlugin.TimedHazard> hazards = plugin.getActiveHazards();
		if (hazards == null || hazards.isEmpty()) {
			return null;
		}

		int maxRadius = config.discRadius();
		Color baseColor = config.shadowColor();
		Color borderColor = config.borderColor();

		for (GroundShadowContrastPlugin.TimedHazard hazard : hazards) {
			LocalPoint lp = hazard.getLocation();
			if (lp == null) {
				continue;
			}

			// 1. Draw outer boundary ring on every hazard
			renderRing(graphics, lp, maxRadius, borderColor);

			// 2. Render inner fill (dynamic scale for Warden, full size for others)
			if (hazard.shouldGrow()) {
				double scale = 0.5 + (0.5 * hazard.getProgress());
				int currentRadius = (int) (maxRadius * scale);
				renderDisc(graphics, lp, currentRadius, baseColor);
			} else {
				renderDisc(graphics, lp, maxRadius, baseColor);
			}
		}

		return null;
	}

	private void renderDisc(Graphics2D graphics, LocalPoint center, int radius, Color color) {
		Polygon poly = getCirclePolygon(center, radius);
		if (poly != null) {
			graphics.setColor(color);
			graphics.fill(poly);
		}
	}

	private void renderRing(Graphics2D graphics, LocalPoint center, int radius, Color color) {
		Polygon poly = getCirclePolygon(center, radius);
		if (poly != null) {
			Stroke originalStroke = graphics.getStroke();
			graphics.setStroke(new BasicStroke(2.0f));
			graphics.setColor(color);
			graphics.draw(poly);
			graphics.setStroke(originalStroke);
		}
	}

	private Polygon getCirclePolygon(LocalPoint center, int radius) {
		Polygon poly = new Polygon();
		int plane = client.getPlane();

		for (int i = 0; i < 24; i++) {
			double angle = i * 2 * Math.PI / 24;
			int dx = (int) (radius * Math.cos(angle));
			int dy = (int) (radius * Math.sin(angle));

			LocalPoint edgePoint = new LocalPoint(center.getX() + dx, center.getY() + dy);
			Point canvasPoint = Perspective.localToCanvas(client, edgePoint, plane);

			if (canvasPoint != null) {
				poly.addPoint(canvasPoint.getX(), canvasPoint.getY());
			}
		}

		return poly.npoints > 0 ? poly : null;
	}
}