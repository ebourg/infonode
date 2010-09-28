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


// $Id: AbstractTabWindow.java,v 1.20 2004/08/11 13:47:58 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.location.WindowLocation;
import net.infonode.docking.location.WindowTabLocation;
import net.infonode.docking.properties.TabWindowProperties;
import net.infonode.docking.properties.WindowTabProperties;
import net.infonode.tabbedpanel.*;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Abstract base class for windows containing a tabbed panel.
 * 
 * @author $Author: jesper $
 * @version $Revision: 1.20 $
 */
abstract public class AbstractTabWindow extends DockingWindow {
	private TabWindowProperties	tabWindowProperties;
	private TabbedPanel					tabbedPanel;

	/**
	 * Temporary drag tab.
	 */
	private WindowTab						dragTab;

	protected AbstractTabWindow(boolean showContent) {
		tabbedPanel = showContent ? new TabbedPanel() : new TabbedPanel(null);
		tabbedPanel.addTabListener(new TabWindowMover(this, tabbedPanel));
		setComponent(tabbedPanel);

		getTabbedPanel().addTabListener(new TabAdapter() {
			public void tabSelected(TabStateChangedEvent event) {
				AbstractTabWindow.this.tabSelected((WindowTab) event.getTab());
			}

			public void tabMoved(TabEvent event) {
				fireTitleChanged();
			}
		});

	}

	/**
	 * Returns the currently selected window in the tabbed panel.
	 * 
	 * @return the currently selected window in the tabbed panel
	 */
	public DockingWindow getSelectedWindow() {
		WindowTab tab = (WindowTab) tabbedPanel.getSelectedTab();
		return tab == null ? null : tab.getWindow();
	}

	/**
	 * Selects the tab with the index.
	 * 
	 * @param index the tab index
	 */
	public void setSelectedTab(int index) {
		tabbedPanel.setSelectedTab(index == -1	? null : tabbedPanel.getTabAt(index));
		fireTitleChanged();
	}

	/**
	 * Returns the properties for this tab window.
	 * 
	 * @return the properties for this tab window
	 */
	public TabWindowProperties getTabWindowProperties() {
		return tabWindowProperties;
	}

	/**
	 * Adds a window tab last in this tab window.
	 * 
	 * @param window the window
	 */
	public void addTab(DockingWindow window) {
		addTab(window, tabbedPanel.getTabCount());
	}

	/**
	 * Inserts a window tab at an index in this tab window.
	 * 
	 * @param window the window
	 * @param index the index where to insert the tab
	 * @return the index of the added tab, this might not be the same as
	 *         <tt>index</tt> if the tab already is added to this tab window
	 */
	public int addTab(DockingWindow window, int index) {
		beginOptimize(window.getWindowParent());

		try {
			Tab beforeTab = index == tabbedPanel.getTabCount() ? null
																												: tabbedPanel
																																			.getTabAt(index);
			DockingWindow w = addWindow(window);
			WindowTab tab = w.getTab();
			updateTab(w);
			int actualIndex = beforeTab == null	? tabbedPanel.getTabCount()
																					: tabbedPanel.getTabIndex(beforeTab);
			tabbedPanel.insertTab(tab, actualIndex);
			return actualIndex;
		}
		finally {
			endOptimize();
		}
	}

	protected boolean childInsideTab() {
		return true;
	}

	protected void setTabWindowProperties(TabWindowProperties properties) {
		this.tabWindowProperties = properties;
		getTabbedPanel().getProperties()
										.addSuperObject(properties.getTabbedPanelProperties());
	}

	protected void clearFocus(View view) {
		if (getSelectedWindow() != null) {
			getSelectedWindow().clearFocus(view);
		}
	}

	protected void clearChildren(DockingWindow child, View view) {
		if (getSelectedWindow() != child)
			clearFocus(view);
	}

