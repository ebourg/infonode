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


// $Id: WeakSet.java,v 1.5 2004/11/05 13:05:48 jesper Exp $
package net.infonode.util.collection;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.5 $
 */
public class WeakSet {
  private WeakReference[] elements;
  private int size;

  public WeakSet() {
    this(10);
  }

  public WeakSet(int initialCapacity) {
    elements = new WeakReference[initialCapacity];
  }

  public void add(Object element) {
    int index = findFreeIndex();
    elements[index] = new WeakReference(element);
  }

  public void remove(Object element) {
    int count = 0;

    for (int i = 0; i < size; i++) {
      if (elements[i] != null) {
        Object o = elements[i].get();

        if (o == null || o == element) {
          elements[i] = null;
        }
        else
          count++;
      }
    }

    cleanUp(count);
  }

  public boolean contains(Object element) {
    for (int i = 0; i < size; i++) {
      if (elements[i] != null) {
        if (elements[i].get() == element)
          return true;
      }
    }

    return false;
  }

  public java.util.Iterator iterator() {
    cleanUp();
    return new Iterator(elements, size);
  }

  public Object[] getElements() {
    cleanUp();
    ArrayList l = new ArrayList(size);

    for (int i = 0; i < size; i++) {
      if (elements[i] != null) {
        Object o = elements[i].get();

        if (o != null)
          l.add(o);
      }
    }

    return l.toArray();
  }

  public void each(Closure closure) {
    cleanUp();
    WeakReference[] l = elements;
    int s = size;

    for (int i = 0; i < s; i++) {
      if (l[i] != null) {
        Object o = l[i].get();

        if (o != null)
          closure.apply(o);
      }
    }
  }

  private void cleanUp() {
    int count = 0;

    for (int i = 0; i < size; i++) {
      if (elements[i] != null) {
        if (elements[i].get() == null)
          elements[i] = null;
        else
          count++;
      }
    }

    cleanUp(count);
  }

  private void cleanUp(int count) {
    if (count >= elements.length / 2)
      return;

    WeakReference[] newElements = new WeakReference[(count * 3) / 2 + 1];
    count = 0;

    for (int i = 0; i < size; i++) {
      if (elements[i] != null)
        newElements[count++] = elements[i];
    }

    elements = newElements;
    size = count;
  }

  private int findFreeIndex() {
    if (size < elements.length)
      return size++;

    cleanUp();

    if (size < elements.length)
      return size++;

    WeakReference[] newElements = new WeakReference[(size * 3) / 2 + 1];
    System.arraycopy(elements, 0, newElements, 0, size);
    elements = newElements;
    return size++;
  }

  public boolean isEmpty() {
    cleanUp();
    return size == 0;
  }

  private static class Iterator implements java.util.Iterator {
    private WeakReference[] elements;
    private int index;
    private int size;
    private Object next;

    Iterator(WeakReference[] elements, int size) {
      this.elements = elements;
      this.size = size;
      advance();
    }

    private void advance() {
      while (index < size) {
        if (elements[index] != null) {
          next = elements[index].get();

          if (next != null) {
            index++;
            return;
          }
        }

        index++;
      }

      next = null;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
      return next != null;
    }

    public Object next() {
      Object o = next;
      advance();
      return o;
    }
  }
}
