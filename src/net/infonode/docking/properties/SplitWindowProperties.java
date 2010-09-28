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


// $Id: SplitWindowProperties.java,v 1.7 2004/07/05 12:56:37 jesper Exp $
package net.infonode.docking.properties;

import net.infonode.properties.propertymap.*;
import net.infonode.properties.types.IntegerProperty;

/**
 * Properties and property values for split windows.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.7 $
 */
public class SplitWindowProperties extends PropertyMapContainer {
  /**
   * Property group containing all split window properties.
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Split Window Properties", "");

  /**
   * The split pane divider size.
   */
  public static final IntegerProperty DIVIDER_SIZE =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Divider Size",
                                     "The split pane divider size.",
                                     2,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * Creates an empty property object.
   */
  public SplitWindowProperties() {
    super(PropertyMapFactory.create(PROPERTIES));
  }

  /**
   * Creates a property map containing the map.
   *
   * @param map the property map
   */
  public SplitWindowProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Creates a property object that inherit values from another property object.
   *
   * @param inheritFrom the object from which to inherit property values
   */
  public SplitWindowProperties(SplitWindowProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param properties the object from which to inherit property values
   * @return this
   */
  public SplitWindowProperties addSuperObject(SplitWindowProperties properties) {
    getMap().addSuperMap(properties.getMap());
    return this;
  }

  /**
   * Sets the split pane divider size.
   *
   * @param size the split pane divider size
   * @return this
   */
  public SplitWindowProperties setDividerSize(int size) {
    DIVIDER_SIZE.set(getMap(), size);
    return this;
  }

  /**
   * Returns the split pane divider size.
   * @return the split pane divider size
   */
  public int getDividerSize() {
    return DIVIDER_SIZE.get(getMap());
  }

}
