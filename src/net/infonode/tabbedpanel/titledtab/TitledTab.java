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


// $Id: TitledTab.java,v 1.22 2004/09/28 15:07:29 jesper Exp $
package net.infonode.tabbedpanel.titledtab;

import net.infonode.gui.InsetsUtil;
import net.infonode.gui.RotatableLabel;
import net.infonode.gui.border.FocusBorder;
import net.infonode.gui.icon.IconProvider;
import net.infonode.gui.layout.StackableLayout;
import net.infonode.properties.propertymap.PropertyMapTreeListener;
import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabAdapter;
import net.infonode.tabbedpanel.TabEvent;
import net.infonode.tabbedpanel.TabRemovedEvent;
import net.infonode.util.Alignment;
import net.infonode.util.Direction;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Map;

/**
 * <p>A TitledTab is a tab that has support for text, icon and a custom Swing component
 * (called title component). Titled tab supports several properties that makes it possible
 * to change the look (borders, colors, insets), layout (up, down, left, right).</p>
 *
 * <p>Titled tab has a line based layout, i.e. the text, icon and title component are
 * laid out in a line. The layout of the tab can be rotated, i.e. the text and the icon will
 * be rotated 90, 180 or 270 degrees. The title component will not be rotated but moved so
 * that the line layout will persist.</p>
 *
 * <p>A titled tab has 3 rendering states:
 * <ul>
 * <li>Normal - The tab is selectable but not yet selected
 * <li>Highlighted - The tab is either highlighted or selected
 * <li>Disabled - The tab is disabled and cannot be selected or highlighted
 * </ul>Most of the properties for the tab can be configured for each of the tab rendering
 * states.</p>
 *
 * <p><strong>Note:</strong> If only the normal state properties have been configured, the
 * highlighted and disabled state will automatically use the same properties as for the normal
 * state, see {@link TitledTabProperties} and {@link TitledTabStateProperties}.</p>
 *
 * <p>TitledTab implements the {@link net.infonode.gui.icon.IconProvider} interface and
 * overloads toString() so that both text and icon for the normal state is shown in the
 * tab drop down list in a tabbed panel.</p>
 *
 * @author $Author: jesper $
 * @version $Revision: 1.22 $
 * @see TitledTabProperties
 * @see TitledTabStateProperties
 */
public class TitledTab extends Tab implements IconProvider {

  private class StatePanel extends JPanel {
    private JPanel panel = new JPanel(new BorderLayout());
    private JPanel titleComponentPanel = new JPanel(new BorderLayout());
    private RotatableLabel label = new RotatableLabel(null, null);
    private JComponent titleComponent;
    private boolean titleComponentChanged = true;
    private Direction currentLayoutDirection;
    private int currentLayoutGap = -1;
    private Alignment currentLayoutAlignment;
    private String toolTipText;

    StatePanel() {
      super(new BorderLayout());
      setOpaque(false);
      panel.setOpaque(false);
      titleComponentPanel.setOpaque(false);

      label.setBorder(new FocusBorder(label));
      label.setMinimumSize(new Dimension(0, 0));
      panel.add(label, BorderLayout.CENTER);
      add(panel, BorderLayout.CENTER);
    }

    public void updateStatePanel(TitledTabStateProperties properties, Border outerBorder, Border innerBorder) {
      titleComponentChanged = true;

      toolTipText = properties.getToolTipEnabled() ? properties.getToolTipText() : null;
      if (toolTipText != null && toolTipText.length() == 0)
        toolTipText = null;

      setBorder(outerBorder);
      updateLayout(properties);
      updateLabel(properties);
      updatePanel(innerBorder, properties);
    }

    public String getToolTipText() {
      return toolTipText;
    }

    public JComponent getTitleComponent() {
      return titleComponent;
    }

    public void setTitleComponent(JComponent titleComponent) {
      JComponent oldTitleComponent = this.titleComponent;
      this.titleComponent = null;
      if (oldTitleComponent != null && oldTitleComponent.getParent() == titleComponentPanel)
        titleComponentPanel.remove(oldTitleComponent);
      this.titleComponent = titleComponent;
      titleComponentChanged = true;
    }

