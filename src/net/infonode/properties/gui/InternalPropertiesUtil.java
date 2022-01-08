/*
 * Copyright (C) 2004 NNL Technology AB
 * Visit www.infonode.net for information about InfoNode(R) 
 * products and how to contact NNL Technology AB.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA 02111-1307, USA.
 */


// $Id: InternalPropertiesUtil.java,v 1.5 2005/12/04 13:46:06 jesper Exp $
package net.infonode.properties.gui;

import net.infonode.gui.panel.BaseContainerUtil;
import net.infonode.gui.shaped.panel.ShapedPanel;
import net.infonode.properties.gui.util.ShapedPanelProperties;
import net.infonode.util.Direction;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.5 $
 */
public class InternalPropertiesUtil {
  private InternalPropertiesUtil() {
  }

  /**
   * Applies the property values to a shaped panel.
   *
   * @param panel the shaped panel on which to apply the property values
   */
  public static final void applyTo(ShapedPanelProperties properties, ShapedPanel panel) {
    applyTo(properties, panel, null);
  }

  /**
   * Applies the property values to a shaped panel.
   *
   * @param panel the shaped panel on which to apply the property values
   */
  public static final void applyTo(ShapedPanelProperties properties, ShapedPanel panel, Direction d) {
    panel.setHorizontalFlip(properties.getHorizontalFlip());
    panel.setVerticalFlip(properties.getVerticalFlip());
    panel.setComponentPainter(properties.getComponentPainter());
    panel.setDirection(d == null ? properties.getDirection() : d);
    panel.setClipChildren(properties.getClipChildren());
    BaseContainerUtil.setForcedOpaque(panel, properties.getOpaque());
    //panel.setForcedOpaque(properties.getOpaque());
  }
}
