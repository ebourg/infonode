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


// $Id: TitledTabBorderSizePolicyProperty.java,v 1.3 2004/06/24 13:49:50 johan Exp $
package net.infonode.tabbedpanel.titledtab;

import net.infonode.properties.base.PropertyGroup;
import net.infonode.properties.types.EnumProperty;
import net.infonode.properties.util.PropertyValueHandler;

/**
 * Property for TitledTabBorderSizePolicy
 *
 * @see TitledTabBorderSizePolicy
 * @author $Author: johan $
 * @version $Revision: 1.3 $
 */
public class TitledTabBorderSizePolicyProperty extends EnumProperty {
  /**
   * Constructs a TitledTabBorderSizePolicyProperty object.
   *
   * @param group         property group
   * @param name          property name
   * @param description   property description
   * @param valueStorage  storage for property
   */
  public TitledTabBorderSizePolicyProperty(PropertyGroup group, String name, String description, PropertyValueHandler valueStorage) {
    super(group, name, TitledTabBorderSizePolicy.class, description, valueStorage, TitledTabBorderSizePolicy.SIZE_POLICIES);
  }

  /**
   * Gets the TitledTabBorderSizePolicy
   *
   * @param object storage object for property
   * @return the TitledTabBorderSizePolicy
   */
  public TitledTabBorderSizePolicy get(Object object) {
    return (TitledTabBorderSizePolicy)getValue(object);
  }

  /**
   * Sets the TitledTabBorderSizePolicy
   *
   * @param object storage object for property
   * @param policy the TitledTabBorderSizePolicy
   */
  public void set(Object object, TitledTabBorderSizePolicy policy) {
    setValue(object, policy);
  }
}
