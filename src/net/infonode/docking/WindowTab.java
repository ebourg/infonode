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


// $Id: WindowTab.java,v 1.20 2004/07/08 13:05:05 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.properties.WindowTabProperties;
import net.infonode.docking.properties.WindowTabStateProperties;
import net.infonode.gui.ButtonFactory;
import net.infonode.gui.layout.DirectionLayout;
import net.infonode.gui.panel.SimplePanel;
import net.infonode.properties.propertymap.PropertyMap;
import net.infonode.properties.propertymap.PropertyMapListener;
import net.infonode.properties.propertymap.PropertyMapTreeListener;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.tabbedpanel.titledtab.TitledTabStateProperties;
import net.infonode.util.Direction;
import net.infonode.util.Enum;

import javax.swing.*;
import java.awt.event.*;
import java.util.Map;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.20 $
 */
class WindowTab extends TitledTab {
  private static final TitledTabStateProperties EMPTY_PROPERTIES = new TitledTabStateProperties();

  static class State extends Enum {
    public static final State NORMAL = new State(0, "Normal");
    public static final State HIGHLIGHTED = new State(1, "Highlighted");
    public static final State FOCUSED = new State(2, "Focused");

    public static final State[] STATES = {NORMAL, HIGHLIGHTED, FOCUSED};

    private State(int value, String name) {
      super(value, name);
    }
  }

  private DockingWindow window;
  private JButton[] minimizeRestoreButtons = new JButton[State.STATES.length];
  private JButton[] closeButtons = new JButton[State.STATES.length];
  private WindowTabProperties windowTabProperties = new WindowTabProperties(new WindowTabProperties());
  private SimplePanel[] buttonBoxes = new SimplePanel[State.STATES.length];
  private boolean isFocused;

  WindowTab(DockingWindow _window, boolean emptyContent) {
    super(_window.getTitle(), _window.getIcon(), emptyContent ? null : _window, null);
    this.window = _window;

    for (int i = 0; i < State.STATES.length; i++) {
      minimizeRestoreButtons[i] = ButtonFactory.createFlatHighlightButton(
          null, "Restore", 0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (getWindow().isMinimized())
                getWindow().restore();
              else
                getWindow().minimize();
            }
          });
      minimizeRestoreButtons[i].setFocusable(false);

      closeButtons[i] = ButtonFactory.createFlatHighlightButton(
          null, "Close Tab", 0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              getWindow().close();
            }
          });
      closeButtons[i].setFocusable(false);

      buttonBoxes[i] = new SimplePanel(new DirectionLayout(Direction.RIGHT));
      buttonBoxes[i].add(minimizeRestoreButtons[i]);
      buttonBoxes[i].add(closeButtons[i]);
    }

    setHighlightedStateTitleComponent(buttonBoxes[State.HIGHLIGHTED.getValue()]);
    setNormalStateTitleComponent(buttonBoxes[State.NORMAL.getValue()]);

    MouseListener mouseListener = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger() && contains(e.getPoint())) {
          window.showMenu(e);
        }
      }

      public void mouseReleased(MouseEvent e) {
        mousePressed(e);
      }

      public void mouseClicked(MouseEvent e) {
        final RootWindow root = window.getRootWindow();

        if (isSelected() && root != null) {
          // Focus can move when content components are swapped in the tab panel
          root.startIgnoreFocusChanges();

          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  root.stopIgnoreFocusChanges();
                  window.restoreFocus();
                }
              });
            }
          });
        }
      }
    };

    addMouseListener(mouseListener);

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        TabbedPanel tabbedPanel = getTabbedPanel();

        if (tabbedPanel != null && tabbedPanel.tabAreaContainsPoint(SwingUtilities.convertPoint(WindowTab.this, e.getPoint(), tabbedPanel))) {
          getWindow().getRootWindow().setText(null, null);
          getWindow().getRootWindow().setRectangle(null);
        }
      }
    });

    super.getProperties().addSuperObject(this.windowTabProperties.getTitledTabProperties());

    this.windowTabProperties.getMap().addTreeListener(new PropertyMapTreeListener() {
      public void propertyValuesChanged(Map changes) {
        updateButtons();
      }
    });

    window.getWindowProperties().getMap().addListener(new PropertyMapListener() {
      public void propertyValuesChanged(PropertyMap propertyObject, Map changes) {
        updateButtons();
      }
    });

    this.windowTabProperties.getTitledTabProperties().getHighlightedProperties().addSuperObject(EMPTY_PROPERTIES);
  }

  public void updateUI() {
    super.updateUI();

    if (buttonBoxes != null)
      for (int i = 0; i < 3; i++)
        if (buttonBoxes[i] != null)
          SwingUtilities.updateComponentTreeUI(buttonBoxes[i]);
  }

  void setFocused(boolean focused) {
    if (isFocused != focused) {
      this.isFocused = focused;
      TitledTabStateProperties properties = focused ? this.windowTabProperties.getFocusedProperties() : EMPTY_PROPERTIES;
      this.windowTabProperties.getTitledTabProperties().getHighlightedProperties().getMap().removeSuperMap();
      this.windowTabProperties.getTitledTabProperties().getHighlightedProperties().addSuperObject(properties);
      setHighlightedStateTitleComponent(buttonBoxes[focused ? State.FOCUSED.getValue() : State.HIGHLIGHTED.getValue()]);
    }
  }

  void setProperties(WindowTabProperties properties) {
    this.windowTabProperties.getMap().removeSuperMap();
    this.windowTabProperties.addSuperObject(properties);
  }

  void updateButtons() {
    for (int i = 0; i < State.STATES.length; i++) {
      State state = State.STATES[i];
      WindowTabStateProperties buttonProperties =
          state == State.FOCUSED ? windowTabProperties.getFocusedButtonProperties() :
          state == State.HIGHLIGHTED ? windowTabProperties.getHighlightedButtonProperties() :
          windowTabProperties.getNormalButtonProperties();

      closeButtons[i].setIcon(buttonProperties.getCloseButtonProperties().getIcon());
      closeButtons[i].setVisible(buttonProperties.getCloseButtonProperties().isVisible());

      minimizeRestoreButtons[i].setIcon(window.isMinimized() ? buttonProperties.getRestoreButtonProperties().getIcon() :
                                        buttonProperties.getMinimizeButtonProperties().getIcon());
      minimizeRestoreButtons[i].setToolTipText(window.isMinimized() ? "Restore" : "Minimize");
      minimizeRestoreButtons[i].setVisible(window.isMinimized() ? buttonProperties.getRestoreButtonProperties().isVisible() :
                                           (buttonProperties.getMinimizeButtonProperties().isVisible() && window.isMinimizable()));

      Direction dir = (state == State.NORMAL ? getProperties().getNormalProperties() :
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
}
