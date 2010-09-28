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


// $Id: TabbedPanelProperties.java,v 1.19 2004/08/11 13:47:58 jesper Exp $
package net.infonode.tabbedpanel;

import net.infonode.gui.DynamicUIManager;
import net.infonode.gui.DynamicUIManagerListener;
import net.infonode.properties.base.Property;
import net.infonode.properties.propertymap.*;
import net.infonode.properties.types.*;
import net.infonode.tabbedpanel.border.OpenContentBorder;
import net.infonode.util.ArrayUtil;
import net.infonode.util.Direction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;

/**
 * TabbedPanelProperties holds all properties for a {@link TabbedPanel}. A TabbedPanelProperties object contains
 * separate property objects for the content area and the tab area of the TabbedPanel.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.19 $
 * @see TabbedPanel
 * @see #getContentPanelProperties
 * @see #getTabAreaProperties
 */
public class TabbedPanelProperties extends PropertyMapContainer {
  /**
   * A property group for all properties in TabbedPanelProperties
   */
  public static final PropertyMapGroup PROPERTIES =
      new PropertyMapGroup("Tabbed Panel Properties", "Properties for the TabbedPanel class.");

  /**
   * Tab reorder property
   *
   * @see #setTabReorderEnabled
   * @see #getTabReorderEnabled
   */
  public static final BooleanProperty TAB_REORDER_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Tab Reorder Enabled",
                          "Tab reorder enabled or disabled",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Abort drag key code property
   *
   * @see #setAbortDragKey
   * @see #getAbortDragKey
   */
  public static final IntegerProperty ABORT_DRAG_KEY =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Abort Drag Key Code",
                                     "Key code for aborting drag",
                                     3,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * Tab layout property
   *
   * @see #setTabLayoutPolicy
   * @see #getTabLayoutPolicy
   */
  public static final TabLayoutPolicyProperty TAB_LAYOUT_POLICY =
      new TabLayoutPolicyProperty(PROPERTIES,
                                  "Layout Policy",
                                  "Tab layout in tab area",
                                  PropertyMapValueHandler.INSTANCE);

