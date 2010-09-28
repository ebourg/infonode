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


// $Id: WindowMenuUtil.java,v 1.11 2004/11/11 14:09:46 jesper Exp $
package net.infonode.docking.util;

import net.infonode.docking.*;
import net.infonode.gui.MenuUtil;
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
 * @version $Revision: 1.11 $
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
      final Direction[] directions = Direction.getDirections();

      for (int i = 0; i < 4; i++) {
        final Direction dir = directions[i];

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
    if ((window.isMinimized() || window.isMaximized()) && window.isRestorable())
      menu.add("Restore").addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          window.restore();
        }
      });

    if (window.isMinimizable() && !window.isMinimized()) {
      menu.add("Minimize").addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          window.minimize();
        }
      });
    }

    if (!window.isMaximized() && window.isMaximizable() && window instanceof TabWindow)
      menu.add("Maximize").addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          window.maximize();
        }
      });

    if (window.isClosable())
      menu.add("Close").addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            window.closeWithAbort();
          }
          catch (OperationAbortedException e1) {
            // Ignore
          }
        }
      });


    JMenu moveToMenu = getMoveToMenuItems(window);

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

  private static void addTabOrientationMenuItems(JPopupMenu menu, DockingWindow window) {
    final AbstractTabWindow tabWindow = getTabWindowFor(window);

    if (tabWindow == null || tabWindow instanceof WindowBar)
      return;

    JMenu orientationMenu = new JMenu("Tab Orientation");
    TabbedPanelProperties properties = tabWindow.getTabWindowProperties().getTabbedPanelProperties();
    final Direction[] directions = Direction.getDirections();

    for (int i = 0; i < directions.length; i++) {
      final Direction dir = directions[i];
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

  private static void addTabDirectionMenuItems(JPopupMenu menu, DockingWindow window) {
    final AbstractTabWindow tabWindow = getTabWindowFor(window);

    if (tabWindow == null)
      return;

    JMenu directionMenu = new JMenu("Tab Direction");
    TitledTabProperties properties = TitledTabProperties.getDefaultProperties();
    properties.addSuperObject(tabWindow.getTabWindowProperties().getTabProperties().getTitledTabProperties());
    final Direction[] directions = Direction.getDirections();

    for (int i = 0; i < directions.length; i++) {
      final Direction dir = directions[i];

      if (dir != Direction.LEFT) {
        JMenuItem item = directionMenu.add(dir.toString());
        item.setEnabled(dir != properties.getNormalProperties().getDirection());
        item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            tabWindow.getTabWindowProperties().getTabProperties().getTitledTabProperties().getNormalProperties()
                .setDirection(dir);
          }
        });
      }
    }

    menu.add(directionMenu);
  }

  private static void addSplitWindowMenuItems(JPopupMenu menu, final DockingWindow window) {
    if (window instanceof SplitWindow) {
      JMenu splitMenu = new JMenu("Split Window");

      splitMenu.add("Center").addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ((SplitWindow) window).setDividerLocation(0.5f);
        }
      });

      splitMenu.add("Flip Orientation").addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ((SplitWindow) window).setHorizontal(!((SplitWindow) window).isHorizontal());
        }
      });

      splitMenu.add("Swap Windows").addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          SplitWindow sw = (SplitWindow) window;
          sw.setWindows(window.getChildWindow(1), window.getChildWindow(0));
          sw.setDividerLocation(1 - sw.getDividerLocation());
        }
      });

      menu.add(splitMenu);
    }
  }

  /**
   * Creates a factory which creates a popup menu containing common window actions.
   *
   * @param viewFactoryManager used for creating a list of views that the user can show
   * @param addTabItems        add items for changing tab direction and orientation
   * @return the window popup menu factory
   */
  public static WindowPopupMenuFactory createWindowMenuFactory(ViewFactoryManager viewFactoryManager,
                                                               boolean addTabItems) {
    return createWindowMenuFactory(viewFactoryManager, addTabItems, true);
  }

  /**
   * Creates a factory which creates a popup menu containing common window actions.
   *
   * @param viewFactoryManager  used for creating a list of views that the user can show
   * @param addTabItems         add items for changing tab direction and orientation
   * @param addSplitWindowItems add items for {@link SplitWindow}'s
   * @return the window popup menu factory
   * @since IDW 1.2.0
   */
  public static WindowPopupMenuFactory createWindowMenuFactory(final ViewFactoryManager viewFactoryManager,
                                                               final boolean addTabItems, final boolean addSplitWindowItems) {
    return new WindowPopupMenuFactory() {
      public JPopupMenu createPopupMenu(DockingWindow window) {
        JPopupMenu menu = new JPopupMenu(window.getTitle());

        if (!(window instanceof RootWindow)) {
          if (!(window instanceof WindowBar)) {
            addWindowMenuItems(menu, window);
            menu.addSeparator();
          }

          if (addTabItems) {
            addTabOrientationMenuItems(menu, window);
            addTabDirectionMenuItems(menu, window);
            menu.addSeparator();
          }

          if (addSplitWindowItems) {
            addSplitWindowMenuItems(menu, window);
            menu.addSeparator();
          }
        }

        addNewViewMenuItems(menu, window, viewFactoryManager);
        MenuUtil.optimizeSeparators(menu);
        return menu;
      }
    };
  }

}
