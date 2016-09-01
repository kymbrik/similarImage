/*  Copyright (C) 2016  Nicholas Wright
    
    This file is part of similarImage - A similar image finder using pHash
    
    similarImage is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dozedoff.similarImage.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.HashMap;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.github.dozedoff.similarImage.db.CustomUserTag;
import com.github.dozedoff.similarImage.db.Persistence;
import com.github.dozedoff.similarImage.duplicate.DuplicateOperations;
import com.github.dozedoff.similarImage.duplicate.ImageInfo;
import com.github.dozedoff.similarImage.event.GuiEventBus;
import com.github.dozedoff.similarImage.event.GuiUserTagChangedEvent;
import com.google.common.eventbus.Subscribe;

public class OperationsMenu {
	private final DuplicateOperations duplicateOperations;
	private final UserTagSettingController utsController;
	private JPopupMenu menu;
	private final HashMap<Operations, ActionListener> actions = new HashMap<OperationsMenu.Operations, ActionListener>();

	private enum Operations {
		Delete, MarkAndDeleteDNW, MarkBlocked, Ignore
	};

	private final ImageInfo imageInfo;

	public OperationsMenu(ImageInfo imageInfo, Persistence persistence, UserTagSettingController utsController) {
		super();
		this.duplicateOperations = new DuplicateOperations(persistence);
		this.imageInfo = imageInfo;
		this.menu = new JPopupMenu();
		this.utsController = utsController;

		setupPopupMenu();
		GuiEventBus.getInstance().register(this);
	}

	private void setupPopupMenu() {
		actions.put(Operations.Delete, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path path = imageInfo.getPath();
				duplicateOperations.deleteFile(path);
			}
		});

		actions.put(Operations.MarkAndDeleteDNW, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path path = imageInfo.getPath();
				duplicateOperations.markAs(path, DuplicateOperations.Tags.DNW.toString());
				duplicateOperations.deleteFile(path);
			}
		});

		actions.put(Operations.MarkBlocked, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path path = imageInfo.getPath();
				duplicateOperations.markAs(path, "BLOCK");
			}
		});

		actions.put(Operations.Ignore, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				duplicateOperations.ignore(imageInfo.getpHash());
			}
		});

		createMenuItems(actions);
	}

	private void createUserTags() {
		for (CustomUserTag cut : utsController.getAllUserTags()) {
			JMenuItem menuItem = new JMenuItem("Tag " + cut.getTag());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					duplicateOperations.markAs(imageInfo.getPath(), cut.getTag());
				}
			});

			menu.add(menuItem);
		}
	}

	private void createMenuItems(HashMap<Operations, ActionListener> actions) {
		for (Operations op : Operations.values()) {
			ActionListener listener = actions.get(op);

			if (listener != null) {
				JMenuItem jmi = new JMenuItem(op.toString());
				jmi.addActionListener(listener);
				menu.add(jmi);
			}
		}

		createUserTags();
	}

	@Subscribe
	private void reCreateMenu(GuiUserTagChangedEvent event) {
		menu = new JPopupMenu();
		createMenuItems(actions);
		createUserTags();
	}

	public JPopupMenu getMenu() {
		return menu;
	}
}
