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


// $Id: TabbedPanel.java,v 1.30 2004/08/03 09:23:23 johan Exp $
package net.infonode.tabbedpanel;

import net.infonode.gui.ComponentUtils;
import net.infonode.gui.draggable.*;
import net.infonode.properties.propertymap.PropertyMapTreeListener;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.util.ColorUtil;
import net.infonode.util.Direction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * <p>A TabbedPanel is a component that handles a group of components in a notebook like manor. Each component is
 * represented by a {@link Tab}. A tab is a component itself that defines how the tab will be rendered. The tab also
 * holds a reference to the content component associated with the tab. The tabbed panel is divided into two
 * areas, the tab area where the tabs are displayed and the content area where the tab's content component is
 * displayed.</p>
 *
 * <p>The tabbed panel is configured using a {@link TabbedPanelProperties} object. A tabbed panel will always have a
 * properties object with default values based on the current Look and Feel</p>
 *
 * <p>Tabs can be added, inserted, removed, selected, highlighted, dragged and moved.</p>
 *
 * <p>The tabbed panel support tab placement in a horizontal line above or under the content area or a vertical row to the
 * left or to the right of the content area. The tab line can be laid out as either scrolling or compression. If the tabs are
 * too many to fit in the tab area and scrolling is enabled, then the mouse wheel is activated and scrollbuttons are shown
 * so that the tabs can be scrolled. Compression means that the tabs will be downsized to fit into the visible tab area.</p>
 *
 * <p>It is possible to add a {@link TabListener} and receive events when a tab is added, removed, selected, deselected,
 * highlighted, dehighlighted, moved, dragged, dropped or drag is aborted. The listener will receive events for all the tabs
 * in the tabbed panel. A tabbed panel will trigger selected, deselected, highlighted and dehighlighted even if for example
 * the selected tab is null (no selected tab), i.e. null will be treated as if it was a tab.</p>
 *
 * @see Tab
 * @see TitledTab
 * @see TabbedPanelProperties
 * @see TabListener
 * @author $Author: johan $
 * @version $Revision: 1.30 $
 */
public class TabbedPanel extends JPanel {
  // Shadow property values
  private int shadowSize = 4;
  private int shadowBlendSize = 4;
  private float shadowStrength = 0.4F;
  private Color shadowColor;

  private JComponent contentPanel;

  private Direction tabAreaOrientation;
  private DraggableComponentBox draggableComponentBox = new DraggableComponentBox();
  private ArrayList listeners;
  private ArrayList tabs = new ArrayList(4);
  private TabbedPanelProperties properties = new TabbedPanelProperties(TabbedPanelProperties.getDefaultProperties());
  private Tab highlightedTab;
  private boolean settingHighlighted = false;
  private ShadowPanel componentsPanel = new ShadowPanel();

