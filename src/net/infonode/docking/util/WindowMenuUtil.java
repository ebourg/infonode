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


// $Id: WindowMenuUtil.java,v 1.3 2004/07/05 13:03:40 jesper Exp $
package net.infonode.docking.util;

import net.infonode.docking.*;
import net.infonode.tabbedpanel.TabbedPanelProperties;
import net.infonode.tabbedpanel.titledtab.TitledTabProperties;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class containing utility methods for creating window popup menues.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.3 $
 */
public final class WindowMenuUtil {
  private WindowMenuUtil() {
  }

  private static AbstractTabWindow getTabWindowFor(DockingWindow window) {
    return (AbstractTabWindow)
        (window instanceof AbstractTabWindow ? window :
        window.getWindowParent() != null && window.getWindowParent() instanceof AbstractTabWindow ? window.getWindowParent() :
        null);
  }

  private static JMenu getMoveToMenuItems(final DockingWindow window) {
    JMenu moveToMenu = new JMenu("Move to Window Bar");

    if (window.isMinimizable()) {
      final RootWindow root = window.getRootWindow();

      for (int i = 0; i < 4; i++) {
        final Direction dir = Direction.DIRECTIONS[i];

        if (!DockingUtil.isAncestor(root.getWindowBar(dir), window) && root.getWindowBar(dir).isEnabled()) {
          moveToMenu.add(dir.getName()).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              root.getWindowBar(dir).addTab(window);
            }
          });
        }
      }
    }

    return moveToMenu;
  }

  private static void addWindowMenuItems(JPopupMenu menu, final DockingWindow window) {
    if (window.isMinimizable()) {
      menu.add(window.isMinimized() ? "Restore" : "Minimize").addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (window.isMinimized())
            window.restore();
          else
            window.minimize();
        }
      });
    }

    menu.add("Close").addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        window.close();
      }
    });


    JMenu moveToMenu = WindowMenuUtil.getMoveToMenuItems(window);

    if (moveToMenu.getItemCount() > 0) {
      menu.add(moveToMenu);
    }
  }

  private static void addNewViewMenuItems(JPopupMenu menu, final DockingWindow window, ViewFactoryManager viewManager) {
    ViewFactory[] viewFactories = viewManager.getViewFactories();

    if (viewFactories.length == 0)
      return;

    JMenu viewsPopup = new JMenu("Show View");

    for (int i = 0; i < viewFactories.length; i++) {
      final ViewFactory vf = viewFactories[i];

      viewsPopup.add(new JMenuItem(vf.getTitle(), vf.getIcon())).addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          View view = vf.createView();

          if (window instanceof RootWindow)
            ((RootWindow) window).setWindow(view);
          else {
            AbstractTabWindow tabWindow = getTabWindowFor(window);

            if (tabWindow != null)
              tabWindow.addTab(view);
          }
        }
      });
    }

    menu.add(viewsPopup);
  }

  private static void addTabOrientationMenuItems(JPopupMenu menu, final DockingWindow window) {
    final AbstractTabWindow tabWindow = getTabWindowFor(window);

    if (tabWindow == null || tabWindow instanceof WindowBar)
      return;

    JMenu orientationMenu = new JMenu("Tab Orientation");
    TabbedPanelProperties properties = tabWindow.getTabWindowProperties().getTabbedPanelProperties();

    for (int i = 0; i < Direction.DIRECTIONS.length; i++) {
      final Direction dir = Direction.DIRECTIONS[i];
      JMenuItem item = orientationMenu.add(dir.toString());
      item.setEnabled(dir != properties.getTabAreaOrientation());
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tabWindow.getTabWindowProperties().getTabbedPanelProperties().setTabAreaOrientation(dir);
        }
      });
    }

    menu.add(orientationMenu);
  }

  private static void addTabDirectionMenuItems(JPopupMenu menu, final DockingWindow window) {
    final AbstractTabWindow tabWindow = getTabWindowFor(window);

    if (tabWindow == null)
      return;

    JMenu directionMenu = new JMenu("Tab Direction");
    TitledTabProperties properties = TitledTabProperties.getDefaultProperties();
    properties.addSuperObject(tabWindow.getTabWindowProperties().getTabProperties().getTitledTabProperties());

    for (int i = 0; i < Direction.DIRECTIONS.length; i++) {
      final Direction dir = Direction.DIRECTIONS[i];

      if (dir != Direction.LEFT) {
        JMenuItem item = directionMenu.add(dir.toString());
        item.setEnabled(dir != properties.getNormalProperties().getDirection());
        item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            tabWindow.getTabWindowProperties().getTabProperties().getTitledTabProperties().getNormalProperties().setDirection(dir);
          }
        });
      }
    }

    menu.add(directionMenu);
  }

  /**
   * Creates a factory which creates a popup menu containing common window actions.
   *
   * @param viewFactoryManager used for creating a list of views that the user can show
   * @param addTabItems add items for changing tab direction and orientation
   * @return the window popup menu factory
   */
  public static WindowPopupMenuFactory createWindowMenuFactory(final ViewFactoryManager viewFactoryManager,
                                                               final boolean addTabItems) {
    return new WindowPopupMenuFactory() {
      public JPopupMenu createPopupMenu(DockingWindow window) {
        JPopupMenu menu = new JPopupMenu(window.getTitle());

        if (!(window instanceof RootWindow)) {
          if (!(window instanceof WindowBar)) {
            WindowMenuUtil.addWindowMenuItems(menu, window);
            menu.addSeparator();
          }

          if (addTabItems) {
            WindowMenuUtil.addTabOrientationMenuItems(menu, window);
            WindowMenuUtil.addTabDirectionMenuItems(menu, window);
            menu.addSeparator();
          }
        }

        WindowMenuUtil.addNewViewMenuItems(menu, window, viewFactoryManager);
        return menu;
      }
    };
  }
}