    public void activateTitleComponent() {
      if (titleComponent != null) {
        if (titleComponent.getParent() != titleComponentPanel) {
          if (titleComponent.getParent() != null)
            titleComponent.getParent().remove(titleComponent);
          titleComponentPanel.add(titleComponent, BorderLayout.CENTER);
        }
      }
      else {
        titleComponentPanel.removeAll();
      }
    }

    public void activate() {
      remove(panel);
      eventPanel.add(panel, BorderLayout.CENTER);
      add(eventPanel, BorderLayout.CENTER);
    }

    public void deactivate() {
      remove(eventPanel);
      eventPanel.remove(panel);
      add(panel, BorderLayout.CENTER);
    }

    public Dimension getPreferredSize() {
      activateTitleComponent();
      return super.getPreferredSize();
    }

    public Dimension getMinimumSize() {
      activateTitleComponent();
      return super.getMinimumSize();
    }

    public Dimension getMaximumSize() {
      activateTitleComponent();
      return super.getMaximumSize();
    }

    public JComponent getFocusableComponent() {
      return label;
    }

    private void updateLayout(TitledTabStateProperties properties) {
      if (titleComponent != null && properties.getTitleComponentVisible()) {
        Direction d = properties.getDirection();
        int gap;
        if (properties.getIconVisible() || properties.getTextVisible())
          gap = properties.getTextTitleComponentGap();
        else
          gap = 0;
        Alignment alignment = properties.getTitleComponentTextRelativeAlignment();
        if (titleComponentPanel.getComponentCount() == 0 ||
            (titleComponentPanel.getComponentCount() > 0 && titleComponentPanel.getComponent(0) != titleComponent) ||
            titleComponentChanged ||
            gap != currentLayoutGap ||
            alignment != currentLayoutAlignment ||
            d != currentLayoutDirection) {
          titleComponentChanged = false;
          currentLayoutDirection = d;
          currentLayoutGap = gap;
          currentLayoutAlignment = alignment;

          panel.remove(titleComponentPanel);
          if (d == Direction.UP) {
            panel.add(titleComponentPanel, alignment == Alignment.LEFT ? BorderLayout.SOUTH : BorderLayout.NORTH);
            titleComponentPanel.setBorder(new EmptyBorder(alignment == Alignment.LEFT ? gap : 0, 0, alignment == Alignment.LEFT ? 0 : gap, 0));
          }
          else if (d == Direction.LEFT) {
            panel.add(titleComponentPanel, alignment == Alignment.LEFT ? BorderLayout.EAST : BorderLayout.WEST);
            titleComponentPanel.setBorder(new EmptyBorder(0, alignment == Alignment.LEFT ? gap : 0, 0, alignment == Alignment.LEFT ? 0 : gap));
          }
          else if (d == Direction.DOWN) {
            panel.add(titleComponentPanel, alignment == Alignment.LEFT ? BorderLayout.NORTH : BorderLayout.SOUTH);
            titleComponentPanel.setBorder(new EmptyBorder(alignment == Alignment.LEFT ? 0 : gap, 0, alignment == Alignment.LEFT ? gap : 0, 0));
          }
          else {
            panel.add(titleComponentPanel, alignment == Alignment.LEFT ? BorderLayout.WEST : BorderLayout.EAST);
            titleComponentPanel.setBorder(new EmptyBorder(0, alignment == Alignment.LEFT ? 0 : gap, 0, alignment == Alignment.LEFT ? gap : 0));
          }
        }
      }
      else {
        panel.remove(titleComponentPanel);
        titleComponentPanel.removeAll();
      }
    }