  private DraggableComponentBoxListener draggableComponentBoxListener = new DraggableComponentBoxListener() {
    private boolean selectedMoved = false;

    public void componentSelected(DraggableComponentBoxEvent event) {
      if (event.getDraggableComponent() == event.getOldDraggableComponent()) {
        if (!selectedMoved && properties.getTabDeselectable())
          draggableComponentBox.selectDraggableComponent(null);
      }
      else {
        Tab tab = findTab(event.getDraggableComponent());
        setHighlightedTab(tab);
        fireSelectedEvent(tab, findTab(event.getOldDraggableComponent()));
      }
    }

    public void componentRemoved(DraggableComponentBoxEvent event) {
      Tab tab = findTab(event.getDraggableComponent());
      tabs.remove(tab);
      if (highlightedTab == tab)
        highlightedTab = null;
      tab.setTabbedPanel(null);

      revalidate();
      repaint();
      fireRemovedEvent(tab);
    }

    public void componentAdded(DraggableComponentBoxEvent event) {
      revalidate();
      repaint();
      fireAddedEvent(findTab(event.getDraggableComponent()));
    }

    public void componentDragged(DraggableComponentBoxEvent event) {
      fireDraggedEvent(findTab(event.getDraggableComponent()), event.getDraggableComponentBoxPoint());
    }

    public void componentDropped(DraggableComponentBoxEvent event) {
      if (!draggableComponentBox.contains(event.getDraggableComponentBoxPoint()))
        setHighlightedTab(findTab(draggableComponentBox.getSelectedDraggableComponent()));
      fireDroppedEvent(findTab(event.getDraggableComponent()), event.getDraggableComponentBoxPoint());
    }

    public void componentDragAborted(DraggableComponentBoxEvent event) {
      fireNotDroppedEvent(findTab(event.getDraggableComponent()));
    }

    public void changed(DraggableComponentBoxEvent event) {
      if (event.getDraggableComponentEvent() != null) {
        int type = event.getDraggableComponentEvent().getType();

        if (type == DraggableComponentEvent.TYPE_PRESSED && getProperties().getHighlightPressedTab())
          setHighlightedTab(findTab(event.getDraggableComponent()));
        else if (type == DraggableComponentEvent.TYPE_RELEASED) {
          selectedMoved = false;
          setHighlightedTab(getSelectedTab());
        }
        else if (type == DraggableComponentEvent.TYPE_DISABLED &&
            highlightedTab != null &&
            highlightedTab.getDraggableComponent() == event.getDraggableComponent())
          setHighlightedTab(null);
        else if (type == DraggableComponentEvent.TYPE_ENABLED &&
            draggableComponentBox.getSelectedDraggableComponent() == event.getDraggableComponent())
          setHighlightedTab(findTab(event.getDraggableComponent()));
        else if (type == DraggableComponentEvent.TYPE_MOVED) {
          selectedMoved = event.getDraggableComponent() == draggableComponentBox.getSelectedDraggableComponent();
          fireTabMoved(findTab(event.getDraggableComponent()));
        }
      }

      updateShadow();
    }
  };

  private PropertyMapTreeListener propertyChangedListener = new PropertyMapTreeListener() {
    public void propertyValuesChanged(Map changes) {
      updateProperties();
    }
  };

  /**
   * Constructs a TabbedPanel with a TabbedPanelContentPanel as content
   * area component and with default TabbedPanelProperties
   *
   * @see TabbedPanelProperties
   * @see TabbedPanelContentPanel
   */
  public TabbedPanel() {
    initialize(new TabbedPanelContentPanel(this, new TabContentPanel(this)));
  }

  /**
   * <p>Constructs a TabbedPanel with a custom component as content area component
   * or without any content area component and with default TabbedPanelProperties.</p>
   *
   * <p>If no content area component is used, then the tabbed panel will act as a bar and
   * the tabs will be lai out in a line.</p>
   *
   * <p><strong>Note:</strong> A custom content area component is by itself responsible for
   * showing a tab's content component when a tab is selected, for eaxmple by listening to
   * events from the tabbed panel. The component will be laid out just as the default content
   * area component so that shadows etc. can be used.</p>
   *
   * @param contentAreaComponent  component to be used as content area component or null for no
   *                              content area component
   * @see TabbedPanelProperties
   */
  public TabbedPanel(JComponent contentAreaComponent) {
    initialize(contentAreaComponent);
  }

  /**
   * Check if the tab area contains the given point
   *
   * @param p the point to check. Must be relative to this tabbed panel.
   * @return  true if tab area contains point, otherwise false
   * @see #contentAreaContainsPoint
   */
  public boolean tabAreaContainsPoint(Point p) {
    return draggableComponentBox.contains(SwingUtilities.convertPoint(this, p, draggableComponentBox));
  }

  /**
   * Check if the content area contains the given point
   *
   * @param p the point to check. Must be relative to this tabbed panel.
   * @return  true if content area contains point, otherwise false
   * @see #tabAreaContainsPoint
   */
  public boolean contentAreaContainsPoint(Point p) {
    return contentPanel != null ? contentPanel.contains(SwingUtilities.convertPoint(this, p, contentPanel)) : false;
  }

