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


// $Id: TabbedHoverUtil.java,v 1.6 2005/02/16 11:28:14 jesper Exp $

package net.infonode.tabbedpanel.internal;

import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.TabbedPanelContentPanel;
import net.infonode.tabbedpanel.TabbedPanelHoverPolicy;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author johan
 */
public class TabbedHoverUtil {

  public static boolean isDeepestHoverableTabbedPanel(ArrayList enterables, TabbedPanel tp) {
    Component c = (Component) enterables.get(0);
    while (c != null) {
      if (c instanceof TabbedPanel)
        if (c == tp)
          return true;
        else
          return false;

      if (c instanceof TabbedPanelContentPanel)
        if (((TabbedPanelContentPanel) c).getTabbedPanel() == tp)
          return true;
        else
          return false;

      c = c.getParent();
    }

    return true;
  }

  public static boolean hasVisibleTabbedPanelChild(Component c) {
    if (c instanceof TabbedPanel)
      return true;

    if (c instanceof Container) {
      Container container = ((Container) c);
      for (int i = 0; i < container.getComponentCount(); i++) {
        if (container.getComponent(i).isVisible() && hasVisibleTabbedPanelChild(container.getComponent(i)))
          return true;
      }
    }

    return false;
  }

  public static boolean acceptTabbedPanelHover(TabbedPanelHoverPolicy policy,
                                               ArrayList enterables,
                                               TabbedPanel tp,
                                               Component c) {
    if (policy == TabbedPanelHoverPolicy.NO_HOVERED_CHILD)
      return isDeepestHoverableTabbedPanel(enterables, tp);

    if (policy == TabbedPanelHoverPolicy.NEVER)
      return false;

    if (policy == TabbedPanelHoverPolicy.ALWAYS)
      return true;

    if (policy == TabbedPanelHoverPolicy.ONLY_WHEN_DEEPEST && c != null)
      return !hasVisibleTabbedPanelChild(c);

    return false;
  }
}