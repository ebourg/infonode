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


// $Id: TabbedPanel.java,v 1.98 2004/11/11 14:10:33 jesper Exp $
package net.infonode.tabbedpanel;

import net.infonode.gui.ComponentUtil;
import net.infonode.gui.DimensionUtil;
import net.infonode.gui.ScrollButtonBox;
import net.infonode.gui.componentpainter.ComponentPainter;
import net.infonode.gui.componentpainter.SolidColorComponentPainter;
import net.infonode.gui.draggable.*;
import net.infonode.gui.layout.DirectionLayout;
import net.infonode.gui.shaped.panel.ShapedPanel;
import net.infonode.properties.gui.util.ShapedPanelProperties;
import net.infonode.properties.propertymap.PropertyMapTreeListener;
import net.infonode.properties.propertymap.PropertyMapWeakListenerManager;
import net.infonode.tabbedpanel.internal.ShadowPainter;
import net.infonode.tabbedpanel.internal.TabDropDownList;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.util.Direction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * <p>
 * A TabbedPanel is a component that handles a group of components in a notebook
 * like manor. Each component is represented by a {@link Tab}. A tab is a
 * component itself that defines how the tab will be rendered. The tab also
 * holds a reference to the content component associated with the tab. The
 * tabbed panel is divided into two areas, the tab area where the tabs are
 * displayed and the content area where the tab's content component is
 * displayed.
 * </p>
 *
 * <p>
 * The demo program for InfoNode Tabbed Panel on
 * <a href="http://www.infonode.net/index.html?itpdemo" target="_blank">
 * www.infonode.net</a> demonstrates and explains most of the tabbed panel's
 * features.
 * </p>
 *
 * <p>
 * The tabbed panel is configured using a {@link TabbedPanelProperties} object.
 * A tabbed panel will always have a properties object with default values based
 * on the current Look and Feel
 * </p>
 *
 * <p>
 * Tabs can be added, inserted, removed, selected, highlighted, dragged and
 * moved.
 * </p>
 *
 * <p>
 * The tabbed panel support tab placement in a horizontal line above or under
 * the content area or a vertical row to the left or to the right of the content
 * area. The tab line can be laid out as either scrolling or compression. If the
 * tabs are too many to fit in the tab area and scrolling is enabled, then the
 * mouse wheel is activated and scrollbuttons are shown so that the tabs can be
 * scrolled. Compression means that the tabs will be downsized to fit into the
 * visible tab area.
 * </p>
 *
 * <p>
 * It is possible to display a button in the tab area next to the tabs that shows
 * a drop down list (called tab drop down list) with all the tabs where it is
 * possible to select a tab. This is for example useful when the tabbed panel
 * contains a large amount of tabs or if some tabs are scrolled out. The drop down
 * list can show a text and an icon for a tab. The text is retrieved by calling
 * toString() on the tab and the icon is only retrieved if the tab implements the
 * {@link net.infonode.gui.icon.IconProvider} interface.
 * </p>
 *
 * <p>
 * It is possible to set an array of components (called tab area components) to
 * be shown next to the tabs in the tab area, the same place where the drop down
 * list and the scrollbuttons are shown. This for example useful for adding
 * buttons to the tabbed panel.
 * </p>
 *
 * <p>
 * It is possible to add a {@link TabListener} and receive events when a tab is
 * added, removed, selected, deselected, highlighted, dehighlighted, moved,
 * dragged, dropped or drag is aborted. The listener will receive events for all
 * the tabs in the tabbed panel. A tabbed panel will trigger selected,
 * deselected, highlighted and dehighlighted even if for example the selected
 * tab is null (no selected tab), i.e. null will be treated as if it was a tab.
 * </p>
 *
 * @author $Author: jesper $
 * @version $Revision: 1.98 $
 * @see Tab
 * @see TitledTab
 * @see TabbedPanelProperties
 * @see TabListener
 */
public class TabbedPanel extends JPanel {
  // Shadow property values
  private int shadowSize = 4;

  private TabDropDownList dropDownList;

  private JComponent contentPanel;

