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


// $Id: Signal.java,v 1.3 2005/12/04 13:46:04 jesper Exp $
package net.infonode.util.signal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.3 $
 */
public class Signal {
  private static class WeakListener extends WeakReference<SignalListener> implements SignalListener {
    private SignalHookImpl hook;

    protected WeakListener(SignalListener listener, ReferenceQueue q, SignalHookImpl hook) {
      super(listener, q);
      this.hook = hook;
    }

    public void remove() {
      hook.removeWeak(this);
    }

    public void signalEmitted(Signal signal, Object object) {
      SignalListener l = get();

      if (l != null)
        l.signalEmitted(signal, object);
    }
  }

  private class SignalHookImpl implements SignalHook {
    public void add(SignalListener listener) {
      addListener(listener);
    }

    public void addWeak(SignalListener listener) {
      addListener(new WeakListener(listener, refQueue, this));
    }

    public boolean remove(SignalListener listener) {
      return removeListener(listener);
    }

    public void removeWeak(WeakListener ref) {
      removeWeakListener(ref);
    }
  }

  private static ReferenceQueue refQueue = new ReferenceQueue();

  static {
    Thread thread = new Thread(new Runnable() {
      public void run() {
        try {
          while (true) {
            ((WeakListener) refQueue.remove()).remove();
          }
        }
        catch (InterruptedException e) {
        }
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  private boolean reverseNotifyOrder;
  private CopyOnWriteArrayList<SignalListener> listeners;
  private SignalHookImpl signalHook = new SignalHookImpl();

  public Signal() {
    this(true);
  }

  public Signal(boolean reverseNotifyOrder) {
    this.reverseNotifyOrder = reverseNotifyOrder;
  }

  protected void firstListenerAdded() {
  }

  protected void lastListenerRemoved() {
  }

  public synchronized void addListener(SignalListener listener) {
    if (listeners == null)
      listeners = new CopyOnWriteArrayList<SignalListener>();

    listeners.add(listener);

    if (listeners.size() == 1)
      firstListenerAdded();
  }

  public synchronized boolean removeListener(SignalListener listener) {
    if (listeners != null) {
      for (int i = 0; i < listeners.size(); i++) {
        Object o = listeners.get(i);

        if (o == listener || (o instanceof WeakListener && ((WeakListener) o).get() == listener)) {
          removeListener(i);
          return true;
        }
      }
    }

    return false;
  }

  protected synchronized void removeWeakListener(WeakListener listener) {
    if (listeners != null) {
      for (int i = 0; i < listeners.size(); i++) {
        Object o = listeners.get(i);

        if (o == listener) {
          removeListener(i);
          break;
        }
      }
    }
  }

  protected synchronized void removeListener(int index) {
    listeners.remove(index);

    if (listeners.size() == 0) {
      listeners = null;
      lastListenerRemoved();
    }
  }

  public synchronized boolean hasListeners() {
    return listeners != null && listeners.size() > 0;
  }

  public synchronized Iterator iterator() {
    return listeners == null ? Collections.emptyIterator() : listeners.iterator();
  }

  public SignalHook getHook() {
    return signalHook;
  }

  public synchronized void emit(Object object) {
    Object[] e;
    int size;

    synchronized (this) {
      if (listeners == null)
        return;

      e = listeners.toArray();
      size = listeners.size();
    }

    if (reverseNotifyOrder) {
      for (int i = size - 1; i >= 0; i--)
        ((SignalListener) e[i]).signalEmitted(this, object);
    }
    else {
      for (int i = 0; i < size; i++)
        ((SignalListener) e[i]).signalEmitted(this, object);
    }
  }

  public void removeListeners(Collection toRemove) {
    listeners.removeAll(toRemove);
  }

}