  /**
   * <p>Add a tab. The tab will be added after the last tab.</p>
   *
   * <p>If the tab to be added is the only tab in this tabbed panel and the property
   * "Auto Select Tab" is enabled then the tab will become selected in this tabbed panel
   * after the tab has been added.</p>
   *
   * @param tab tab to be added
   * @see #insertTab(Tab, int)
   * @see TabbedPanelProperties
   */
  public void addTab(Tab tab) {
    doInsertTab(tab, null, -1);
  }

  /**
   * <p>Insert a tab at the specified tab index (position).</p>
   *
   * <p>If the tab to be inserted is the only tab in this tabbed panel and the property
   * "Auto Select Tab" is enabled then the tab will become selected in this tabbed panel
   * after the tab has been inserted.</p>
   *
   * @param tab   tab to be inserted
   * @param index the index to insert tab at
   * @see #addTab
   * @see TabbedPanelProperties
   */
  public void insertTab(Tab tab, int index) {
    doInsertTab(tab, null, index);
  }

  /**
   * <p>Insert a tab at the specified point.</p>
   *
   * <p>If the point is outside the tab area then the tab will be inserted after the last tab.</p>
   *
   * <p>If the tab to be inserted is the only tab in this tabbed panel and the property
   * "Auto Select Tab" is enabled then the tab will become selected in this tabbed panel
   * after the tab has been inserted.</p>
   *
   * @param tab tab to be inserted
   * @param p   the point to insert tab at. Must be relative to this tabbed panel.
   * @see #addTab
   * @see TabbedPanelProperties
   */
  public void insertTab(Tab tab, Point p) {
    doInsertTab(tab, p, -1);
  }

  /**
   * Removes a tab
   *
   * @param tab tab to be removed from this TabbedPanel
   */
  public void removeTab(Tab tab) {
    if (tab != null)
      draggableComponentBox.removeDraggableComponent(tab.getDraggableComponent());
  }

  /**
   * Move tab to point p. If p is outside the tab area then the tab is not moved.
   *
   * @param tab   tab to move. Tab must be a member (added/inserted) of this
   *              tabbed panel.
   * @param p     point to move tab to. Must be relative to this tabbed panel.
   */
  public void moveTab(Tab tab, Point p) {
    if (tabs.contains(tab))
      draggableComponentBox.dragDraggableComponent(tab.getDraggableComponent(),
                                                   SwingUtilities.convertPoint(this, p, draggableComponentBox));
  }

  /**
   * Selects a tab, i.e. displays the tab's content component in this
   * tabbed panel's content area
   *
   * @param tab tab to select. Tab must be a member (added/inserted)
   *            of this tabbed panel.
   */
  public void setSelectedTab(Tab tab) {
    if (getSelectedTab() == tab)
      return;

    if (tab != null) {
      if (tab.isEnabled() && getTabIndex(tab) > -1) {
        if (tab.getDraggableComponent() != draggableComponentBox.getSelectedDraggableComponent())
          tab.setSelected(true);
        else
          setHighlightedTab(tab);
      }
    }
    else {
      draggableComponentBox.selectDraggableComponent(null);
    }
  }

  /**
   * Gets the selected tab, i.e. the tab who's content component is currently
   * displayed in this tabbed panel's content area
   *
   * @return  the selected tab or null if no tab is selected in
   *          this tabbed panel
   */
  public Tab getSelectedTab() {
    return findTab(draggableComponentBox.getSelectedDraggableComponent());
  }

  /**
   * Sets which tab that should be highlighted, i.e. signal highlighted state to the tab
   *
   * @param highlightedTab  tab that should be highlighted or null if no tab should be
   *                        highlighted. The tab must be a member (added/inserted) of this
   *                        tabbed panel.
   */
  public void setHighlightedTab(Tab highlightedTab) {
    if (!settingHighlighted) {
      settingHighlighted = true;
      Tab oldTab = this.highlightedTab;
      Tab newTab = null;
      if (highlightedTab != null) {
        if (getTabIndex(highlightedTab) > -1) {
          this.highlightedTab = highlightedTab;
          if (oldTab != null && oldTab != highlightedTab)
            oldTab.setHighlighted(false);

          if (oldTab != highlightedTab)
            if (highlightedTab.isEnabled())
              highlightedTab.setHighlighted(true);
            else {
              highlightedTab.setHighlighted(false);
              this.highlightedTab = null;
            }

          if (highlightedTab.isEnabled() && highlightedTab != oldTab)
            newTab = highlightedTab;

          if (oldTab != highlightedTab)
            fireHighlightedEvent(newTab, oldTab);
        }
      }
      else if (oldTab != null) {
        this.highlightedTab = null;
        oldTab.setHighlighted(false);
        fireHighlightedEvent(null, oldTab);
      }

      updateShadow();
      settingHighlighted = false;
    }
  }

