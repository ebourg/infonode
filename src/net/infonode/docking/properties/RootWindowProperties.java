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


// $Id: RootWindowProperties.java,v 1.45 2004/11/11 14:09:46 jesper Exp $
package net.infonode.docking.properties;

import net.infonode.docking.DefaultButtonFactories;
import net.infonode.gui.DynamicUIManager;
import net.infonode.gui.DynamicUIManagerListener;
import net.infonode.gui.icon.button.CloseIcon;
import net.infonode.gui.icon.button.MaximizeIcon;
import net.infonode.gui.icon.button.MinimizeIcon;
import net.infonode.gui.icon.button.RestoreIcon;
import net.infonode.properties.gui.util.ComponentProperties;
import net.infonode.properties.gui.util.ShapedPanelProperties;
import net.infonode.properties.propertymap.*;
import net.infonode.properties.types.BooleanProperty;
import net.infonode.properties.types.IntegerProperty;
import net.infonode.tabbedpanel.TabDropDownListVisiblePolicy;
import net.infonode.tabbedpanel.TabSelectTrigger;
import net.infonode.tabbedpanel.TabbedPanelProperties;
import net.infonode.tabbedpanel.TabbedUIDefaults;
import net.infonode.tabbedpanel.border.TabAreaLineBorder;
import net.infonode.tabbedpanel.titledtab.TitledTabProperties;
import net.infonode.tabbedpanel.titledtab.TitledTabSizePolicy;
import net.infonode.util.ColorUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

/**
 * Properties and property values for a root window.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.45 $
 */
public class RootWindowProperties extends PropertyMapContainer {
  /**
   * The size of the default window tab button icons.
   */
  public static final int DEFAULT_WINDOW_TAB_BUTTON_ICON_SIZE = 10;

