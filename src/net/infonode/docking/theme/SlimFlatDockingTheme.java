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


//$Id: SlimFlatDockingTheme.java,v 1.16 2005/02/16 11:28:14 jesper Exp $
package net.infonode.docking.theme;

import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.properties.WindowBarProperties;
import net.infonode.docking.properties.WindowTabProperties;
import net.infonode.gui.icon.button.CloseIcon;
import net.infonode.gui.icon.button.MaximizeIcon;
import net.infonode.gui.icon.button.MinimizeIcon;
import net.infonode.gui.icon.button.RestoreIcon;
import net.infonode.tabbedpanel.TabLayoutPolicy;
import net.infonode.tabbedpanel.TabbedPanelProperties;
import net.infonode.tabbedpanel.border.TabAreaLineBorder;
import net.infonode.tabbedpanel.theme.SmallFlatTheme;

import javax.swing.border.Border;
import java.awt.*;

/**
 * A theme very slim theme that doesn't waste any screen space.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.16 $
 */
public final class SlimFlatDockingTheme extends DockingWindowsTheme {
  private RootWindowProperties rootWindowProperties;

  public SlimFlatDockingTheme() {
    rootWindowProperties = createRootWindowProperties();
  }

  public String getName() {
    return "Slim Flat Theme";
  }

  public RootWindowProperties getRootWindowProperties() {
    return rootWindowProperties;
  }

  /**
   * Create a root window properties object with the property values for this theme.
   *
   * @return the root window properties object
   */
  public static final RootWindowProperties createRootWindowProperties() {
    SmallFlatTheme smallFlatTheme = new SmallFlatTheme();

    RootWindowProperties rootWindowProperties = new RootWindowProperties();
    rootWindowProperties.getWindowAreaProperties()
        .setInsets(new Insets(0, 0, 0, 0))
        .setBorder(null);

    rootWindowProperties.getSplitWindowProperties().setDividerSize(3);

    TabbedPanelProperties tpProperties = rootWindowProperties.getTabWindowProperties().getTabbedPanelProperties();
    tpProperties.addSuperObject(smallFlatTheme.getTabbedPanelProperties());
    tpProperties.setShadowEnabled(false).setTabLayoutPolicy(TabLayoutPolicy.COMPRESSION);
    tpProperties.getTabAreaComponentsProperties().getComponentProperties().setInsets(new Insets(0, 1, 0, 1));

    WindowTabProperties tabProperties = rootWindowProperties.getTabWindowProperties().getTabProperties();
    tabProperties.getTitledTabProperties().addSuperObject(smallFlatTheme.getTitledTabProperties());

    tabProperties.getTitledTabProperties().getHighlightedProperties().getComponentProperties().setFont(
        tabProperties.getTitledTabProperties().getHighlightedProperties().getComponentProperties().getFont().
        deriveFont(
            tabProperties.getTitledTabProperties().getNormalProperties().getComponentProperties().getFont().getSize2D()));

    tabProperties.getNormalButtonProperties().getCloseButtonProperties().setIcon(new CloseIcon(8));
    tabProperties.getNormalButtonProperties().getRestoreButtonProperties().setIcon(new RestoreIcon(8));
    tabProperties.getNormalButtonProperties().getMinimizeButtonProperties().setIcon(new MinimizeIcon(8));

    rootWindowProperties.getTabWindowProperties().getCloseButtonProperties().setIcon(new CloseIcon(8));
    rootWindowProperties.getTabWindowProperties().getRestoreButtonProperties().setIcon(new RestoreIcon(8));
    rootWindowProperties.getTabWindowProperties().getMinimizeButtonProperties().setIcon(new MinimizeIcon(8));
    rootWindowProperties.getTabWindowProperties().getMaximizeButtonProperties().setIcon(new MaximizeIcon(8));

    setWindowBarProperties(rootWindowProperties.getWindowBarProperties());

    return rootWindowProperties;
  }

  private static void setWindowBarProperties(WindowBarProperties windowBarProperties) {
    windowBarProperties.setMinimumWidth(3);

    Border border = new TabAreaLineBorder(false, true, true, false);

    windowBarProperties.getTabWindowProperties().getTabProperties().getTitledTabProperties().getNormalProperties()
        .getComponentProperties().setInsets(new Insets(0, 4, 0, 4))
        .setBorder(border);

    windowBarProperties.getTabWindowProperties().getTabProperties().getTitledTabProperties().getHighlightedProperties()
        .getComponentProperties().setBorder(border);
  }

  /**
   * Create a window bar properties object with the property values for this theme.
   *
   * @return the root window properties object
   * @deprecated the window bar properties are now included in the root window properties
   */
  public static final WindowBarProperties createWindowBarProperties() {
    return new WindowBarProperties();
  }
}