  /**
   * Gets the highlighted tab
   *
   * @return  the highlighted tab or null if no tab is highlighted in
   *          this tabbed panel
   */
  public Tab getHighlightedTab() {
    return highlightedTab;
  }

  /**
   * Gets the number of tabs
   *
   * @return number of tabs
   */
  public int getTabCount() {
    return draggableComponentBox.getDraggableComponentCount();
  }

  /**
   * Gets the tab at index
   *
   * @param index   index of tab
   * @return        tab at index
   * @exception     java.lang.ArrayIndexOutOfBoundsException if there is no tab at index
   */
  public Tab getTabAt(int index) {
    return findTab(draggableComponentBox.getDraggableComponentAt(index));
  }

  /**
   * Gets the index for tab
   *
   * @param tab   tab
   * @return      index or -1 if tab is not a member of this TabbedPanel
   */
  public int getTabIndex(Tab tab) {
    return tab == null ? -1 : draggableComponentBox.getDraggableComponentIndex(tab.getDraggableComponent());
  }

  /**
   * Adds a TablListener that will receive events for all the tabs in this TabbedPanel
   *
   * @param listener the TabListener to add
   */
  public void addTabListener(TabListener listener) {
    if (listeners == null)
      listeners = new ArrayList(2);

    listeners.add(listener);
  }

  /**
   * Removes a TabListener
   *
   * @param listener the TabListener to remove
   */
  public void removeTabListener(TabListener listener) {
    if (listeners != null) {
      listeners.remove(listener);

      if (listeners.size() == 0)
        listeners = null;
    }
  }

  /**
   * Gets the TabbedPanelProperties
   *
   * @return the TabbedPanelProperties for this tabbed panel
   */
  public TabbedPanelProperties getProperties() {
    return properties;
  }

  DraggableComponentBox getDraggableComponentBox() {
    return draggableComponentBox;
  }

  private void initialize(JComponent contentPanel) {
    setLayout(new BorderLayout());
    setOpaque(false);

    this.contentPanel = contentPanel;
    properties.getMap().addTreeListener(propertyChangedListener);

    draggableComponentBox.addListener(draggableComponentBoxListener);

    if (contentPanel != null) {
      componentsPanel.add(contentPanel, BorderLayout.CENTER);
    }

    add(componentsPanel, BorderLayout.CENTER);
    updateProperties();
  }

  private void updateProperties() {
    componentsPanel.remove(draggableComponentBox);
    tabAreaOrientation = properties.getTabAreaOrientation();
    updateAllTabsProperties();
    draggableComponentBox.setScrollEnabled(properties.getTabLayoutPolicy() == TabLayoutPolicy.SCROLLING);
    draggableComponentBox.setScrollOffset(properties.getTabScrollingOffset());
    draggableComponentBox.setComponentDirection(tabAreaOrientation);
    draggableComponentBox.setScrollButtonDirection(tabAreaOrientation == Direction.UP ? Direction.DOWN :
                                                   tabAreaOrientation == Direction.LEFT ? Direction.RIGHT :
                                                   tabAreaOrientation == Direction.DOWN ? Direction.UP :
                                                   Direction.LEFT);
    draggableComponentBox.setEnsureSelectedVisible(properties.getEnsureSelectedTabVisible());
    draggableComponentBox.setAutoSelect(properties.getAutoSelectTab());

    draggableComponentBox.setComponentSpacing(properties.getTabSpacing());

    properties.getTabAreaProperties().getComponentProperties().applyTo(draggableComponentBox);

    componentsPanel.add(draggableComponentBox, ComponentUtils.getBorderLayoutOrientation(tabAreaOrientation));

    // Shadow
    shadowSize = properties.getShadowSize();
    shadowBlendSize = properties.getShadowBlendAreaSize();
    shadowStrength = properties.getShadowStrength();
    componentsPanel.setBorder(contentPanel != null && properties.getShadowEnabled() ?
                              new EmptyBorder(0, 0, shadowSize, shadowSize) :
                              null);
    shadowColor = properties.getShadowColor();

    repaint();
    revalidate();
  }

