/** 
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


// $Id: SmallFlatTheme.java,v 1.10 2004/07/05 13:37:05 jesper Exp $
package net.infonode.tabbedpanel.theme;

import net.infonode.tabbedpanel.TabbedPanelProperties;
import net.infonode.tabbedpanel.TabbedUIDefaults;
import net.infonode.tabbedpanel.border.OpenContentBorder;
import net.infonode.tabbedpanel.border.TabLineBorder;
import net.infonode.tabbedpanel.titledtab.TitledTabProperties;

import java.awt.*;

/**
 * A theme with small fonts and flat look
 *
 * @author $Author: jesper $
 * @version $Revision: 1.10 $
 */
public class SmallFlatTheme {
  private TitledTabProperties tabProperties = new TitledTabProperties();
  private TabbedPanelProperties tabbedPanelProperties = new TabbedPanelProperties();

  /**
   * Constructs a SmallFlatTheme
   */
  public SmallFlatTheme() {
    Color dark = TabbedUIDefaults.getDarkShadow();
    TitledTabProperties tabDefaultProp = TitledTabProperties.getDefaultProperties();

    TabLineBorder border = new TabLineBorder(dark);
    tabProperties.getNormalProperties().getComponentProperties()
            .setBorder(border)
            .setFont(tabDefaultProp.getNormalProperties().getComponentProperties().getFont().deriveFont((float)9))
            .setInsets(new Insets(0, 4, 0, 4));
    tabProperties.getHighlightedProperties().getComponentProperties()
            .setBorder(border);
    tabProperties.setHighlightedRaised(0);

    tabbedPanelProperties
            .getContentPanelProperties().getComponentProperties().setBorder(new OpenContentBorder(dark));
  }

  /**
   * Gets the TitledTabProperties for this theme
   *
   * @return the TitledTabProperties
   */
  public TitledTabProperties getTitledTabProperties() {
    return tabProperties;
  }

  /**
   * Gets the TabbedPanelProperties for this theme
   *
   * @return the TabbedPanelProperties
   */
  public TabbedPanelProperties getTabbedPanelProperties() {
    return tabbedPanelProperties;
  }
}
