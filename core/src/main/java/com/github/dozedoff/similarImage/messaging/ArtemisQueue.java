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
package com.github.dozedoff.similarImage.messaging;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtemisQueue {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtemisQueue.class);

	public enum QueueAddress {
		hash, result
	}
	
	private final ClientSession session;

	/**
	 * Create a instance ready for managing queues
	 * 
	 * @param session
	 *            to create queues on
	 */
	public ArtemisQueue(ClientSession session) {
		this.session = session;
	}
	
	/**
	 * Creates all queues. Does not check if they already exist.
	 */
	public void createAll() {
		for (QueueAddress queue : QueueAddress.values()) {
			queueHelper(queue);
		}
	}
	
	private void queueHelper(QueueAddress address) {
		String addr = address.toString();
		
		LOGGER.info("Creating queue for address {} ...", addr);

		try {
			this.session.createQueue(addr, addr, false);
		} catch (ActiveMQException e) {
			LOGGER.error("Failed to create queue {}: {}", addr, e.toString());
		}
	}
}