  private void updateAllTabsProperties() {
    for (int i = 0; i < tabs.size(); i++)
      updateTabProperties((Tab) tabs.get(i));
  }

  private void updateTabProperties(Tab tab) {
    tab.getDraggableComponent().setAbortDragKeyCode(properties.getAbortDragKey());
    tab.getDraggableComponent().setReorderEnabled(properties.getTabReorderEnabled());
  }

  private void doInsertTab(Tab tab, Point p, int index) {
    if (tab != null && !tabs.contains(tab)) {
      tab.setTabbedPanel(this);
      tabs.add(tab);
      if (p != null)
        draggableComponentBox.insertDraggableComponent(tab.getDraggableComponent(), SwingUtilities.convertPoint(this, p, draggableComponentBox));
      else
        draggableComponentBox.insertDraggableComponent(tab.getDraggableComponent(), index);
      updateTabProperties(tab);
    }
  }

  private Tab findTab(DraggableComponent draggableComponent) {
    for (int i = 0; i < tabs.size(); i++)
      if (((Tab) tabs.get(i)).getDraggableComponent() == draggableComponent)
        return (Tab) tabs.get(i);

    return null;
  }

  private void fireTabMoved(Tab tab) {
    if (listeners != null) {
      TabEvent event = new TabEvent(this, tab);
      Object l[] = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabMoved(event);
    }
  }

  private void fireDraggedEvent(Tab tab, Point p) {
    if (listeners != null) {
      TabDragEvent event = new TabDragEvent(this, tab, SwingUtilities.convertPoint(draggableComponentBox, p, tab));
      Object l[] = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabDragged(event);
    }
  }

  private void fireDroppedEvent(Tab tab, Point p) {
    if (listeners != null) {
      TabDragEvent event = new TabDragEvent(this, tab, SwingUtilities.convertPoint(draggableComponentBox, p, tab));
      Object l[] = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabDropped(event);
    }
  }

  private void fireNotDroppedEvent(Tab tab) {
    if (listeners != null) {
      TabEvent event = new TabEvent(this, tab);
      Object l[] = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabDragAborted(event);
    }
  }

  private void fireSelectedEvent(Tab tab, Tab oldTab) {
    if (listeners != null) {
      {
        TabStateChangedEvent event = new TabStateChangedEvent(this, this, oldTab, oldTab, tab);
        Object l[] = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((TabListener)l[i]).tabDeselected(event);
      }
      {
        TabStateChangedEvent event = new TabStateChangedEvent(this, this, tab, oldTab, tab);
        Object l[] = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((TabListener)l[i]).tabSelected(event);
      }
    }
  }

  private void fireHighlightedEvent(Tab tab, Tab oldTab) {
    if (listeners != null) {
      {
        TabStateChangedEvent event = new TabStateChangedEvent(this, this, oldTab, oldTab, tab);
        Object l[] = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((TabListener)l[i]).tabDehighlighted(event);
      }
      {
        TabStateChangedEvent event = new TabStateChangedEvent(this, this, tab, oldTab, tab);
        Object l[] = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((TabListener)l[i]).tabHighlighted(event);
      }
    }
  }

  private void fireAddedEvent(Tab tab) {
    if (listeners != null) {
      TabEvent event = new TabEvent(this, tab);
      Object l[] = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabAdded(event);
    }
  }

  private void fireRemovedEvent(Tab tab) {
    if (listeners != null) {
      TabRemovedEvent event = new TabRemovedEvent(this, tab, this);
      Object l[] = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabRemoved(event);
    }
  }


