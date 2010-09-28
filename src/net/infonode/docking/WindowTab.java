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


// $Id: WindowTab.java,v 1.36 2004/11/11 14:09:46 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.internalutil.*;
import net.infonode.docking.properties.WindowTabProperties;
import net.infonode.docking.properties.WindowTabStateProperties;
import net.infonode.gui.layout.DirectionLayout;
import net.infonode.gui.panel.SimplePanel;
import net.infonode.properties.propertymap.PropertyMap;
import net.infonode.properties.propertymap.PropertyMapListener;
import net.infonode.properties.propertymap.PropertyMapTreeListener;
import net.infonode.properties.propertymap.PropertyMapWeakListenerManager;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.tabbedpanel.titledtab.TitledTabStateProperties;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.36 $
 */
class WindowTab extends TitledTab {
  private static final TitledTabStateProperties EMPTY_PROPERTIES = new TitledTabStateProperties();

  private static final ButtonInfo[] buttonInfos = {new MinimizeButtonInfo(
      WindowTabStateProperties.MINIMIZE_BUTTON_PROPERTIES),
                                                   new RestoreButtonInfo(
                                                       WindowTabStateProperties.RESTORE_BUTTON_PROPERTIES),
                                                   new CloseButtonInfo(
                                                       WindowTabStateProperties.CLOSE_BUTTON_PROPERTIES)};

  private final DockingWindow window;
  private AbstractButton[][] buttons = new AbstractButton[WindowTabState.STATES.length][];
  private SimplePanel[] buttonBoxes = new SimplePanel[WindowTabState.STATES.length];
  private WindowTabProperties windowTabProperties = new WindowTabProperties(new WindowTabProperties());
  private boolean isFocused;

  private PropertyMapListener windowPropertiesListener = new PropertyMapListener() {
    public void propertyValuesChanged(PropertyMap propertyObject, Map changes) {
      updateButtons();
    }
  };

  private PropertyMapTreeListener windowTabPropertiesListener = new PropertyMapTreeListener() {
    public void propertyValuesChanged(Map changes) {
      updateButtons();
    }
  };

  WindowTab(DockingWindow window, boolean emptyContent) {
    super(window.getTitle(), window.getIcon(), emptyContent ? null : new SimplePanel(window), null);
    this.window = window;

    for (int i = 0; i < WindowTabState.STATES.length; i++) {
      buttonBoxes[i] = new SimplePanel(new DirectionLayout(Direction.RIGHT));
      buttons[i] = new AbstractButton[buttonInfos.length];
    }

    setHighlightedStateTitleComponent(buttonBoxes[WindowTabState.HIGHLIGHTED.getValue()]);
    setNormalStateTitleComponent(buttonBoxes[WindowTabState.NORMAL.getValue()]);

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        checkPopupMenu(e);

        if (e.getButton() == MouseEvent.BUTTON1 && isSelected() && WindowTab.this.window.getRootWindow() != null)
          WindowTab.this.window.restoreFocus();
      }

      public void mouseReleased(MouseEvent e) {
        checkPopupMenu(e);
      }

      private void checkPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger() && contains(e.getPoint())) {
          WindowTab.this.window.showMenu(e);
        }
      }

    });

    super.getProperties().addSuperObject(windowTabProperties.getTitledTabProperties());

    PropertyMapWeakListenerManager.addWeakTreeListener(windowTabProperties.getMap(), windowTabPropertiesListener);

    PropertyMapWeakListenerManager.addWeakListener(this.window.getWindowProperties().getMap(),
                                                   windowPropertiesListener);

    windowTabProperties.getTitledTabProperties().getHighlightedProperties().addSuperObject(EMPTY_PROPERTIES);
  }

  public void updateUI() {
    super.updateUI();

    if (buttonBoxes != null)
      for (int i = 0; i < WindowTabState.STATES.length; i++)
        if (buttonBoxes[i] != null)
          SwingUtilities.updateComponentTreeUI(buttonBoxes[i]);
  }

  void setFocused(boolean focused) {
    if (isFocused != focused) {
      isFocused = focused;
      TitledTabStateProperties properties = focused ? windowTabProperties.getFocusedProperties() : EMPTY_PROPERTIES;
      windowTabProperties.getTitledTabProperties().getHighlightedProperties().getMap().removeSuperMap();
      windowTabProperties.getTitledTabProperties().getHighlightedProperties().addSuperObject(properties);
      setHighlightedStateTitleComponent(buttonBoxes[focused ?
                                                    WindowTabState.FOCUSED.getValue() :
                                                    WindowTabState.HIGHLIGHTED.getValue()]);
    }
  }

  void setProperties(WindowTabProperties properties) {
    windowTabProperties.getMap().removeSuperMap();
    windowTabProperties.addSuperObject(properties);
  }

  void updateButtons() {
    for (int i = 0; i < WindowTabState.STATES.length; i++) {
      WindowTabState state = WindowTabState.STATES[i];
      WindowTabStateProperties buttonProperties =
          state == WindowTabState.FOCUSED ? windowTabProperties.getFocusedButtonProperties() :
          state == WindowTabState.HIGHLIGHTED ? windowTabProperties.getHighlightedButtonProperties() :
          windowTabProperties.getNormalButtonProperties();

      InternalDockingUtil.updateButtons(buttonInfos, buttons[i], buttonBoxes[i], window, buttonProperties.getMap());
      Direction dir = (state == WindowTabState.NORMAL ? getProperties().getNormalProperties() :
                       getProperties().getHighlightedProperties()).getDirection();

      if (dir != ((DirectionLayout) buttonBoxes[i].getLayout()).getDirection()) {
        ((DirectionLayout) buttonBoxes[i].getLayout()).setDirection(dir);
      }
    }
  }

  DockingWindow getWindow() {
    return window;
  }

  void windowTitleChanged() {
    setText(getWindow().getTitle());
    setIcon(getWindow().getIcon());
  }

  public String toString() {
    return window.toString();
  }

  void setContentComponent(Component component) {
    ((SimplePanel) getContentComponent()).setComponent(component);
  }
}
