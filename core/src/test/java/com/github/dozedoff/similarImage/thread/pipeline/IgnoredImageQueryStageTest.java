/*  Copyright (C) 2017  Nicholas Wright
    
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
package com.github.dozedoff.similarImage.thread.pipeline;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.dozedoff.similarImage.db.repository.ImageRepository;
import com.github.dozedoff.similarImage.db.repository.RepositoryException;

@RunWith(MockitoJUnitRunner.class)
public class IgnoredImageQueryStageTest {
	private static final Path PATH = Paths.get("foo");

	@Mock
	private ImageRepository imageRepository;
	@InjectMocks
	private IgnoredImageQueryStage cut;

	@Before
	public void setUp() throws Exception {
	}

	
	@Test
	public void testQueryForNull() throws Exception {
		cut.apply(null);

		verify(imageRepository).getAllWithoutIgnored();
	}

	@Test
	public void testQueryForEmpty() throws Exception {
		cut.apply(Paths.get(""));

		verify(imageRepository).getAllWithoutIgnored();
	}

	@Test
	public void testQueryForPath() throws Exception {
		cut.apply(PATH);

		verify(imageRepository).getAllWithoutIgnored(PATH);
	}

	@Test
	public void testRepositoryError() throws Exception {
		when(imageRepository.getAllWithoutIgnored()).thenThrow(new RepositoryException(""));

		assertThat(cut.apply(null), is(empty()));
	}
}