  /**
   * Tab scrolling offset property
   *
   * @see #setTabScrollingOffset
   * @see #getTabScrollingOffset
   */
  public static final IntegerProperty TAB_SCROLLING_OFFSET =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Scroll Offset",
                                     "Number of pixels to be shown for the last scrolled tab",
                                     3,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * Ensure selected visible property
   *
   * @see #setEnsureSelectedTabVisible
   * @see #getEnsureSelectedTabVisible
   */
  public static final BooleanProperty ENSURE_SELECTED_VISIBLE =
      new BooleanProperty(PROPERTIES,
                          "Ensure Selected Visible",
                          "Upon select, the selected tab will be scrolled into the visible area.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Tab area orientation property
   *
   * @see #setTabAreaOrientation
   * @see #getTabAreaOrientation
   */
  public static final DirectionProperty TAB_AREA_ORIENTATION =
      new DirectionProperty(PROPERTIES,
                            "Tab Area Orientation",
                            "Tab area's orientation relative to the content area.",
                            PropertyMapValueHandler.INSTANCE);

  /**
   * Tab spacing property
   *
   * @see #setTabSpacing
   * @see #getTabSpacing
   */
  public static final IntegerProperty TAB_SPACING =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Tab Spacing",
                                     "Number of pixels between tabs in tab area",
                                     2,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * Auto select tab property
   *
   * @see #setAutoSelectTab
   * @see #getAutoSelectTab
   */
  public static final BooleanProperty AUTO_SELECT_TAB =
      new BooleanProperty(PROPERTIES,
                          "Auto Select Tab",
                          "When enabled the first tab that i added will be selected automatically. " +
                          "If the selected tab is removed then the tab next to the removed tab will be selected automatically.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * If true the tab pressed with the mouse will be highlighted, otherwise it remains unchanged.
   *
   * @see #setHighlightPressedTab
   * @see #getHighlightPressedTab
   */
  public static final BooleanProperty HIGHLIGHT_PRESSED_TAB =
      new BooleanProperty(PROPERTIES,
                          "Highlight Pressed Tab",
                          "If true the tab pressed with the mouse will be highlighted, otherwise it remains unchanged.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Tab deselectable property
   *
   * @see #setTabDeselectable
   * @see #getTabDeselectable
   */
  public static final BooleanProperty TAB_DESELECTABLE =
      new BooleanProperty(PROPERTIES,
                          "Tab Deselectable",
                          "When enabled the selected tab can be deselected by clicking on it.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Content area properties
   *
   * @see #getContentPanelProperties
   */
  public static final PropertyMapProperty CONTENT_PANEL_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Content Panel Properties",
                              "Content panel properties.",
                              TabbedPanelContentPanelProperties.PROPERTIES);

  /**
   * Tab area properties
   *
   * @see #getTabAreaProperties
   */
  public static final PropertyMapProperty TAB_AREA_PROPERTIES =
      new PropertyMapProperty(PROPERTIES, "Tab Area Properties", "Tab area properties.", TabAreaProperties.PROPERTIES);

  /**
   * Shadow enabled property
   *
   * @see #setShadowEnabled
   * @see #getShadowEnabled
   */
  public static final BooleanProperty SHADOW_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Shadow Enabled",
                          "Indicates that a shadow is painted for the selected tab and the content panel.\n" +
                          "The shadow is partially painted using alpha transparency which can be slow on some systems.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Shadow size property
   *
   * @see #setShadowSize
   * @see #getShadowSize
   */
  public static final IntegerProperty SHADOW_SIZE =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Shadow Size",
                                     "The size of the tab shadow.",
                                     2,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * Shadow blend area size property
   *
   * @see #setShadowBlendAreaSize
   * @see #getShadowBlendAreaSize
   */
  public static final IntegerProperty SHADOW_BLEND_AREA_SIZE =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Shadow Blend Size",
                                     "The size of the tab shadow blend area.",
                                     2,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * Shadow color property
   *
   * @see #setShadowColor
   * @see #getShadowColor
   */
  public static final ColorProperty SHADOW_COLOR =
      new ColorProperty(PROPERTIES, "Shadow Color", "The color of the shadow.", PropertyMapValueHandler.INSTANCE);

  /**
   * Shadow strength property
   *
   * @see #setShadowStrength
   * @see #getShadowStrength
   */
  public static final FloatProperty SHADOW_STRENGTH =
      new FloatProperty(PROPERTIES,
                        "Shadow Strength",
                        "The strength of the shadow. 0 means the shadow color is the same as the backgound color, " +
                        "1 means the shadow color is '" + SHADOW_COLOR + "'.",
                        PropertyMapValueHandler.INSTANCE,
                        0,
                        1);


  /**
   * Array with all properties that controls the functional behavior
   */
  public static final Property[] FUNCTIONAL_PROPERTIES = {TAB_REORDER_ENABLED, ABORT_DRAG_KEY,
                                                          TAB_LAYOUT_POLICY, ENSURE_SELECTED_VISIBLE, AUTO_SELECT_TAB,
                                                          TAB_DESELECTABLE};

  /**
   * Array with all properties that controls the shadow
   */
  public static final Property[] SHADOW_PROPERTIES = {SHADOW_ENABLED, SHADOW_SIZE, SHADOW_BLEND_AREA_SIZE, SHADOW_COLOR,
                                                      SHADOW_STRENGTH};

  /**
   * Array with all properties that controls the visual apperance except for shadow
   */
  public static final Property[] TABS_VISUAL_PROPERTIES = {TAB_SCROLLING_OFFSET, TAB_SPACING, TAB_AREA_ORIENTATION,
                                                           TAB_LAYOUT_POLICY, CONTENT_PANEL_PROPERTIES};

  /**
   * Array with all properties that controls the visual apperance including shadow
   */
  public static final Property[] VISUAL_PROPERTIES =
      (Property[]) ArrayUtil.append(TABS_VISUAL_PROPERTIES,
                                    SHADOW_PROPERTIES,
                                    new Property[TABS_VISUAL_PROPERTIES.length + SHADOW_PROPERTIES.length]);

  private static final TabbedPanelProperties DEFAULT_PROPERTIES = new TabbedPanelProperties(PROPERTIES.getDefaultMap());

  static {
    DynamicUIManager.getInstance().addListener(new DynamicUIManagerListener() {
      public void lookAndFeelChanged() {
        updateVisualProperties();
      }

      public void propertyChange(PropertyChangeEvent event) {
        updateVisualProperties();
      }
    });

    updateVisualProperties();
    updateFunctionalProperties();
  }

  /**
   * Creates a properties object with default properties based on the current look and feel
   *
   * @return properties object
   */
  public static TabbedPanelProperties getDefaultProperties() {
    return new TabbedPanelProperties(DEFAULT_PROPERTIES);
  }

  private static void updateVisualProperties() {
    PropertyMapManager.runBatch(new Runnable() {
      public void run() {
        DEFAULT_PROPERTIES.getContentPanelProperties().getComponentProperties()
            .setBorder(new OpenContentBorder(TabbedUIDefaults.getDarkShadow(), TabbedUIDefaults.getHighlight()))
            .setInsets(TabbedUIDefaults.getContentAreaInsets())
            .setBackgroundColor(TabbedUIDefaults.getContentAreaBackground());
      }
    });
  }

  private static void updateFunctionalProperties() {
    DEFAULT_PROPERTIES
        .setTabReorderEnabled(false)
        .setAbortDragKey(KeyEvent.VK_ESCAPE)
        .setTabLayoutPolicy(TabLayoutPolicy.SCROLLING)
        .setTabScrollingOffset(10)
        .setTabSpacing(0)
        .setEnsureSelectedTabVisible(false)
        .setTabAreaOrientation(Direction.UP)
        .setAutoSelectTab(true)
        .setHighlightPressedTab(true)

        .setShadowEnabled(false)
        .setShadowSize(3)
        .setShadowBlendAreaSize(3)

        .setShadowColor(Color.BLACK)
        .setShadowStrength(0.4F);
  }

  /**
   * Constructs an empty TabbedPanelProperties object
   */
  public TabbedPanelProperties() {
    super(PROPERTIES);
  }

  /**
   * Constructs a TabbedPanelProperties map with the given map as property storage
   *
   * @param map map to store properties in
   */
  public TabbedPanelProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Constructs a TabbedPanelProperties object that inherits its properties from the given TabbedPanelProperties object
   *
   * @param inheritFrom TabbedPanelProperties object to inherit properties from
   */
  public TabbedPanelProperties(TabbedPanelProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param superObject the object from which to inherit property values
   *
   * @return this
   */
  public TabbedPanelProperties addSuperObject(TabbedPanelProperties superObject) {
    getMap().addSuperMap(superObject.getMap());
    return this;
  }

  /**
   * <p>Sets the shadow strength. 0 means the shadow color is the same as the backgound color and 1 means the shadow
   * color is the same as shadow color.</p>
   * <p/>
   * <p><strong>Note:</strong> This property will only have effect if shadow is enabled.</p>
   *
   * @param strength the strength between 0 and 1
   *
   * @return this TabbedPanelProperties
   *
   * @see #setShadowColor
   * @see #setShadowEnabled
   */
  public TabbedPanelProperties setShadowStrength(float strength) {
    SHADOW_STRENGTH.set(getMap(), strength);
    return this;
  }

  /**
   * <p>Sets the shadow blend area size, i.e. number of pixels for the shadow color fading.</p>
   * <p/>
   * <p><strong>Note:</strong> This property will only have effect if shadow is enabled.</p>
   *
   * @param size the shadow blend area size in pixels
   *
   * @return this TabbedPanelProperties
   *
   * @see #setShadowEnabled
   */
  public TabbedPanelProperties setShadowBlendAreaSize(int size) {
    SHADOW_BLEND_AREA_SIZE.set(getMap(), size);
    return this;
  }

  /**
   * <p>Sets the shadow size, i.e. the width/height of the shadow in pixels.</p>
   * <p/>
   * <p><strong>Note:</strong> This property will only have effect if shadow is enabled.</p>
   *
   * @param size shadow size in pixels
   *
   * @return this TabbedPanelProperties
   *
   * @see #setShadowEnabled
   */
  public TabbedPanelProperties setShadowSize(int size) {
    SHADOW_SIZE.set(getMap(), size);
    return this;
  }

  /**
   * <p>Sets the shadow color.</p>
   * <p/>
   * <p><strong>Note:</strong> This property will only have effect if shadow is enabled.</p>
   *
   * @param color the shadow color
   *
   * @return this TabbedPanelProperties
   *
   * @see #setShadowEnabled
   */
  public TabbedPanelProperties setShadowColor(Color color) {
    SHADOW_COLOR.set(getMap(), color);
    return this;
  }

  /**
   * Sets shadow enabled
   *
   * @param value true for enabled, otherwise false
   *
   * @return this TabbedPanelProperties
   */
  public TabbedPanelProperties setShadowEnabled(boolean value) {
    SHADOW_ENABLED.set(getMap(), value);
    return this;
  }

  /**
   * Sets if automatic selection of a tab is enabled. Automatic selection means that if no tab is selected and a tab is
   * added to the TabbedPanel, then the added tab will automatically be selected. If a selected tab is removed from the
   * TabbedPanel then the tab next to the selected tab will automatically be selected.
   *
   * @param value true for automactic selection, otherwise false
   *
   * @return this TabbedPanelProperties
   */
  public TabbedPanelProperties setAutoSelectTab(boolean value) {
    AUTO_SELECT_TAB.set(getMap(), value);
    return this;
  }

  /**
   * Sets if tab is deselectable. This means that if the selected tab is clicked then the selected tab will be
   * deselected. Clicking it again will select the tab again.
   *
   * @param value true for deselectable, otherwise false
   *
   * @return this TabbedPanelProperties
   */
  public TabbedPanelProperties setTabDeselectable(boolean value) {
    TAB_DESELECTABLE.set(getMap(), value);
    return this;
  }

  /**
   * <p>Sets if a tab should be made visible if it is selected, i.e. if scrolling is enabled, a tab will be scrolled
   * into the visible part of the tab area when it becomes selected.</p>
   * <p/>
   * <p><strong>Note:</strong> This will only have effect if scolling is enabled.</p>
   *
   * @param value true for selected visible, otherwise false
   *
   * @return this TabbedPanelProperties
   *
   * @see #setTabLayoutPolicy
   */
  public TabbedPanelProperties setEnsureSelectedTabVisible(boolean value) {
    ENSURE_SELECTED_VISIBLE.set(getMap(), value);
    return this;
  }

  /**
   * <p>Sets number of pixels to be shown for the scrolled out tab next to the first visible tab.</p>
   * <p/>
   * <p><strong>Note:</strong> This will only have effect if scolling is enabled.</p>
   *
   * @param offset number of pixels
   *
   * @return this TabbedPanelProperties
   *
   * @see #setTabLayoutPolicy
   */
  public TabbedPanelProperties setTabScrollingOffset(int offset) {
    TAB_SCROLLING_OFFSET.set(getMap(), offset);
    return this;
  }

  /**
   * Sets if the tabs can be reordered using the mouse
   *
   * @param enabled true for enabled, otherwise disabled
   *
   * @return this TabbedPanelProperties
   */
  public TabbedPanelProperties setTabReorderEnabled(boolean enabled) {
    TAB_REORDER_ENABLED.set(getMap(), enabled);
    return this;
  }

  /**
   * Set to true if the tab pressed with the mouse should be highlighted, otherwise it's not changed.
   *
   * @param highlightEnabled true if the tab pressed with the mouse should be highlighted
   *
   * @return this
   */
  public TabbedPanelProperties setHighlightPressedTab(boolean highlightEnabled) {
    HIGHLIGHT_PRESSED_TAB.set(getMap(), highlightEnabled);
    return this;
  }

  /**
   * <p>Sets the key code for aborting a tab drag or reorder operation.</p>
   * <p/>
   * <p><strong>Note:</strong> The right mouse button can also be used to abort the operation.</p>
   *
   * @param keyCode key code
   *
   * @return this TabbedPanelProperties
   */
  public TabbedPanelProperties setAbortDragKey(int keyCode) {
    ABORT_DRAG_KEY.set(getMap(), keyCode);
    return this;
  }

  /**
   * Sets the tab layout policy for the tab area, i.e. how the line of tabs should be laid out
   *
   * @param policy the tab area layout policy
   *
   * @return this TabbedPanelProperties
   */
  public TabbedPanelProperties setTabLayoutPolicy(TabLayoutPolicy policy) {
    TAB_LAYOUT_POLICY.set(getMap(), policy);
    return this;
  }

  /**
   * Sets the tab area orientation, i.e. if the tab area should be placed up, down, left or right of the content area.
   *
   * @param direction the orientation
   *
   * @return this TabbedPanelProperties
   */
  public TabbedPanelProperties setTabAreaOrientation(Direction direction) {
    TAB_AREA_ORIENTATION.set(getMap(), direction);
    return this;
  }

  /**
   * Sets the tab spacing, i.e. number of pixels between the tabs in the tab area
   *
   * @param value number of pixels
   *
   * @return this TabbedPanelProperties
   */
  public TabbedPanelProperties setTabSpacing(int value) {
    TAB_SPACING.set(getMap(), value);
    return this;
  }

  /**
   * <p>Gets the shadow strength. 0 means the shadow color is the same as the backgound color and 1 means the shadow
   * color is the same as shadow color.</p>
   * <p/>
   * <p><strong>Note:</strong> This property will only have effect if shadow is enabled.</p>
   *
   * @return the shadow strength between 0 and 1
   *
   * @see #getShadowColor
   * @see #getShadowEnabled
   */
  public float getShadowStrength() {
    return SHADOW_STRENGTH.get(getMap());
  }

  /**
   * <p>Gets the shadow blend area size, i.e. number of pixels for the shadow color fading.</p>
   * <p/>
   * <p><strong>Note:</strong> This property will only have effect if shadow is enabled.</p>
   *
   * @return the shadow blend area size in pixels
   *
   * @see #getShadowEnabled
   */
  public int getShadowBlendAreaSize() {
    return SHADOW_BLEND_AREA_SIZE.get(getMap());
  }

  /**
   * <p>Gets the shadow size, i.e. the width/height of the shadow in pixels.</p>
   * <p/>
   * <p><strong>Note:</strong> This property will only have effect if shadow is enabled.</p>
   *
   * @return shadow size in pixels
   *
   * @see #getShadowEnabled
   */
  public int getShadowSize() {
    return SHADOW_SIZE.get(getMap());
  }

  /**
   * <p>Gets the shadow color.</p>
   * <p/>
   * <p><strong>Note:</strong> This property will only have effect if shadow is enabled.</p>
   *
   * @return the shadow color
   *
   * @see #getShadowEnabled
   */
  public Color getShadowColor() {
    return SHADOW_COLOR.get(getMap());
  }

  /**
   * Gets shadow enabled
   *
   * @return true if shadow is enabled, otherwise false
   */
  public boolean getShadowEnabled() {
    return SHADOW_ENABLED.get(getMap());
  }

  /**
   * Gets if automatic selection of a tab is enabled. Automatic selection means that if no tab is selected and a tab is
   * added to the TabbedPanel, then the added tab will automatically be selected. If a selected tab is removed from the
   * TabbedPanel then the tab next to the selected tab will automatically be selected.
   *
   * @return true if automactic selection, otherwise false
   */
  public boolean getAutoSelectTab() {
    return AUTO_SELECT_TAB.get(getMap());
  }

  /**
   * Gets if the tab pressed with the mouse will be highlighted.
   *
   * @return true if the tab pressed with the mouse will be highlighted
   */
  public boolean getHighlightPressedTab() {
    return HIGHLIGHT_PRESSED_TAB.get(getMap());
  }

  /**
   * Gets if tab is deselectable. This means that if the selected tab is clicked then the selected tab will be
   * deselected. Clicking it again will select the tab again.
   *
   * @return true if deselectable, otherwise false
   */
  public boolean getTabDeselectable() {
    return TAB_DESELECTABLE.get(getMap());
  }

  /**
   * <p>Gets if a tab should be made visible if it is selected, i.e. if scrolling is enabled, a tab will be scrolled
   * into the visible part of the tab area when it becomes selected.</p>
   * <p/>
   * <p><strong>Note:</strong> This will only have effect if scolling is enabled.</p>
   *
   * @return true if selected visible should be made visible, otherwise false
   *
   * @see #getTabLayoutPolicy
   */
  public boolean getEnsureSelectedTabVisible() {
    return ENSURE_SELECTED_VISIBLE.get(getMap());
  }

  /**
   * <p>Gets number of pixels to be shown for the last scrolled tab.</p>
   * <p/>
   * <p><strong>Note:</strong> This will only have effect if scolling is enabled.</p>
   *
   * @return number of pixels
   *
   * @see #getTabLayoutPolicy
   */
  public int getTabScrollingOffset() {
    return TAB_SCROLLING_OFFSET.get(getMap());
  }

  /**
   * Gets if the tabs can be reorder using the mouse.
   *
   * @return true if enabled, otherwise disabled
   */
  public boolean getTabReorderEnabled() {
    return TAB_REORDER_ENABLED.get(getMap());
  }

  /**
   * <p>Gets the key code for aborting a tab drag or reorder operation.</p>
   * <p/>
   * <p>Note that the right mouse button can also be used to abort the operation.</p>
   *
   * @return the key code
   */
  public int getAbortDragKey() {
    return ABORT_DRAG_KEY.get(getMap());
  }

  /**
   * Gets the tab layout policy for the tab area, i.e. how the line of tabs should be laid out
   *
   * @return the tab area layout policy
   */
  public TabLayoutPolicy getTabLayoutPolicy() {
    return TAB_LAYOUT_POLICY.get(getMap());
  }

  /**
   * Gets the tab area orientation, i.e. if the tab area should be placed up, down, left or right of the content area
   *
   * @return the orientation
   */
  public Direction getTabAreaOrientation() {
    return TAB_AREA_ORIENTATION.get(getMap());
  }

  /**
   * Gets the tab spacing, i.e. number of pixels between the tabs in the tab area
   *
   * @return number of pixels
   */
  public int getTabSpacing() {
    return TAB_SPACING.get(getMap());
  }

  /**
   * Gets the properties getMap() with properties for the tabbed panel's content area
   *
   * @return the properties for the content area
   */
  public TabbedPanelContentPanelProperties getContentPanelProperties() {
    return new TabbedPanelContentPanelProperties(CONTENT_PANEL_PROPERTIES.get(getMap()));
  }

  /**
   * Gets the properties getMap() with properties for the tabbed panel's tab area
   *
   * @return the properties for the tab area
   */
  public TabAreaProperties getTabAreaProperties() {
    return new TabAreaProperties(TAB_AREA_PROPERTIES.get(getMap()));
  }
}