  private void updateShadow() {
    if (contentPanel != null && properties.getShadowEnabled()) {
      repaint(SwingUtilities.convertRectangle(draggableComponentBox.getParent(), draggableComponentBox.getBounds(), TabbedPanel.this));
      Point p = SwingUtilities.convertPoint(draggableComponentBox, draggableComponentBox.getLocation(), this);
      repaint(p.x, p.y, draggableComponentBox.getWidth() + shadowSize, draggableComponentBox.getHeight() + shadowSize);
    }
  }

  private class ShadowPanel extends JPanel {
    private Color panelBackgroundColor;
    private Color tabBackgroundColor;

    public ShadowPanel() {
      super(new BorderLayout());
      setOpaque(false);
      setCursor(null);
    }

    public void paint(Graphics g) {
      super.paint(g);

      if (contentPanel == null || !properties.getShadowEnabled())
        return;

      panelBackgroundColor = ComponentUtils.getBackgroundColor(this);
      panelBackgroundColor = panelBackgroundColor == null ? UIManager.getColor("Panel.background") : panelBackgroundColor;

      tabBackgroundColor = getHighlightedTab() == null ?
          panelBackgroundColor :
          ComponentUtils.getBackgroundColor(getHighlightedTab().getParent());

      tabBackgroundColor = tabBackgroundColor == null ? panelBackgroundColor : tabBackgroundColor;

      final Direction tabOrientation = properties.getTabAreaOrientation();
      Rectangle contentPanelBounds = contentPanel.getBounds();

      int edgeStart = 0, edgeSize = 0;

      if (getHighlightedTab() != null) {
        JComponent selected = getHighlightedTab().getDraggableComponent().getComponent();
        Point p = SwingUtilities.convertPoint(selected.getParent(), selected.getLocation(), this);

        Point tabAreaPos = SwingUtilities.convertPoint(draggableComponentBox, 0, 0, this);
        JComponent tabBox = (JComponent) getTabAt(0).getParent().getParent();
        Rectangle tabsSize = tabBox.getVisibleRect();
        Dimension tabAreaSize = tabBox.getParent().getSize();
        Point tabsPos = SwingUtilities.convertPoint(tabBox, 0, 0, this);

        // Set tab clip
        Shape clip = g.getClipBounds();
        int width = (tabOrientation.isHorizontal() ? 0 : tabsPos.x) + tabsSize.width;
        int height = (tabOrientation.isHorizontal() ? tabsPos.y : 0) + tabsSize.height;
        g.clipRect(tabAreaPos.x,
                   tabAreaPos.y,
                   width + (tabOrientation == Direction.RIGHT || contentPanelBounds.width == width ? shadowSize : 0),
                   height + (tabOrientation == Direction.DOWN || contentPanelBounds.height == height ? shadowSize : 0));

        boolean isLast = getTabIndex(getHighlightedTab()) == getTabCount() - 1;

        if (tabOrientation == Direction.DOWN)
          drawBottomRightTabShadow(g,
                                   contentPanelBounds.x,
                                   contentPanelBounds.y + contentPanelBounds.height,
                                   contentPanelBounds.width,
                                   p.x,
                                   selected.getWidth(),
                                   selected.getHeight(),
                                   false,
                                   isLast);
        else if (tabOrientation == Direction.RIGHT)
          drawBottomRightTabShadow(g,
                                   contentPanelBounds.y,
                                   contentPanelBounds.x + contentPanelBounds.width,
                                   contentPanelBounds.height, p.y,
                                   selected.getHeight(),
                                   selected.getWidth(),
                                   true,
                                   isLast);
        else if (tabOrientation == Direction.UP)
          drawTopLeftTabShadow(g, p.x + selected.getWidth(), p.y, selected.getHeight(), false, isLast);
        else
          drawTopLeftTabShadow(g, p.y + selected.getHeight(), p.x, selected.getWidth(), true, isLast);

        g.setClip(clip);

        if (tabOrientation == Direction.DOWN) {
          drawShadowLine(g,
                         tabsPos.x + tabAreaSize.width,
                         tabAreaPos.x + contentPanelBounds.width,
                         tabAreaPos.y,
                         false,
                         panelBackgroundColor);
        }
        else if (tabOrientation == Direction.RIGHT) {
          drawShadowLine(g,
                         tabsPos.y + tabAreaSize.height,
                         tabAreaPos.y + contentPanelBounds.height,
                         tabAreaPos.x,
                         true,
                         panelBackgroundColor);
        }

        if (isLast) {
          if (p.x + selected.getWidth() >= contentPanelBounds.width &&
              contentPanelBounds.width == width && !tabOrientation.isHorizontal()) {
            edgeStart += tabOrientation == Direction.UP ? height : 0;
            edgeSize += height;
          }
          else if (p.y + selected.getHeight() >= contentPanelBounds.height &&
              contentPanelBounds.height == height && tabOrientation.isHorizontal()) {
            edgeStart += tabOrientation == Direction.LEFT ? width : 0;
            edgeSize += width;
          }
        }
      }

      if (tabOrientation != Direction.RIGHT)
        drawBottomRightEdgeShadow(g,
                                  contentPanelBounds.y - edgeStart,
                                  contentPanelBounds.x + contentPanelBounds.width,
                                  contentPanelBounds.height + edgeSize,
                                  true,
                                  panelBackgroundColor);
      else if (getHighlightedTab() == null)
        drawBottomRightEdgeShadow(g, contentPanelBounds.y - edgeStart, contentPanelBounds.x + contentPanelBounds.width,
                                  contentPanelBounds.height + edgeSize, true, null);

      if (tabOrientation != Direction.DOWN)
        drawBottomRightEdgeShadow(g, contentPanelBounds.x - edgeStart, contentPanelBounds.y + contentPanelBounds.height,
                                  contentPanelBounds.width + edgeSize, false, panelBackgroundColor);
      else if (getHighlightedTab() == null)
        drawBottomRightEdgeShadow(g, contentPanelBounds.x - edgeStart, contentPanelBounds.y + contentPanelBounds.height,
                                  contentPanelBounds.width + edgeSize, false, null);

      drawRightCornerShadow(g,
                            contentPanelBounds.x + contentPanelBounds.width + (tabOrientation == Direction.RIGHT ? edgeSize : 0),
                            contentPanelBounds.y + contentPanelBounds.height + (tabOrientation == Direction.DOWN ? edgeSize : 0),
                            false,
                            panelBackgroundColor);
    }

