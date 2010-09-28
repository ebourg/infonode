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


// $Id: DynamicUIManager.java,v 1.3 2004/07/06 15:08:44 jesper Exp $
package net.infonode.gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class DynamicUIManager {
  private static final DynamicUIManager instance = new DynamicUIManager();

  private ArrayList listeners = new ArrayList(2);
  private String[] properties = {"win.3d.backgroundColor",
                                 "win.3d.highlightColor",
                                 "win.3d.lightColor",
                                 "win.3d.shadowColor",
                                 "win.frame.activeCaptionColor",
                                 "win.frame.activeCaptionGradientColor",
                                 "win.frame.captionTextColor",
                                 "win.frame.activeBorderColor",
                                 "win.mdi.backgroundColor",
                                 "win.desktop.backgroundColor",
                                 "win.frame.inactiveCaptionColor",
                                 "win.frame.inactiveCaptionGradientColor",
                                 "win.frame.inactiveCaptionTextColor",
                                 "win.frame.inactiveBorderColor",
                                 "win.menu.backgroundColor",
                                 "win.menu.textColor",
                                 "win.frame.textColor?????",
                                 "win.item.highlightColor",
                                 "win.item.highlightTextColor",
                                 "win.tooltip.backgroundColor",
                                 "win.tooltip.textColor",
                                 "win.frame.backgroundColor",
                                 "win.frame.textColor",
                                 "win.item.hotTrackedColor"};
  private Toolkit currentToolkit;

  public static DynamicUIManager getInstance() {
    return instance;
  }

  private DynamicUIManager() {
    final PropertyChangeListener l = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        firePropertyChange(event);
      }
    };

    UIManager.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("lookAndFeel")) {
          setupPropertyListener(l);
          fireLookAndFeelChanged();
        }
      }
    });
    UIManager.getDefaults().addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        firePropertyChange(event);
      }
    });

    setupPropertyListener(l);
  }

  private void setupPropertyListener(PropertyChangeListener l) {
    if (currentToolkit != null)
      for (int i = 0; i < properties.length; i++)
        currentToolkit.removePropertyChangeListener(properties[i], l);

    currentToolkit = Toolkit.getDefaultToolkit();
    for (int i = 0; i < properties.length; i++) {
      currentToolkit.addPropertyChangeListener(properties[i], l);
    }
  }

  public void addListener(DynamicUIManagerListener l) {
    listeners.add(l);
  }

  public void removeListener(DynamicUIManagerListener l) {
    listeners.remove(l);
  }

  private void fireLookAndFeelChanged() {
    Object l[] = listeners.toArray();
    for (int i = 0; i < l.length; i++)
      ((DynamicUIManagerListener) l[i]).lookAndFeelChanged();
  }

  private void firePropertyChange(PropertyChangeEvent event) {
    Object l[] = listeners.toArray();
    for (int i = 0; i < l.length; i++)
      ((DynamicUIManagerListener) l[i]).propertyChange(event);
  }
}