    private void updateLabel(TitledTabStateProperties properties) {
      if (properties.getIconVisible())
        label.setIcon(properties.getIcon());
      else
        label.setIcon(null);
      if (properties.getTextVisible())
        label.setText(properties.getText());
      else
        label.setText(null);
      label.setFont(properties.getComponentProperties().getFont());
      label.setForeground(properties.getComponentProperties().getForegroundColor());
      if (properties.getIconVisible() && properties.getTextVisible())
        label.setIconTextGap(properties.getIconTextGap());
      else
        label.setIconTextGap(0);
      label.setDirection(properties.getDirection());

      Alignment alignment = properties.getHorizontalAlignment();
      label.setHorizontalAlignment(alignment == Alignment.LEFT ? JLabel.LEFT :
                                   alignment == Alignment.CENTER ? JLabel.CENTER :
                                   JLabel.RIGHT);
      alignment = properties.getVerticalAlignment();
      label.setVerticalAlignment(alignment == Alignment.TOP ? JLabel.TOP :
                                 alignment == Alignment.CENTER ? JLabel.CENTER :
                                 JLabel.BOTTOM);
      alignment = properties.getIconTextRelativeAlignment();
      label.setHorizontalTextPosition(alignment == Alignment.LEFT ? JLabel.RIGHT :
                                      JLabel.LEFT);
    }

    private void updatePanel(Border border, TitledTabStateProperties properties) {
      panel.setBorder(border);
      panel.setForeground(properties.getComponentProperties().getForegroundColor());
      panel.setBackground(properties.getComponentProperties().getBackgroundColor());
      panel.setOpaque(properties.getComponentProperties().getBackgroundColor() != null);
    }
  }

  private TitledTabProperties properties = TitledTabProperties.getDefaultProperties();

  private JPanel eventPanel = new JPanel(new BorderLayout());

  private StatePanel normalStatePanel = new StatePanel();
  private StatePanel highlightedStatePanel = new StatePanel();
  private StatePanel disabledStatePanel = new StatePanel();

  private ArrayList mouseListeners;
  private ArrayList mouseMotionListeners;
  private StackableLayout layout;
  private StatePanel currentStatePanel;

  private PropertyMapTreeListener propertiesListener = new PropertyMapTreeListener() {
    public void propertyValuesChanged(Map changes) {
      updateTab();
    }
  };

  /**
   * Constructs a TitledTab with a text, icon, content component and title component.
   *
   * @param text             text or null for no text. The text will be applied to the
   *                         normal state properties
   * @param icon             icon or null for no icon. The icon will be applied to the
   *                         normal state properties
   * @param contentComponent content component or null for no content component
   * @param titleComponent   title component or null for no title component. The title
   *                         component will be applied to all the states
   * @see net.infonode.tabbedpanel.TabFactory
   */
  public TitledTab(String text, Icon icon, JComponent contentComponent, JComponent titleComponent) {
    super(contentComponent);

    eventPanel.setOpaque(false);

    layout = new StackableLayout(this) {
      public void layoutContainer(Container parent) {
        super.layoutContainer(parent);
        StatePanel visibleStatePanel = (StatePanel) getVisibleComponent();
        visibleStatePanel.activateTitleComponent();
        setFocusableComponent(properties.getFocusable() ? visibleStatePanel.getFocusableComponent() : null);
      }
    };
    setLayout(layout);

    add(normalStatePanel);
    add(highlightedStatePanel);
    add(disabledStatePanel);

    setText(text);
    setIcon(icon);
    setTitleComponent(titleComponent);
    setEventComponent(eventPanel);

    MouseListener mouseListener = new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        if (mouseListeners != null) {
          MouseEvent event = convertMouseEvent(e);
          Object[] l = mouseListeners.toArray();
          for (int i = 0; i < l.length; i++)
            ((MouseListener) l[i]).mouseClicked(event);
        }
      }

      public void mousePressed(MouseEvent e) {
        if (mouseListeners != null) {
          MouseEvent event = convertMouseEvent(e);
          Object[] l = mouseListeners.toArray();
          for (int i = 0; i < l.length; i++)
            ((MouseListener) l[i]).mousePressed(event);
        }
      }

      public void mouseReleased(MouseEvent e) {
        if (mouseListeners != null) {
          MouseEvent event = convertMouseEvent(e);
          Object[] l = mouseListeners.toArray();
          for (int i = 0; i < l.length; i++)
            ((MouseListener) l[i]).mouseReleased(event);
        }
      }