    private Rectangle createRectangle(int x, int y, int width, int height, boolean flip) {
      return flip ? new Rectangle(y, x, height, width) : new Rectangle(x, y, width, height);
    }

    private void drawTopLeftTabShadow(Graphics g, int x, int y, int height, boolean flip, boolean isLast) {
      drawLeftCornerShadow(g, y, x, !flip, isLast ? tabBackgroundColor : null);
      drawEdgeShadow(g, y, y + height, x, false, !flip, isLast ? tabBackgroundColor : null);
    }

    private void drawBottomRightTabShadow(Graphics g, int x, int y, int width, int tabX, int tabWidth,
                                          int tabHeight, boolean flip, boolean isLast) {
      Shape oldClipRect = g.getClip();
      Rectangle clipRect = createRectangle(x, y, tabX - x, tabHeight + shadowSize, flip);
      g.clipRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

      drawLeftCornerShadow(g, x, y, flip, null);
      drawEdgeShadow(g, x, tabX, y, false, flip, null);

      g.setClip(oldClipRect);

      drawLeftCornerShadow(g, tabX, y + tabHeight, flip, panelBackgroundColor);
      drawEdgeShadow(g, tabX, tabX + tabWidth, y + tabHeight, false, flip, panelBackgroundColor);
      drawRightCornerShadow(g, tabX + tabWidth, y + tabHeight, flip, panelBackgroundColor);
      drawEdgeShadow(g, y, y + tabHeight, tabX + tabWidth, true, !flip, isLast ? tabBackgroundColor : null);
      drawEdgeShadow(g, tabX + tabWidth, x + width, y, true, flip, isLast ? tabBackgroundColor : null);
    }