	protected void tabSelected(final WindowTab tab) {
		if (tab != null) {
			final RootWindow root = getRootWindow();

			if (root != null) {
        // Focus can move when content components are swapped in the tab panel
        root.startIgnoreFocusChanges();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								root.stopIgnoreFocusChanges();
								DockingWindow w = tab.getWindow();
								w.restoreFocus();
							}
						});
					}
				});
			}
		}

		fireTitleChanged();
	}

	protected TabbedPanel getTabbedPanel() {
		return tabbedPanel;
	}

	public DockingWindow getChildWindow(int index) {
		return (DockingWindow) tabbedPanel.getTabAt(index).getContentComponent();
	}

	protected DockingWindow getLocationWindow() {
		return tabbedPanel.getTabCount() == 1	? (DockingWindow) tabbedPanel
																																				.getTabAt(0)
																																				.getContentComponent()
																					: this;
	}

	public int getChildWindowCount() {
		return tabbedPanel.getTabCount();
	}

	protected WindowLocation getWindowLocation(DockingWindow window) {
		return tabbedPanel.getTabCount() < 2 ? getWindowLocation()
																				: new WindowTabLocation(this,
																																getWindowLocation(),
																																getChildWindowIndex(window));
	}

	public Icon getIcon() {
		if (tabbedPanel.getSelectedTab() != null)
			return ((DockingWindow) tabbedPanel.getSelectedTab()
																					.getContentComponent()) != null	? ((DockingWindow) tabbedPanel
																																																				.getSelectedTab()
																																																				.getContentComponent())
																																																																.getIcon()
																																					: null;

		return null;
	}

	private void updateTab(DockingWindow window) {
		window.getTab().setProperties(getTabProperties(window));
	}

	private WindowTabProperties getTabProperties(DockingWindow window) {
		WindowTabProperties properties = new WindowTabProperties(tabWindowProperties
																																								.getTabProperties());
		properties.addSuperObject(window.getWindowProperties().getTabProperties());
		return properties;
	}

	protected void doReplace(DockingWindow oldWindow, DockingWindow newWindow) {
		Tab tab = oldWindow.getTab();
		int tabIndex = tabbedPanel.getTabIndex(tab);

		boolean selected = tab.isSelected();
		tabbedPanel.removeTab(tab);
		tabbedPanel.insertTab(newWindow.getTab(), tabIndex);

		if (selected)
			tabbedPanel.setSelectedTab(newWindow.getTab());

		updateTab(newWindow);
	}

	protected void doRemoveWindow(DockingWindow window) {
		Tab tab = window.getTab();
		tabbedPanel.removeTab(tab);
	}

	DockingWindow acceptDrop(Point p, DockingWindow window) {
		Point p2 = SwingUtilities.convertPoint(this, p, tabbedPanel);
		boolean enableTab = getRootWindow().getRootWindowProperties()
																				.getRecursiveTabsEnabled()
												|| window.getChildWindowCount() <= 1;

		if (hasParent(window))
			return null;

		if (tabbedPanel.getTabCount() == 1
				&& window == tabbedPanel.getTabAt(0).getContentComponent())
			return null;

		if (enableTab && window.getTab().getTabbedPanel() != tabbedPanel) {
			if (dragTab == null) {
				if (!isSplittable() || tabbedPanel.tabAreaContainsPoint(p2)) {
					dragTab = new WindowTab(window, true);
					dragTab.setProperties(getTabProperties(window));
					tabbedPanel.insertTab(dragTab, p2);
					return this;
				}
			}
			else {
				if (!isSplittable() || tabbedPanel.tabAreaContainsPoint(p2)) {
					getRootWindow().setText(null, null);
					tabbedPanel.moveTab(dragTab, p2);
					return this;
				}
				else {
					stopDrag();
					return null;
				}
			}
		}

		if (dragTab == null) {
			if (tabbedPanel.getTabCount() > 1
					&& getRootWindow().getRootWindowProperties()
														.getRecursiveTabsEnabled()) {
				Direction d = getSplitDirection(p);
				Direction tabDir = tabbedPanel.getProperties().getTabAreaOrientation();
				if ((d == tabDir && !tabbedPanel.tabAreaContainsPoint(p2) && !tabbedPanel
																																									.contentAreaContainsPoint(p2))
						|| d != tabDir || !tabbedPanel.contains(p2))
					return super.acceptDrop(p, window);
			}
			else
				return super.acceptDrop(p, window);
		}

		return null;
	}

	void doDrop(Point p, DockingWindow window) {
		if (dragTab != null) {
			int index = tabbedPanel.getTabIndex(dragTab);
			stopDrag();
			addTab(window, index);
		}
		else {
			super.doDrop(p, window);
		}
	}

	void abortDrop() {
		stopDrag();
	}

	private void stopDrag() {
		if (dragTab != null) {
			tabbedPanel.removeTab(dragTab);
			dragTab = null;
		}
	}

	protected boolean showsWindowTitle() {
		return true;
	}

	protected void write(ObjectOutputStream out, WriteContext context) throws IOException {
		out.writeInt(WindowIds.TAB);
		out.writeInt(tabbedPanel.getTabCount());
    out.writeInt(tabbedPanel.getTabIndex(tabbedPanel.getSelectedTab()));

		for (int i = 0; i < tabbedPanel.getTabCount(); i++) {
			((DockingWindow) tabbedPanel.getTabAt(i).getContentComponent()).write(out, context);
		}

		super.write(out, context);
	}

	protected DockingWindow read(ObjectInputStream in, ReadContext context)	throws IOException {
		int size = in.readInt();
    int selectedIndex = in.readInt();

		for (int i = 0; i < getChildWindowCount(); i++)
			removeChildWindow(getChildWindow(0));

		for (int i = 0; i < size; i++) {
      DockingWindow window = WindowDecoder.decodeWindow(in, context);

      if (window != null)
  			addTab(window);
      else if (i < selectedIndex)
        selectedIndex--;
		}

		super.read(in, context);

    if (tabbedPanel.getTabCount() > 0) {
      if (selectedIndex >= 0)
  		  setSelectedTab(Math.min(tabbedPanel.getTabCount() - 1, selectedIndex));

      return this;
    }
    else
      return null;
	}

}