      public void mouseEntered(MouseEvent e) {
        if (mouseListeners != null) {
          MouseEvent event = convertMouseEvent(e);
          Object[] l = mouseListeners.toArray();
          for (int i = 0; i < l.length; i++)
            ((MouseListener) l[i]).mouseEntered(event);
        }
      }

      public void mouseExited(MouseEvent e) {
        if (mouseListeners != null) {
          MouseEvent event = convertMouseEvent(e);
          Object[] l = mouseListeners.toArray();
          for (int i = 0; i < l.length; i++)
            ((MouseListener) l[i]).mouseExited(event);
        }
      }
    };

    MouseMotionListener mouseMotionListener = new MouseMotionListener() {
      public void mouseDragged(MouseEvent e) {
        if (mouseMotionListeners != null) {
          MouseEvent event = convertMouseEvent(e);
          Object[] l = mouseMotionListeners.toArray();
          for (int i = 0; i < l.length; i++)
            ((MouseMotionListener) l[i]).mouseDragged(event);
        }
      }

      public void mouseMoved(MouseEvent e) {
        if (mouseMotionListeners != null) {
          MouseEvent event = convertMouseEvent(e);
          Object[] l = mouseMotionListeners.toArray();
          for (int i = 0; i < l.length; i++)
            ((MouseMotionListener) l[i]).mouseMoved(event);
        }
      }
    };

    eventPanel.addMouseListener(mouseListener);
    eventPanel.addMouseMotionListener(mouseMotionListener);

    properties.getMap().addTreeListener(propertiesListener);

    addTabListener(new TabAdapter() {
      public void tabAdded(TabEvent event) {
        getTabbedPanel().getProperties().getMap().addTreeListener(propertiesListener);
        updateTab();
      }

      public void tabRemoved(TabRemovedEvent event) {
        event.getTabbedPanel().getProperties().getMap().removeTreeListener(propertiesListener);
        updateTab();
      }
    });
  }

  /**
   * Gets the title component for the normal state
   *
   * @return title component or null if no title component
   */
  public JComponent getNormalStateTitleComponent() {
    return normalStatePanel.getTitleComponent();
  }

  /**
   * Gets the title component for the highlighted state
   *
   * @return title component or null if no title component
   */
  public JComponent getHighlightedStateTitleComponent() {
    return highlightedStatePanel.getTitleComponent();
  }

  /**
   * Gets the title component for the disabled state
   *
   * @return title component or null if no title component
   */
  public JComponent getDisabledStateTitleComponent() {
    return disabledStatePanel.getTitleComponent();
  }

  /**
   * <p>Sets the title component.</p>
   *
   * <p>This method is a convenience method for setting the same title component for
   * all states.</p>
   *
   * @param titleComponent the title component or null for no title component
   */
  public void setTitleComponent(JComponent titleComponent) {
    normalStatePanel.setTitleComponent(titleComponent);
    highlightedStatePanel.setTitleComponent(titleComponent);
    disabledStatePanel.setTitleComponent(titleComponent);
    updateTab();
  }

  /**
   * Sets the normal state title component
   *
   * @param titleComponent the title component or null for no title component
   */
  public void setNormalStateTitleComponent(JComponent titleComponent) {
    normalStatePanel.setTitleComponent(titleComponent);
    updateTab();
  }

  /**
   * Sets the highlighted state title component
   *
   * @param titleComponent the title component or null for no title component
   */
  public void setHighlightedStateTitleComponent(JComponent titleComponent) {
    highlightedStatePanel.setTitleComponent(titleComponent);
    updateTab();
  }

  /**
   * Sets the disabled state title component
   *
   * @param titleComponent the title component or null for no title component
   */
  public void setDisabledStateTitleComponent(JComponent titleComponent) {
    disabledStatePanel.setTitleComponent(titleComponent);
    updateTab();
  }

  /**
   * <p>Sets if this TitledTab should be highlighted or not.</p>
   *
   * <p><strong>Note:</strong> This will only have effect if this TitledTab
   * is enabled and a member of a tabbed panel.</p>
   *
   * @param highlighted true for highlight, otherwise false
   */
  public void setHighlighted(boolean highlighted) {
    super.setHighlighted(highlighted);
    updateCurrentStatePanel();
  }

  /**
   * Sets if this TitledTab should be enabled or disabled
   *
   * @param enabled true for enabled, otherwise false
   */
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    updateCurrentStatePanel();
  }

  /**
   * Gets the text for the normal state
   *
   * @return the text or null if no text
   */
  public String getText() {
    return properties.getNormalProperties().getText();
  }

  /**
   * Sets the text for the normal state
   *
   * @param text the text or null for no text
   */
  public void setText(String text) {
    properties.getNormalProperties().setText(text);
  }

  /**
   * Gets the icon for the normal state
   *
   * @return the icon or null if none
   */
  public Icon getIcon() {
    return properties.getNormalProperties().getIcon();
  }

  /**
   * Sets the icon for the normal state
   *
   * @param icon the icon or null for no icon
   */
  public void setIcon(Icon icon) {
    properties.getNormalProperties().setIcon(icon);
  }

  /**
   * Gets the TitledTabProperties
   *
   * @return the TitledTabProperties for this TitledTab
   */
  public TitledTabProperties getProperties() {
    return properties;
  }

  /**
   * Gets the text for the normal state.
   *
   * Same as getText().
   *
   * @return the text or null if no text
   * @see #getText
   * @since ITP 1.1.0
   */
  public String toString() {
    return getText();
  }

  /**
   * Adds a MouseListener to receive mouse events from this TitledTab.
   *
   * @param l the MouseListener
   */
  public synchronized void addMouseListener(MouseListener l) {
    if (mouseListeners == null)
      mouseListeners = new ArrayList(2);
    mouseListeners.add(l);
  }

  /**
   * Removes a MouseListener
   *
   * @param l the MouseListener to remove
   */
  public synchronized void removeMouseListener(MouseListener l) {
    if (mouseListeners != null) {
      mouseListeners.remove(l);

      if (mouseListeners.size() == 0)
        mouseListeners = null;
    }
  }

  /**
   * Gets the mouse listeners
   *
   * @return the mouse listeners
   */
  public synchronized MouseListener[] getMouseListeners() {
    MouseListener[] listeners = new MouseListener[0];

    if (mouseListeners != null) {
      Object[] l = mouseListeners.toArray();
      listeners = new MouseListener[l.length];
      for (int i = 0; i < l.length; i++)
        listeners[i] = (MouseListener) l[i];
    }

    return listeners;
  }

  /**
   * Adds a MouseMotionListener to receive mouse events from this TitledTab.
   *
   * @param l the MouseMotionListener
   */
  public synchronized void addMouseMotionListener(MouseMotionListener l) {
    if (mouseMotionListeners == null)
      mouseMotionListeners = new ArrayList(2);

    mouseMotionListeners.add(l);
  }

  /**
   * Removes a MouseMotionListener
   *
   * @param l the MouseMotionListener to remove
   */
  public synchronized void removeMouseMotionListener(MouseMotionListener l) {
    if (mouseMotionListeners != null) {
      mouseMotionListeners.remove(l);

      if (mouseMotionListeners.size() == 0)
        mouseMotionListeners = null;
    }
  }

  /**
   * Gets the mouse motion listeners
   *
   * @return the mouse motion listeners
   */
  public synchronized MouseMotionListener[] getMouseMotionListeners() {
    MouseMotionListener[] listeners = new MouseMotionListener[0];

    if (mouseMotionListeners != null) {
      Object[] l = mouseMotionListeners.toArray();
      listeners = new MouseMotionListener[l.length];
      for (int i = 0; i < l.length; i++)
        listeners[i] = (MouseMotionListener) l[i];
    }

    return listeners;
  }

  private Insets getBorderInsets(Border border) {
    return border == null ? InsetsUtil.EMPTY_INSETS : border.getBorderInsets(this);
  }

  private void updateTab() {
    if (getTabbedPanel() != null) {
      layout.setUseSelectedComponentSize(getProperties().getSizePolicy() == TitledTabSizePolicy.INDIVIDUAL_SIZE);

      Direction tabOrientation = getTabbedPanel().getProperties().getTabAreaOrientation();
      int raised = properties.getHighlightedRaised();
      Insets notRaised = InsetsUtil.setInset(InsetsUtil.EMPTY_INSETS, tabOrientation, raised);
      Border normalBorder = new EmptyBorder(notRaised);

      Insets maxInsets = properties.getBorderSizePolicy() == TitledTabBorderSizePolicy.INDIVIDUAL_SIZE ?
                         null :
                         InsetsUtil.max(getBorderInsets(properties.getNormalProperties().getComponentProperties().getBorder()),
                                        InsetsUtil.max(getBorderInsets(properties.getHighlightedProperties().getComponentProperties().getBorder()),
                                                       getBorderInsets(properties.getDisabledProperties().getComponentProperties().getBorder())));

      Insets normalInsets = InsetsUtil.rotate(properties.getNormalProperties().getDirection(),
                                              properties.getNormalProperties().getComponentProperties().getInsets());

      Insets disabledInsets = InsetsUtil.rotate(properties.getDisabledProperties().getDirection(),
                                                properties.getDisabledProperties().getComponentProperties().getInsets());

      int edgeInset = Math.min(InsetsUtil.getInset(normalInsets,
                                                   tabOrientation.getOpposite()),
                               InsetsUtil.getInset(disabledInsets,
                                                   tabOrientation.getOpposite()));

      int normalLowered = Math.min(edgeInset, raised);

      Border innerNormalBorder = getInnerBorder(properties.getNormalProperties(), tabOrientation, -normalLowered, maxInsets);
      Border innerHighlightBorder = getInnerBorder(properties.getHighlightedProperties(), tabOrientation, raised - normalLowered, maxInsets);
      Border innerDisabledBorder = getInnerBorder(properties.getDisabledProperties(), tabOrientation, -normalLowered, maxInsets);

      normalStatePanel.updateStatePanel(properties.getNormalProperties(), normalBorder, innerNormalBorder);
      highlightedStatePanel.updateStatePanel(properties.getHighlightedProperties(), null, innerHighlightBorder);
      disabledStatePanel.updateStatePanel(properties.getDisabledProperties(), normalBorder, innerDisabledBorder);

      updateCurrentStatePanel();
    }
  }

  private Border getInnerBorder(TitledTabStateProperties properties,
                                Direction tabOrientation,
                                int raised,
                                Insets maxInsets) {
    Direction tabDir = properties.getDirection();
    Insets insets = InsetsUtil.rotate(tabDir, properties.getComponentProperties().getInsets());

    if (maxInsets != null)
      insets = InsetsUtil.add(insets, InsetsUtil.sub(maxInsets,
                                                     getBorderInsets(properties.getComponentProperties().getBorder())));

    Border border = properties.getComponentProperties().getBorder();
    Border innerBorder = new EmptyBorder(InsetsUtil.add(insets,
                                                        InsetsUtil.setInset(InsetsUtil.EMPTY_INSETS,
                                                                            tabOrientation.getOpposite(),
                                                                            raised)));
    return border == null ? innerBorder : new CompoundBorder(border, innerBorder);
  }

  private void updateCurrentStatePanel() {
    StatePanel newStatePanel = normalStatePanel;
    if (!isEnabled())
      newStatePanel = disabledStatePanel;
    else if (isHighlighted())
      newStatePanel = highlightedStatePanel;

    eventPanel.setToolTipText(newStatePanel.getToolTipText());

    if (currentStatePanel != newStatePanel) {
      if (currentStatePanel != null)
        currentStatePanel.deactivate();
      currentStatePanel = newStatePanel;
      currentStatePanel.activate();
    }
    layout.showComponent(currentStatePanel);
  }

  private MouseEvent convertMouseEvent(MouseEvent e) {
    Point p = SwingUtilities.convertPoint((JComponent) e.getSource(), e.getPoint(), TitledTab.this);
    return new MouseEvent(TitledTab.this, e.getID(), e.getWhen(), e.getModifiers(),
                          (int) p.getX(), (int) p.getY(), e.getClickCount(),
                          !e.isConsumed() && e.isPopupTrigger(), e.getButton());
  }
}