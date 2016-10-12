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
package com.github.dozedoff.similarImage.handler;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import javax.management.InvalidAttributeValueException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.similarImage.db.ImageRecord;
import com.github.dozedoff.similarImage.db.Persistence;
import com.github.dozedoff.similarImage.db.repository.ImageRepository;
import com.github.dozedoff.similarImage.db.repository.RepositoryException;
import com.github.dozedoff.similarImage.db.repository.ormlite.OrmliteImageRepository;
import com.github.dozedoff.similarImage.io.HashAttribute;
import com.j256.ormlite.dao.DaoManager;

/**
 * This handler reads the hash from the extended attributes of a file and stores them in the database.
 * 
 * @author Nicholas Wright
 *
 */
public class ExtendedAttributeHandler implements HashHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedAttributeHandler.class);

	private HashAttribute hashAttribute;
	private final ImageRepository imageRepository;

	/**
	 * Sets up the handler to read extended attributes and access the database.
	 * 
	 * @param hashAttribute
	 *            to read the extended attributes
	 * @param persistence
	 *            to access the database
	 * @deprecated Use {@link ExtendedAttributeHandler#ExtendedAttributeHandler(HashAttribute, ImageRepository)}
	 *             instead.
	 */
	@Deprecated
	public ExtendedAttributeHandler(HashAttribute hashAttribute, Persistence persistence) {
		this.hashAttribute = hashAttribute;
		try {
			this.imageRepository = new OrmliteImageRepository(
					DaoManager.createDao(persistence.getCs(), ImageRecord.class));
		} catch (RepositoryException | SQLException e) {
			throw new RuntimeException("Failed to create repository");
		}
	}

	/**
	 * Sets up the handler to read extended attributes and access the database.
	 * 
	 * @param hashAttribute
	 *            to read the extended attributes
	 * @param imageRepository
	 *            access to the image datasource
	 */
	public ExtendedAttributeHandler(HashAttribute hashAttribute, ImageRepository imageRepository) {
		this.hashAttribute = hashAttribute;
		this.imageRepository = imageRepository;
	}

	/**
	 * Read the extended attributes from the file and store them in the database.
	 * 
	 * @param file
	 *            to read extended attributes from
	 * @return true if the attributes were successfully read from the file and stored.
	 */
	@Override
	public boolean handle(Path file) {
		LOGGER.trace("Handling {} with {}", file, ExtendedAttributeHandler.class.getSimpleName());
		if (hashAttribute.areAttributesValid(file)) {
			LOGGER.trace("{} has valid extended attributes", file);
			try {
				imageRepository.store(new ImageRecord(file.toString(), hashAttribute.readHash(file)));
				// Successfully
				LOGGER.trace("Successfully read and stored the hash for {}", file);
				return true;
			} catch (InvalidAttributeValueException | IOException e) {
				LOGGER.error("Failed to read extended attribute from {} ({})", file, e.toString());
			} catch (RepositoryException e) {
				LOGGER.error("Failed to access database for {} ({})", file, e.toString());
			}
		}

		return false;
	}
}