  /**
   * Property group containing all root window properties.
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Root Window Properties", "");

  /**
   * The root window component property values.
   */
  public static final PropertyMapProperty COMPONENT_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Component Properties",
                              "The root window component property values.",
                              ComponentProperties.PROPERTIES);

  /**
   * The root window shaped panel property values.
   */
  public static final PropertyMapProperty SHAPED_PANEL_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Shaped Panel Properties",
                              "The root window shaped panel property values.",
                              ShapedPanelProperties.PROPERTIES);

  /**
   * The window area component property values. The window area is the area inside the WindowBar's.
   */
  public static final PropertyMapProperty WINDOW_AREA_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Window Area Properties",
                              "The window area component property values. The window area is the area inside the WindowBar's.",
                              ComponentProperties.PROPERTIES);

  /**
   * The window area shaped panel property values. The window area is the area inside the WindowBar's.
   */
  public static final PropertyMapProperty WINDOW_AREA_SHAPED_PANEL_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Window Area Shaped Panel Properties",
                              "The window area shaped panel property values. The window area is the area inside the WindowBar's.",
                              ShapedPanelProperties.PROPERTIES);

  /**
   * Shaped panel properties for the drag rectangle. Setting a painter disables the default drag rectangle.
   *
   * @since IDW 1.2.0
   */
  public static final PropertyMapProperty DRAG_RECTANGLE_SHAPED_PANEL_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Drag Rectangle Shaped Panel Properties",
                              "Shaped panel properties for the drag rectangle. Setting a painter disables the default drag rectangle.",
                              ShapedPanelProperties.PROPERTIES);

  /**
   * The width of the drag rectangle border.
   */
  public static final IntegerProperty DRAG_RECTANGLE_BORDER_WIDTH =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Drag Rectangle Border Width",
                                     "The width of the drag rectangle border. The drag rectangle will only " +
                                     "be painted if the painter of the '" + DRAG_RECTANGLE_SHAPED_PANEL_PROPERTIES.getName() +
                                     "' property is not set.",
                                     2,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * The window drag label property values.
   */
  public static final PropertyMapProperty DRAG_LABEL_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Drag Label Properties",
                              "The window drag label property values.",
                              ComponentProperties.PROPERTIES);

  /**
   * Default property values for DockingWindow's inside this root window.
   */
  public static final PropertyMapProperty DOCKING_WINDOW_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Docking Window Properties",
                              "Default property values for DockingWindow's inside this RootWindow.",
                              DockingWindowProperties.PROPERTIES);

  /**
   * Default property values for tab windows inside this root window.
   */
  public static final PropertyMapProperty TAB_WINDOW_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Tab Window Properties",
                              "Default property values for TabWindow's inside this RootWindow.",
                              TabWindowProperties.PROPERTIES);

  /**
   * Default property values for split windows inside this root window.
   */
  public static final PropertyMapProperty SPLIT_WINDOW_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Split Window Properties",
                              "Default property values for SplitWindow's inside this RootWindow.",
                              SplitWindowProperties.PROPERTIES);

  /**
   * Default property values for views inside this root window.
   */
  public static final PropertyMapProperty VIEW_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "View Properties",
                              "Default property values for View's inside this RootWindow.",
                              ViewProperties.PROPERTIES);

  /**
   * Double clicking on a minimized window in a window bar restores it.
   */
  public static final BooleanProperty DOUBLE_CLICK_RESTORES_WINDOW =
      new BooleanProperty(PROPERTIES,
                          "Double Click Restores Window",
                          "Double clicking on a minimized window in a window bar restores it.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * If true, makes it possible for the user to create tab windows inside other tab windows when dragging windows.
   * If false, only one level of tab windows is allowed.
   * Changing the value of this property does not alter the window tree.
   */
  public static final BooleanProperty RECURSIVE_TABS_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Recursive Tabs Enabled",
                          "If true, makes it possible for the user to create tab windows inside other tab windows by " +
                          "dragging windows. If false, only one level of tab windows is allowed. Changing the value of " +
                          "this property does not alter the window tree.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Inside this distance from the window edge a mouse drag will trigger a window split.
   */
  public static final IntegerProperty EDGE_SPLIT_DISTANCE =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Edge Split Distance",
                                     "Inside this distance from the window edge a mouse drag will trigger a window split.",
                                     3,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * Key code for the key that aborts a drag.
   */
  public static final IntegerProperty ABORT_DRAG_KEY =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Abort Drag Key Code",
                                     "Key code for the key that aborts a drag.",
                                     3,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * The default window bar property values.
   *
   * @since IDW 1.1.0
   */
  public static final PropertyMapProperty WINDOW_BAR_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Window Bar Properties",
                              "Default property values for WindowBar's inside this RootWindow.",
                              WindowBarProperties.PROPERTIES);

  private static RootWindowProperties DEFAULT_VALUES;

  private static void setTabProperties() {
    WindowTabProperties tabProperties = DEFAULT_VALUES.getTabWindowProperties().getTabProperties();

    tabProperties.getTitledTabProperties().getNormalProperties()
        .setToolTipEnabled(true)
        .getComponentProperties().setInsets(new Insets(0, 3, 0, 2));

    tabProperties.getTitledTabProperties().setSizePolicy(TitledTabSizePolicy.INDIVIDUAL_SIZE);

    tabProperties.getNormalButtonProperties().getCloseButtonProperties()
        .setFactory(DefaultButtonFactories.getCloseButtonFactory())
        .setVisible(false)
        .setToolTipText("Close")
        .setIcon(new CloseIcon(DEFAULT_WINDOW_TAB_BUTTON_ICON_SIZE));

    tabProperties.getNormalButtonProperties().getRestoreButtonProperties()
        .setFactory(DefaultButtonFactories.getRestoreButtonFactory())
        .setVisible(false)
        .setToolTipText("Restore")
        .setIcon(new RestoreIcon(DEFAULT_WINDOW_TAB_BUTTON_ICON_SIZE));

    tabProperties.getNormalButtonProperties().getMinimizeButtonProperties()
        .setFactory(DefaultButtonFactories.getMinimizeButtonFactory())
        .setVisible(false)
        .setToolTipText("Minimize")
        .setIcon(new MinimizeIcon(DEFAULT_WINDOW_TAB_BUTTON_ICON_SIZE));

    tabProperties.getTitledTabProperties().setFocusable(false);
    tabProperties.getHighlightedButtonProperties().getCloseButtonProperties().setVisible(true);
    tabProperties.getHighlightedButtonProperties().getMinimizeButtonProperties().setVisible(true);
    tabProperties.getHighlightedButtonProperties().getRestoreButtonProperties().setVisible(true);
  }

  private static void setTabbedPanelProperties() {
    TabWindowProperties tabWindowProperties = DEFAULT_VALUES.getTabWindowProperties();

    tabWindowProperties.getTabbedPanelProperties()
        .setTabDropDownListVisiblePolicy(TabDropDownListVisiblePolicy.TABS_NOT_VISIBLE)
        .setTabSelectTrigger(TabSelectTrigger.MOUSE_RELEASE)
        .setEnsureSelectedTabVisible(true)
        .setTabReorderEnabled(false)
        .setHighlightPressedTab(false)
        .setShadowEnabled(true);

    tabWindowProperties.getTabbedPanelProperties().getTabAreaComponentsProperties().getComponentProperties()
        .setInsets(new Insets(1, 3, 1, 3));

    tabWindowProperties.getCloseButtonProperties()
        .setFactory(DefaultButtonFactories.getCloseButtonFactory())
        .setVisible(true)
        .setToolTipText("Close")
        .setIcon(new CloseIcon(DEFAULT_WINDOW_TAB_BUTTON_ICON_SIZE));

    tabWindowProperties.getRestoreButtonProperties()
        .setFactory(DefaultButtonFactories.getRestoreButtonFactory())
        .setVisible(true)
        .setToolTipText("Restore")
        .setIcon(new RestoreIcon(DEFAULT_WINDOW_TAB_BUTTON_ICON_SIZE));

    tabWindowProperties.getMinimizeButtonProperties()
        .setFactory(DefaultButtonFactories.getMinimizeButtonFactory())
        .setVisible(true)
        .setToolTipText("Minimize")
        .setIcon(new MinimizeIcon(DEFAULT_WINDOW_TAB_BUTTON_ICON_SIZE));

    tabWindowProperties.getMaximizeButtonProperties()
        .setFactory(DefaultButtonFactories.getMaximizeButtonFactory())
        .setVisible(true)
        .setToolTipText("Maximize")
        .setIcon(new MaximizeIcon(DEFAULT_WINDOW_TAB_BUTTON_ICON_SIZE));
  }

  private static void setWindowBarProperties() {
    {
      WindowBarProperties p = DEFAULT_VALUES.getWindowBarProperties();

      p.setMinimumWidth(4);
      p.setContentPanelEdgeResizeEdgeDistance(6);

      p.getTabWindowProperties().getTabbedPanelProperties()
          .setTabDeselectable(true)
          .setAutoSelectTab(false)

          .getTabAreaComponentsProperties()
          .setStretchEnabled(true)

          .getComponentProperties()
          .setBorder(new TabAreaLineBorder());

      p.getTabWindowProperties().getTabbedPanelProperties().getContentPanelProperties().getComponentProperties()
          .setInsets(new Insets(4, 4, 4, 4));
    }

    {
      WindowTabProperties p = DEFAULT_VALUES.getWindowBarProperties().getTabWindowProperties().getTabProperties();

      p.getTitledTabProperties()
          .setSizePolicy(TitledTabSizePolicy.EQUAL_SIZE)
//          .addSuperObject(HighlightedTabSetup.createTabProperties())
          .setHighlightedRaised(0);

/*      p.getFocusedProperties()
          .setBackgroundColor(Color.YELLOW);

  */
      p.getTitledTabProperties().getNormalProperties()
          .getComponentProperties().setInsets(new Insets(1, 4, 1, 4));

      p.getNormalButtonProperties().getCloseButtonProperties().setVisible(true);
      p.getNormalButtonProperties().getRestoreButtonProperties().setVisible(true);
    }

  }

  private static void updateVisualProperties() {
    DEFAULT_VALUES = new RootWindowProperties(PROPERTIES.getDefaultMap());

    DEFAULT_VALUES.getWindowBarProperties().getTabWindowProperties().getTabProperties().getTitledTabProperties()
        .getNormalProperties().getComponentProperties().setBackgroundColor(
            TabbedUIDefaults.getHighlightedStateBackground());

    DEFAULT_VALUES.getComponentProperties()
        .setBackgroundColor(TabbedUIDefaults.getNormalStateBackground());

    DEFAULT_VALUES.getWindowAreaProperties()
        .setBorder(new LineBorder(UIManager.getColor("controlDkShadow")))
        .setBackgroundColor(ColorUtil.copy(UIManager.getColor("Desktop.background")));

    DEFAULT_VALUES.getDragLabelProperties()
        .setBorder(new LineBorder(UIManager.getColor("controlDkShadow")))
        .setBackgroundColor(ColorUtil.setAlpha(UIManager.getColor("ToolTip.background"), 200));

    DEFAULT_VALUES
        .setRecursiveTabsEnabled(true);
  }

  private static void updateFont() {
    Font font = TitledTabProperties.getDefaultProperties().getHighlightedProperties().
        getComponentProperties().getFont().deriveFont(Font.BOLD);

    DEFAULT_VALUES.getTabWindowProperties().getTabProperties().getTitledTabProperties().
        getHighlightedProperties().getComponentProperties().setFont(font);
  }

  static {
    DEFAULT_VALUES = new RootWindowProperties(PROPERTIES.getDefaultMap());

    DEFAULT_VALUES
        .setAbortDragKey(TabbedPanelProperties.getDefaultProperties().getAbortDragKey())
        .setEdgeSplitDistance(4)
        .setDragRectangleBorderWidth(5);

    DEFAULT_VALUES.getDockingWindowProperties()
        .setMaximizeEnabled(true)
        .setMinimizeEnabled(true)
        .setCloseEnabled(true)
        .setRestoreEnabled(true)
        .setDragEnabled(true);

    DEFAULT_VALUES.getWindowAreaProperties()
        .setInsets(new Insets(6, 6, 2, 2));

    DEFAULT_VALUES.getDragLabelProperties()
        .setInsets(new Insets(4, 6, 4, 6));

    DEFAULT_VALUES.getSplitWindowProperties()
        .setContinuousLayoutEnabled(true)
        .setDividerSize(4)
        .setDividerLocationDragEnabled(true);

    DEFAULT_VALUES.getViewProperties().setAlwaysShowTitle(true);

    setTabbedPanelProperties();
    setTabProperties();
    setWindowBarProperties();

    updateVisualProperties();

    updateFont();

    TitledTabProperties.getDefaultProperties().getHighlightedProperties().getComponentProperties().getMap().
        addListener(new PropertyMapListener() {
          public void propertyValuesChanged(PropertyMap propertyObject, Map changes) {
            updateFont();
          }
        });

    DynamicUIManager.getInstance().addListener(new DynamicUIManagerListener() {
      public void lookAndFeelChanged() {
        updateVisualProperties();
      }

      public void propertiesChanged() {
        updateVisualProperties();
      }
    });
  }

  /**
   * Creates a property object that inherits default property values.
   *
   * @return a new property object that inherits default property values
   */
  public static RootWindowProperties createDefault() {
    return new RootWindowProperties(DEFAULT_VALUES);
  }

  /**
   * Creates an empty property object.
   */
  public RootWindowProperties() {
    super(PropertyMapFactory.create(PROPERTIES));
  }

  /**
   * Creates a property object containing the map.
   *
   * @param map the property map
   */
  public RootWindowProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Creates a property object which inherits property values from another object.
   *
   * @param inheritFrom the object which from to inherit property values
   */
  public RootWindowProperties(RootWindowProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param properties the object from which to inherit property values
   * @return this
   */
  public RootWindowProperties addSuperObject(RootWindowProperties properties) {
    getMap().addSuperMap(properties.getMap());
    return this;
  }

  /**
   * Removes the last added super object.
   *
   * @return this
   * @since IDW 1.1.0
   */
  public RootWindowProperties removeSuperObject() {
    getMap().removeSuperMap();
    return this;
  }

  /**
   * Returns the default property values for tab windows.
   *
   * @return the default property values for tab windows
   */
  public TabWindowProperties getTabWindowProperties() {
    return new TabWindowProperties(TAB_WINDOW_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the default property values for split windows.
   *
   * @return the default property values for split windows
   */
  public SplitWindowProperties getSplitWindowProperties() {
    return new SplitWindowProperties(SPLIT_WINDOW_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the default property values for views.
   *
   * @return the default property values for views
   */
  public ViewProperties getViewProperties() {
    return new ViewProperties(VIEW_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the default property values for docking windows.
   *
   * @return the default property values for docking windows
   */
  public DockingWindowProperties getDockingWindowProperties() {
    return new DockingWindowProperties(DOCKING_WINDOW_PROPERTIES.get(getMap()));
  }

  /**
   * Sets the border width of the drag rectangle.
   *
   * @param width the border width
   * @return this
   */
  public RootWindowProperties setDragRectangleBorderWidth(int width) {
    DRAG_RECTANGLE_BORDER_WIDTH.set(getMap(), width);
    return this;
  }

  /**
   * Returns the border width of the drag rectangle.
   *
   * @return the border width of the drag rectangle
   */
  public int getDragRectangleBorderWidth() {
    return DRAG_RECTANGLE_BORDER_WIDTH.get(getMap());
  }

  /**
   * Returns true if the user is allowed to place tab windows inside other tab windows.
   *
   * @return true if tab windows are allowed to be placed in other tab windows
   */
  public boolean getRecursiveTabsEnabled() {
    return RECURSIVE_TABS_ENABLED.get(getMap());
  }

  /**
   * Returns true if double clicking on a window tab in a window bar restores the window.
   *
   * @return true if double clicking on a window tab in a window bar restores the window
   */
  public boolean getDoubleClickRestoresWindow() {
    return DOUBLE_CLICK_RESTORES_WINDOW.get(getMap());
  }

  /**
   * If set to true, double clicking on a window tab in a window bar restores the window.
   *
   * @param enabled if true, double clicking on a window tab in a window bar restores the window
   * @return this
   */
  public RootWindowProperties setDoubleClickRestoresWindow(boolean enabled) {
    DOUBLE_CLICK_RESTORES_WINDOW.set(getMap(), enabled);
    return this;
  }

  /**
   * If set to true, the user is allowed to place tab windows inside other tab windows.
   *
   * @param enabled if true, the user is allowed to place tab windows inside other tab windows
   * @return this
   */
  public RootWindowProperties setRecursiveTabsEnabled(boolean enabled) {
    RECURSIVE_TABS_ENABLED.set(getMap(), enabled);
    return this;
  }

  /**
   * Returns the property values for the drag label.
   *
   * @return the property values for the drag label
   */
  public ComponentProperties getDragLabelProperties() {
    return new ComponentProperties(DRAG_LABEL_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the property values for the root window component.
   *
   * @return the property values for the root window component
   */
  public ComponentProperties getComponentProperties() {
    return new ComponentProperties(COMPONENT_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the property values for the root window shaped panel.
   *
   * @return the property values for the root window shaped panel
   * @since IDW 1.2.0
   */
  public ShapedPanelProperties getShapedPanelProperties() {
    return new ShapedPanelProperties(SHAPED_PANEL_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the component property values for the window area component.
   *
   * @return the component property values for the window area component
   */
  public ComponentProperties getWindowAreaProperties() {
    return new ComponentProperties(WINDOW_AREA_PROPERTIES.get(getMap()));
  }

  /**
   * Returns the shaped panel property values for the window area component.
   *
   * @return the shaped panel property values for the window area component
   */
  public ShapedPanelProperties getWindowAreaShapedPanelProperties() {
    return new ShapedPanelProperties(WINDOW_AREA_SHAPED_PANEL_PROPERTIES.get(getMap()));
  }

  /**
   * Sets the distance from the window edge inside which a mouse drag will trigger a window split.
   *
   * @param size the distance from the window edge inside which a mouse drag will trigger a window split
   * @return this
   */
  public RootWindowProperties setEdgeSplitDistance(int size) {
    EDGE_SPLIT_DISTANCE.set(getMap(), size);
    return this;
  }

  /**
   * Returns the distance from the window edge inside which a mouse drag will trigger a window split.
   *
   * @return the distance from the window edge inside which a mouse drag will trigger a window split
   */
  public int getEdgeSplitDistance() {
    return EDGE_SPLIT_DISTANCE.get(getMap());
  }

  /**
   * Returns the key code for the key that aborts a drag.
   *
   * @return the key code for the key that aborts a drag
   */
  public int getAbortDragKey() {
    return ABORT_DRAG_KEY.get(getMap());
  }

  /**
   * Sets the key code for the key that aborts a drag.
   *
   * @param key the key code for the key that aborts a drag
   * @return this
   */
  public RootWindowProperties setAbortDragKey(int key) {
    ABORT_DRAG_KEY.set(getMap(), key);
    return this;
  }

  /**
   * Returns the default window bar property values.
   *
   * @return the default window bar property values
   * @since IDW 1.1.0
   */
  public WindowBarProperties getWindowBarProperties() {
    return new WindowBarProperties(WINDOW_BAR_PROPERTIES.get(getMap()));
  }

  /**
   * Shaped panel properties for the drag rectangle. Setting a painter disables the default drag rectangle.
   *
   * @return the drag rectangle shaped panel properties
   * @since IDW 1.2.0
   */
  public ShapedPanelProperties getDragRectangleShapedPanelProperties() {
    return new ShapedPanelProperties(DRAG_RECTANGLE_SHAPED_PANEL_PROPERTIES.get(getMap()));
  }

}