  private GridBagConstraints constraints = new GridBagConstraints();
  private GridBagLayout tabAreaLayoutManager = new GridBagLayout() {
    private int size;
    private int outerSize;

    public void layoutContainer(Container parent) {
      setScrollButtonsVisible();
      super.layoutContainer(parent);
      if (contentPanel != null) {
        int newSize = (tabAreaOrientation == Direction.UP || tabAreaOrientation == Direction.DOWN) ?
                      draggableComponentBox.getWidth() : draggableComponentBox.getHeight();
        int newOuterSize = (tabAreaOrientation == Direction.UP || tabAreaOrientation == Direction.DOWN) ?
                           tabAreaContainer.getWidth() : tabAreaContainer.getHeight();
        if (newOuterSize == outerSize && newSize != size) {
          size = newSize;
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              contentPanel.repaint();
            }
          });
        }
        outerSize = newOuterSize;
      }

      updateShadow();
    }
  };

  private ShapedPanel tabAreaContainer = new ShapedPanel(tabAreaLayoutManager) {
    public Dimension getPreferredSize() {
      if (getTabCount() == 0)
        return super.getPreferredSize();

      boolean vertical = tabAreaOrientation == Direction.RIGHT || tabAreaOrientation == Direction.LEFT;
      Dimension d = DimensionUtil.add(draggableComponentBox.getPreferredSize(), tabAreaContainer.getInsets());

      if (tabAreaComponentsPanel.isVisible() && !ComponentUtil.isOnlyVisibleComponent(scrollButtonBox)) {
        Dimension d2 = new Dimension(0, 0);
        Component c[] = tabAreaComponentsPanel.getComponents();
        for (int i = 0; i < c.length; i++) {
          if (c[i] != scrollButtonBox)
            d = DimensionUtil.add(d, c[i].getPreferredSize(), !vertical);
        }
        Dimension d3 = tabAreaComponentsPanel.getPreferredSize();
        d = new Dimension(vertical ? Math.max(d.width, d2.width) : d.width,
                          vertical ? d.height : Math.max(d.height, d3.height));
      }

      return d;
    }
  };

  private ShapedPanel tabAreaComponentsPanel = new ShapedPanel(new DirectionLayout()) {
    public Dimension getMaximumSize() {
      return getPreferredSize();
    }

    public Dimension getPreferredSize() {
      if (getTabCount() == 0)
        return super.getPreferredSize();

      Dimension d = super.getPreferredSize();
      boolean vertical = tabAreaOrientation == Direction.RIGHT || tabAreaOrientation == Direction.LEFT;
      Insets insets = getInsets();
      int size = vertical ?
                 ComponentUtil.getPreferredMaxWidth(getComponents()) + insets.left + insets.right :
                 (ComponentUtil.getPreferredMaxHeight(getComponents()) + insets.top + insets.bottom);

      return new Dimension(vertical ? size : (int) d.getWidth(), vertical ? (int) d.getHeight() : size);
    }

    public boolean isVisible() {
      return ComponentUtil.hasVisibleChildren(this);
    }
  };

  private JComponent[] tabAreaComponents;

  private Direction tabAreaOrientation;
  private TabDropDownListVisiblePolicy listVisiblePolicy = TabDropDownListVisiblePolicy.NEVER;
  private TabLayoutPolicy listTabLayoutPolicy = TabLayoutPolicy.SCROLLING;
  private DraggableComponentBox draggableComponentBox = new DraggableComponentBox(TabbedUIDefaults.getButtonIconSize());
  private ArrayList listeners;
  private ArrayList tabs = new ArrayList(4);
  private TabbedPanelProperties properties = new TabbedPanelProperties(TabbedPanelProperties.getDefaultProperties());
  private Tab highlightedTab;
  private boolean settingHighlighted;
  private ShadowPanel componentsPanel = new ShadowPanel();

  private ScrollButtonBox scrollButtonBox;

  private DraggableComponentBoxListener draggableComponentBoxListener = new DraggableComponentBoxListener() {
    private boolean selectedMoved;

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
        else if (type == DraggableComponentEvent.TYPE_DISABLED && highlightedTab != null && highlightedTab.getDraggableComponent() == event.getDraggableComponent())
          setHighlightedTab(null);
        else if (type == DraggableComponentEvent.TYPE_ENABLED && draggableComponentBox.getSelectedDraggableComponent() == event.getDraggableComponent())
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
   * Constructs a TabbedPanel with a TabbedPanelContentPanel as content area
   * component and with default TabbedPanelProperties
   *
   * @see TabbedPanelProperties
   * @see TabbedPanelContentPanel
   */
  public TabbedPanel() {
    initialize(new TabbedPanelContentPanel(this, new TabContentPanel(this)));
  }

  /**
   * <p>
   * Constructs a TabbedPanel with a custom component as content area
   * component or without any content area component and with default
   * TabbedPanelProperties.
   * </p>
   *
   * <p>
   * If no content area component is used, then the tabbed panel will act as a
   * bar and the tabs will be lai out in a line.
   * </p>
   *
   * <p>
   * <strong>Note: </strong> A custom content area component is by itself
   * responsible for showing a tab's content component when a tab is selected,
   * for eaxmple by listening to events from the tabbed panel. The component
   * will be laid out just as the default content area component so that
   * shadows etc. can be used.
   * </p>
   *
   * @param contentAreaComponent component to be used as content area component or null for no
   *                             content area component
   * @see TabbedPanelProperties
   */
  public TabbedPanel(JComponent contentAreaComponent) {
    initialize(contentAreaComponent);
  }

  /**
   * Check if the tab area contains the given point
   *
   * @param p the point to check. Must be relative to this tabbed panel.
   * @return true if tab area contains point, otherwise false
   * @see #contentAreaContainsPoint
   */
  public boolean tabAreaContainsPoint(Point p) {
    return tabAreaContainer.contains(SwingUtilities.convertPoint(this, p, tabAreaContainer));
  }

  /**
   * Check if the content area contains the given point
   *
   * @param p the point to check. Must be relative to this tabbed panel.
   * @return true if content area contains point, otherwise false
   * @see #tabAreaContainsPoint
   */
  public boolean contentAreaContainsPoint(Point p) {
    return contentPanel != null ? contentPanel.contains(SwingUtilities.convertPoint(this, p, contentPanel)) : false;
  }

  /**
   * <p>
   * Add a tab. The tab will be added after the last tab.
   * </p>
   *
   * <p>
   * If the tab to be added is the only tab in this tabbed panel and the
   * property "Auto Select Tab" is enabled then the tab will become selected
   * in this tabbed panel after the tab has been added.
   * </p>
   *
   * @param tab tab to be added
   * @see #insertTab(Tab, int)
   * @see TabbedPanelProperties
   */
  public void addTab(Tab tab) {
    doInsertTab(tab, null, -1);
  }

  /**
   * <p>
   * Insert a tab at the specified tab index (position).
   * </p>
   *
   * <p>
   * If the tab to be inserted is the only tab in this tabbed panel and the
   * property "Auto Select Tab" is enabled then the tab will become selected
   * in this tabbed panel after the tab has been inserted.
   * </p>
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
   * <p>
   * Insert a tab at the specified point.
   * </p>
   *
   * <p>
   * If the point is outside the tab area then the tab will be inserted after
   * the last tab.
   * </p>
   *
   * <p>
   * If the tab to be inserted is the only tab in this tabbed panel and the
   * property "Auto Select Tab" is enabled then the tab will become selected
   * in this tabbed panel after the tab has been inserted.
   * </p>
   *
   * @param tab tab to be inserted
   * @param p   the point to insert tab at. Must be relative to this tabbed
   *            panel.
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
    checkOnlyOneTab(false);
  }

  /**
   * Move tab to point p. If p is outside the tab area then the tab is not
   * moved.
   *
   * @param tab tab to move. Tab must be a member (added/inserted) of this
   *            tabbed panel.
   * @param p   point to move tab to. Must be relative to this tabbed panel.
   */
  public void moveTab(Tab tab, Point p) {
    if (tabs.contains(tab))
      draggableComponentBox.dragDraggableComponent(tab.getDraggableComponent(),
                                                   SwingUtilities.convertPoint(this, p, draggableComponentBox));
  }

  /**
   * Selects a tab, i.e. displays the tab's content component in this tabbed
   * panel's content area
   *
   * @param tab tab to select. Tab must be a member (added/inserted) of this
   *            tabbed panel.
   */
  public void setSelectedTab(Tab tab) {
    if (getSelectedTab() == tab)
      return;

    if (tab != null) {
      if (tab.isEnabled() && getTabIndex(tab) > -1) {
        if (tab.getDraggableComponent() == draggableComponentBox.getSelectedDraggableComponent()) {
          setHighlightedTab(tab);
        }
        else {
          tab.setSelected(true);
        }
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
   * @return the selected tab or null if no tab is selected in this tabbed
   *         panel
   */
  public Tab getSelectedTab() {
    return findTab(draggableComponentBox.getSelectedDraggableComponent());
  }

  /**
   * Sets which tab that should be highlighted, i.e. signal highlighted state
   * to the tab
   *
   * @param highlightedTab tab that should be highlighted or null if no tab should be
   *                       highlighted. The tab must be a member (added/inserted) of this
   *                       tabbed panel.
   */
  public void setHighlightedTab(Tab highlightedTab) {
    if (!settingHighlighted) {
      settingHighlighted = true;
      Tab oldTab = this.highlightedTab;
      Tab newTab = null;
      if (oldTab != highlightedTab)
        draggableComponentBox.setTopComponent(highlightedTab != null ? highlightedTab.getDraggableComponent() : null);
      if (highlightedTab != null) {
        if (getTabIndex(highlightedTab) > -1) {
          this.highlightedTab = highlightedTab;
          if (oldTab != null && oldTab != highlightedTab) {
            oldTab.setHighlighted(false);
          }

          if (oldTab != highlightedTab)
            if (highlightedTab.isEnabled()) {
              highlightedTab.setHighlighted(true);
            }
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
   * @return the highlighted tab or null if no tab is highlighted in this
   *         tabbed panel
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
   * @param index index of tab
   * @return tab at index
   * @throws ArrayIndexOutOfBoundsException if there is no tab at index
   */
  public Tab getTabAt(int index) {
    return findTab(draggableComponentBox.getDraggableComponentAt(index));
  }

  /**
   * Gets the index for tab
   *
   * @param tab tab
   * @return index or -1 if tab is not a member of this TabbedPanel
   */
  public int getTabIndex(Tab tab) {
    return tab == null ? -1 : draggableComponentBox.getDraggableComponentIndex(tab.getDraggableComponent());
  }

  /**
   * Sets an array of components that will be shown in the tab area next to
   * the tabs, i.e. to the right or below the tabs depending on the tab area
   * orientation.
   *
   * The components will be laid out in a line and the direction
   * will change depending on the tab area orientation. Tab drop down list and
   * scroll buttons are also tab area components but those are handled
   * automatically by the tabbed panel and are not affected by calling this
   * method.
   *
   * @param tabAreaComponents array of components, null for no components
   * @since ITP 1.1.0
   */
  public void setTabAreaComponents(JComponent[] tabAreaComponents) {
    if (this.tabAreaComponents != null) {
      for (int i = 0; i < this.tabAreaComponents.length; i++)
        tabAreaComponentsPanel.remove(this.tabAreaComponents[i]);
    }

    this.tabAreaComponents = tabAreaComponents == null ? null : (JComponent[]) tabAreaComponents.clone();

    if (tabAreaComponents != null)
      for (int i = 0; i < tabAreaComponents.length; i++)
        tabAreaComponentsPanel.add(tabAreaComponents[i]);

    revalidate();
  }

  /**
   * Gets if any tab area components i.e. scroll buttons etc are visible at the moment
   *
   * @return true if visible, otherwise false
   * @since ITP 1.2.0
   */
  public boolean isTabAreaComponentsVisible() {
    return tabAreaComponentsPanel.isVisible();
  }

  /**
   * Gets the tab area components.
   *
   * Tab drop down list and scroll buttons are also tab area components but
   * those are handled automatically by the tabbed panel and no references
   * to them will be returned. This method only returns the components that
   * have been set with the setTabAreaComponents method.
   *
   * @return an array of tab area components or null if none
   * @see #setTabAreaComponents
   * @since ITP 1.1.0
   */
  public JComponent[] getTabAreaComponents() {
    return tabAreaComponents == null ? null : (JComponent[]) tabAreaComponents.clone();
  }

  /**
   * Adds a TablListener that will receive events for all the tabs in this
   * TabbedPanel
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
    tabAreaContainer.setOpaque(false);
    tabAreaComponentsPanel.setOpaque(false);

    draggableComponentBox.setOuterParentArea(tabAreaContainer);
    tabAreaContainer.add(draggableComponentBox);
    tabAreaContainer.add(tabAreaComponentsPanel);

    this.contentPanel = contentPanel;
    draggableComponentBox.addListener(draggableComponentBoxListener);

    if (contentPanel != null) {
      componentsPanel.add(contentPanel, BorderLayout.CENTER);
    }

    add(componentsPanel, BorderLayout.CENTER);

    PropertyMapWeakListenerManager.addWeakTreeListener(properties.getMap(), propertyChangedListener);
    updateProperties();
  }

  private void updateProperties() {
    componentsPanel.remove(draggableComponentBox);
    tabAreaOrientation = properties.getTabAreaOrientation();
    updateTabArea();
    updateAllTabsProperties();

    componentsPanel.add(tabAreaContainer, ComponentUtil.getBorderLayoutOrientation(tabAreaOrientation));

    // Shadow
    shadowSize = properties.getShadowSize();
    componentsPanel.setBorder(
        contentPanel != null && properties.getShadowEnabled() ? new EmptyBorder(0, 0, shadowSize, shadowSize) : null);

    checkOnlyOneTab(true);

    updateScrollButtons();
    updateTabDropDownList();

    repaint();
    revalidate();
  }

  private void updateTabDropDownList() {
    TabDropDownListVisiblePolicy newListVisiblePolicy = getProperties().getTabDropDownListVisiblePolicy();
    TabLayoutPolicy newListTabLayoutPolicy = getProperties().getTabLayoutPolicy();

    if (newListVisiblePolicy != listVisiblePolicy || newListTabLayoutPolicy != listTabLayoutPolicy) {
      if (dropDownList != null) {
        tabAreaComponentsPanel.remove(dropDownList);
        dropDownList.dispose();
        dropDownList = null;
      }

      if (newListVisiblePolicy == TabDropDownListVisiblePolicy.MORE_THAN_ONE_TAB ||
          (newListVisiblePolicy == TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE && newListTabLayoutPolicy == TabLayoutPolicy.SCROLLING)) {
        dropDownList = new TabDropDownList(this);
        tabAreaComponentsPanel.add(dropDownList, scrollButtonBox == null ? 0 : 1);

        if (newListVisiblePolicy == TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE)
          dropDownList.setVisible(false);
      }
    }

    listVisiblePolicy = newListVisiblePolicy;
    listTabLayoutPolicy = newListTabLayoutPolicy;

    if (dropDownList != null && !draggableComponentBox.isScrollEnabled() && listVisiblePolicy == TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE)
      dropDownList.setVisible(false);
  }

  private void updateAllTabsProperties() {
    for (int i = 0; i < tabs.size(); i++)
      updateTabProperties((Tab) tabs.get(i));
  }

  private void updateTabProperties(Tab tab) {
    tab.getDraggableComponent().setAbortDragKeyCode(properties.getAbortDragKey());
    tab.getDraggableComponent().setReorderEnabled(properties.getTabReorderEnabled());
    tab.getDraggableComponent().setSelectOnMousePress(properties.getTabSelectTrigger() == TabSelectTrigger.MOUSE_PRESS);
  }

  private void updateTabArea() {
    boolean stretch = properties.getTabAreaComponentsProperties().getStretchEnabled();
    if (tabAreaOrientation == Direction.UP) {
      setTabAreaLayoutConstraints(draggableComponentBox,
                                  0,
                                  0,
                                  GridBagConstraints.HORIZONTAL,
                                  1,
                                  1,
                                  GridBagConstraints.SOUTH);
      setTabAreaLayoutConstraints(tabAreaComponentsPanel,
                                  1,
                                  0,
                                  stretch ? GridBagConstraints.VERTICAL : GridBagConstraints.NONE,
                                  0,
                                  1,
                                  GridBagConstraints.SOUTH);
      updateTabAreaComponentsPanel(Direction.RIGHT, 0, 1);
    }
    else if (tabAreaOrientation == Direction.DOWN) {
      setTabAreaLayoutConstraints(draggableComponentBox,
                                  0,
                                  0,
                                  GridBagConstraints.HORIZONTAL,
                                  1,
                                  1,
                                  GridBagConstraints.NORTH);
      setTabAreaLayoutConstraints(tabAreaComponentsPanel,
                                  1,
                                  0,
                                  stretch ? GridBagConstraints.VERTICAL : GridBagConstraints.NONE,
                                  0,
                                  0,
                                  GridBagConstraints.NORTH);
      updateTabAreaComponentsPanel(Direction.RIGHT, 0, 0);
    }
    else if (tabAreaOrientation == Direction.LEFT) {
      setTabAreaLayoutConstraints(draggableComponentBox,
                                  0,
                                  0,
                                  GridBagConstraints.VERTICAL,
                                  1,
                                  1,
                                  GridBagConstraints.EAST);
      setTabAreaLayoutConstraints(tabAreaComponentsPanel,
                                  0,
                                  1,
                                  stretch ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE,
                                  0,
                                  0,
                                  GridBagConstraints.EAST);
      updateTabAreaComponentsPanel(Direction.DOWN, 0, 0);
    }
    else {
      setTabAreaLayoutConstraints(draggableComponentBox,
                                  0,
                                  0,
                                  GridBagConstraints.VERTICAL,
                                  1,
                                  1,
                                  GridBagConstraints.WEST);
      setTabAreaLayoutConstraints(tabAreaComponentsPanel,
                                  0,
                                  1,
                                  stretch ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE,
                                  0,
                                  0,
                                  GridBagConstraints.WEST);
      updateTabAreaComponentsPanel(Direction.DOWN, 0, 1);
    }

    if (getTabCount() > 1)
      draggableComponentBox.setScrollEnabled(properties.getTabLayoutPolicy() == TabLayoutPolicy.SCROLLING);
    draggableComponentBox.setScrollOffset(properties.getTabScrollingOffset());
    draggableComponentBox.setComponentDirection(tabAreaOrientation);
    draggableComponentBox.setEnsureSelectedVisible(properties.getEnsureSelectedTabVisible());
    draggableComponentBox.setAutoSelect(properties.getAutoSelectTab());

    draggableComponentBox.setComponentSpacing(properties.getTabSpacing());
    draggableComponentBox.setDepthSortOrder(properties.getTabDepthOrderPolicy() == TabDepthOrderPolicy.DESCENDING);

    properties.getTabAreaProperties().getComponentProperties().applyTo(tabAreaContainer);
    updateShapedPanelProperties(tabAreaContainer,
                                properties.getTabAreaProperties().getComponentProperties().getBackgroundColor(),
                                properties.getTabAreaProperties().getShapedPanelProperties());

    properties.getTabAreaComponentsProperties().getComponentProperties().applyTo(tabAreaComponentsPanel,
                                                                                 tabAreaOrientation.getNextCW());
    updateShapedPanelProperties(tabAreaComponentsPanel,
                                properties.getTabAreaComponentsProperties().getComponentProperties()
                                .getBackgroundColor(),
                                properties.getTabAreaComponentsProperties().getShapedPanelProperties());
  }

  private void updateTabAreaComponentsPanel(Direction direction, int alignmentX, int alignmentY) {
    ((DirectionLayout) tabAreaComponentsPanel.getLayout()).setDirection(direction);
  }

  private void updateShapedPanelProperties(ShapedPanel panel, Color backgroundColor, ShapedPanelProperties shapedPanelProperties) {
    panel.setOpaque(false);
    ComponentPainter painter = shapedPanelProperties.getComponentPainter();
    if (painter != null)
      panel.setComponentPainter(painter);
    else if (backgroundColor != null)
      panel.setComponentPainter(SolidColorComponentPainter.BACKGROUND_COLOR_PAINTER);
    else
      panel.setComponentPainter(null);
    panel.setVerticalFlip(shapedPanelProperties.getVerticalFlip());
    panel.setHorizontalFlip(shapedPanelProperties.getHorizontalFlip());
    panel.setDirection(getProperties().getTabAreaOrientation().getNextCW());
    panel.setClipChildren(shapedPanelProperties.getClipChildren());
    //panel.setDirection(shapedPanelProperties.getDirection() == null ? getProperties().getTabAreaOrientation().getNextCW() : shapedPanelProperties.getDirection());
  }

  private void setTabAreaLayoutConstraints(JComponent c, int gridx, int gridy, int fill, double weightx, double weighty, int anchor) {
    constraints.gridx = gridx;
    constraints.gridy = gridy;
    constraints.fill = fill;
    constraints.weightx = weightx;
    constraints.weighty = weighty;
    constraints.anchor = anchor;

    tabAreaLayoutManager.setConstraints(c, constraints);
  }

  private void doInsertTab(Tab tab, Point p, int index) {
    if (tab != null && !tabs.contains(tab)) {
      tab.setTabbedPanel(this);
      tabs.add(tab);
      if (p != null)
        draggableComponentBox.insertDraggableComponent(tab.getDraggableComponent(),
                                                       SwingUtilities.convertPoint(this, p, draggableComponentBox));
      else
        draggableComponentBox.insertDraggableComponent(tab.getDraggableComponent(), index);
      updateTabProperties(tab);
      checkOnlyOneTab(true);
    }
  }

  private Tab findTab(DraggableComponent draggableComponent) {
    for (int i = 0; i < tabs.size(); i++)
      if (((Tab) tabs.get(i)).getDraggableComponent() == draggableComponent)
        return (Tab) tabs.get(i);

    return null;
  }

  private void checkOnlyOneTab(boolean inc) {
    if (getTabCount() == 1) {
      draggableComponentBox.setScrollEnabled(false);
      updateScrollButtons();
    }
    else if (inc && getTabCount() == 2) {
      draggableComponentBox.setScrollEnabled(properties.getTabLayoutPolicy() == TabLayoutPolicy.SCROLLING);
      updateScrollButtons();
    }
  }

  private void setScrollButtonsVisible() {
    if (scrollButtonBox != null) {
      boolean visible;
      if (!tabAreaOrientation.isHorizontal())
        visible = draggableComponentBox.getInnerSize().getWidth() > calcScrollWidth();
      else
        visible = draggableComponentBox.getInnerSize().getHeight() > calcScrollHeight();
      scrollButtonBox.setVisible(visible);

      if (listVisiblePolicy == TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE)
        dropDownList.setVisible(visible);

      if (!visible) {
        scrollButtonBox.setButton1Enabled(false);
        scrollButtonBox.setButton2Enabled(true);
      }
    }
  }

  private int calcScrollWidth() {
    Insets componentsPanelInsets = tabAreaComponentsPanel.getInsets();
    boolean includeDropDownWidth = listVisiblePolicy == TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE;
    boolean componentsVisible = includeDropDownWidth
                                ? ComponentUtil.isOnlyVisibleComponents(new Component[]{scrollButtonBox, dropDownList})
                                : ComponentUtil.isOnlyVisibleComponent(scrollButtonBox);
    int insetsWidth = tabAreaComponentsPanel.isVisible() && componentsVisible ?
                      componentsPanelInsets.left + componentsPanelInsets.right : 0;
    int componentsPanelWidth = tabAreaComponentsPanel.isVisible() ?
                               ((int) tabAreaComponentsPanel.getPreferredSize().getWidth() - insetsWidth - (scrollButtonBox.isVisible()
                                                                                                            ?
                                                                                                            scrollButtonBox.getWidth() + (includeDropDownWidth ?
                                                                                                                                          dropDownList.getWidth() :
                                                                                                                                          0)
                                                                                                            :
                                                                                                            0)) :
                               0;
    Insets areaInsets = tabAreaContainer.getInsets();
    return tabAreaContainer.getWidth() - componentsPanelWidth - areaInsets.left - areaInsets.right;
  }

  private int calcScrollHeight() {
    Insets componentsPanelInsets = tabAreaComponentsPanel.getInsets();
    boolean includeDropDownHeight = listVisiblePolicy == TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE;
    boolean componentsVisible = includeDropDownHeight
                                ? ComponentUtil.isOnlyVisibleComponents(new Component[]{scrollButtonBox, dropDownList})
                                : ComponentUtil.isOnlyVisibleComponent(scrollButtonBox);
    int insetsHeight = tabAreaComponentsPanel.isVisible() && componentsVisible ?
                       componentsPanelInsets.top + componentsPanelInsets.bottom : 0;
    int componentsPanelHeight = tabAreaComponentsPanel.isVisible() ?
                                ((int) tabAreaComponentsPanel.getPreferredSize().getHeight() - insetsHeight - (scrollButtonBox.isVisible()
                                                                                                               ?
                                                                                                               scrollButtonBox.getHeight() + (includeDropDownHeight ?
                                                                                                                                              dropDownList.getHeight() :
                                                                                                                                              0)
                                                                                                               :
                                                                                                               0)) :
                                0;
    Insets areaInsets = tabAreaContainer.getInsets();
    return tabAreaContainer.getHeight() - componentsPanelHeight - areaInsets.top - areaInsets.bottom;
  }

  private void updateScrollButtons() {
    ScrollButtonBox oldScrollButtonBox = scrollButtonBox;
    scrollButtonBox = draggableComponentBox.getScrollButtonBox();
    if (oldScrollButtonBox != scrollButtonBox) {
      if (oldScrollButtonBox != null)
        tabAreaComponentsPanel.remove(oldScrollButtonBox);

      if (scrollButtonBox != null) {
        scrollButtonBox.setVisible(false);
        tabAreaComponentsPanel.add(scrollButtonBox, 0);
      }
    }
  }

  private void fireTabMoved(Tab tab) {
    if (listeners != null) {
      TabEvent event = new TabEvent(this, tab);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabMoved(event);
    }
  }

  private void fireDraggedEvent(Tab tab, Point p) {
    if (listeners != null) {
      TabDragEvent event = new TabDragEvent(this, tab, SwingUtilities.convertPoint(draggableComponentBox, p, tab));
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabDragged(event);
    }
  }

  private void fireDroppedEvent(Tab tab, Point p) {
    if (listeners != null) {
      TabDragEvent event = new TabDragEvent(this, tab, SwingUtilities.convertPoint(draggableComponentBox, p, tab));
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabDropped(event);
    }
  }

  private void fireNotDroppedEvent(Tab tab) {
    if (listeners != null) {
      TabEvent event = new TabEvent(this, tab);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabDragAborted(event);
    }
  }

  private void fireSelectedEvent(Tab tab, Tab oldTab) {
    if (listeners != null) {
      {
        TabStateChangedEvent event = new TabStateChangedEvent(this, this, oldTab, oldTab, tab);
        Object[] l = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((TabListener) l[i]).tabDeselected(event);
      }
      {
        TabStateChangedEvent event = new TabStateChangedEvent(this, this, tab, oldTab, tab);
        Object[] l = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((TabListener) l[i]).tabSelected(event);
      }
    }
  }

  private void fireHighlightedEvent(Tab tab, Tab oldTab) {
    if (listeners != null) {
      {
        TabStateChangedEvent event = new TabStateChangedEvent(this, this, oldTab, oldTab, tab);
        Object[] l = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((TabListener) l[i]).tabDehighlighted(event);
      }
      {
        TabStateChangedEvent event = new TabStateChangedEvent(this, this, tab, oldTab, tab);
        Object[] l = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((TabListener) l[i]).tabHighlighted(event);
      }
    }
  }

  private void fireAddedEvent(Tab tab) {
    if (listeners != null) {
      TabEvent event = new TabEvent(this, tab);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabAdded(event);
    }
  }

  private void fireRemovedEvent(Tab tab) {
    if (listeners != null) {
      TabRemovedEvent event = new TabRemovedEvent(this, tab, this);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((TabListener) l[i]).tabRemoved(event);
    }
  }

  private void updateShadow() {
    if (contentPanel != null && properties.getShadowEnabled()) {
      Point p = SwingUtilities.convertPoint(tabAreaContainer, new Point(0, 0), this);
      repaint(p.x, p.y, tabAreaContainer.getWidth() + shadowSize, tabAreaContainer.getHeight() + shadowSize);
    }
  }

  private class ShadowPanel extends JPanel {
    ShadowPanel() {
      super(new BorderLayout());
      setOpaque(false);
      setCursor(null);
    }

    public void paint(Graphics g) {
      super.paint(g);

      if (contentPanel == null || !properties.getShadowEnabled())
        return;

      new ShadowPainter(this,
                        componentsPanel,
                        getHighlightedTab(),
                        contentPanel,
                        tabAreaComponentsPanel,
                        tabAreaContainer,
                        draggableComponentBox,
                        getProperties().getTabAreaOrientation(),
                        getProperties().getPaintTabAreaShadow(),
                        shadowSize,
                        getProperties().getShadowBlendAreaSize(),
                        getProperties().getShadowColor(),
                        getProperties().getShadowStrength(),
                        getTabIndex(getHighlightedTab()) == getTabCount() - 1).paint(g);
    }
  }
}