    private void drawBottomRightEdgeShadow(Graphics g, int x, int y, int width, boolean flip, Color backgroundColor) {
      drawLeftCornerShadow(g, x, y, flip, backgroundColor);
      drawEdgeShadow(g, x, x + width, y, false, flip, backgroundColor);
    }

    private void drawLeftCornerShadow(Graphics g, int x, int y, boolean upper, Color backgroundColor) {
      for (int i = 0; i < shadowBlendSize; i++) {
        g.setColor(getShadowBlendColor(i, backgroundColor));
        int x1 = x + shadowSize + shadowBlendSize - 1 - i;
        int y1 = y + shadowSize - shadowBlendSize;

        if (y1 > y)
          drawLine(g, x1, y, x1, y1 - 1, upper);

        drawLine(g, x1, y1, x + shadowSize + shadowBlendSize - 1, y + shadowSize - shadowBlendSize + i, upper);
      }
    }

    private void drawRightCornerShadow(Graphics g, int x, int y, boolean flip, Color backgroundColor) {
      g.setColor(getShadowColor(backgroundColor));

      for (int i = 0; i < shadowSize - shadowBlendSize; i++) {
        drawLine(g, x + i, y, x + i, y + shadowSize - shadowBlendSize, flip);
      }

      for (int i = 0; i < shadowBlendSize; i++) {
        g.setColor(getShadowBlendColor(i, backgroundColor));
        int d = shadowSize - shadowBlendSize + i;
        drawLine(g, x + d, y, x + d, y + shadowSize - shadowBlendSize, flip);
        drawLine(g, x, y + d, x + shadowSize - shadowBlendSize, y + d, flip);
        drawLine(g, x + d, y + shadowSize - shadowBlendSize, x + shadowSize - shadowBlendSize, y + d, flip);
      }
    }

    private void drawEdgeShadow(Graphics g, int startX, int endX, int y, boolean cornerStart, boolean vertical,
                                Color backgroundColor) {
      if (startX >= endX)
        return;

      g.setColor(getShadowColor(backgroundColor));

      for (int i = 0; i < shadowSize - shadowBlendSize; i++) {
        drawLine(g, startX + (cornerStart ? i + (vertical ? 1 : 0) : shadowSize + shadowBlendSize), y + i, endX - 1, y + i, vertical);
      }

      for (int i = 0; i < shadowBlendSize; i++) {
        g.setColor(getShadowBlendColor(i, backgroundColor));
        int d = shadowSize - shadowBlendSize + i;
        drawLine(g, startX + (cornerStart ? d + (vertical ? 1 : 0) : shadowSize + shadowBlendSize), y + d, endX - 1, y + d, vertical);
      }
    }

    private void drawShadowLine(Graphics g, int startX, int endX, int y, boolean vertical, Color backgroundColor) {
      if (startX >= endX)
        return;

      g.setColor(getShadowColor(backgroundColor));

      for (int i = 0; i < shadowSize - shadowBlendSize; i++) {
        drawLine(g, startX, y + i, endX - 1, y + i, vertical);
      }

      for (int i = 0; i < shadowBlendSize; i++) {
        g.setColor(getShadowBlendColor(i, backgroundColor));
        int d = shadowSize - shadowBlendSize + i;
        drawLine(g, startX, y + d, endX - 1, y + d, vertical);
      }
    }

    private void drawLine(Graphics g, int x1, int y1, int x2, int y2, boolean flip) {
      if (flip)
        g.drawLine(y1, x1, y2, x2);
      else
        g.drawLine(x1, y1, x2, y2);
    }

    private Color getShadowBlendColor(int offset, Color backgroundColor) {
      return backgroundColor == null ?
          new Color(shadowColor.getRed(),
                    shadowColor.getGreen(),
                    shadowColor.getBlue(),
                    (int) (255F * shadowStrength * (shadowBlendSize - offset) / shadowBlendSize)) :
          ColorUtil.blend(backgroundColor,
                          shadowColor,
                          shadowStrength * (shadowBlendSize - offset) / shadowBlendSize);
    }

    private Color getShadowColor(Color backgroundColor) {
      return getShadowBlendColor(0, backgroundColor);
    }
  }
}