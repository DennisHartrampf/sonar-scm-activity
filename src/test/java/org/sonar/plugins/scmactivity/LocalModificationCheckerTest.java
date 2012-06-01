/*
 * Sonar SCM Activity Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.scmactivity;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.command.status.StatusScmResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class LocalModificationCheckerTest {
  LocalModificationChecker checker;

  ScmConfiguration configuration = mock(ScmConfiguration.class);
  ScmFacade scmFacade = mock(ScmFacade.class);
  StatusScmResult statusScmResult = mock(StatusScmResult.class);
  ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() {
    checker = new LocalModificationChecker(fileSystem, configuration, scmFacade);
  }

  @Test
  public void should_ignore() {
    when(configuration.isIgnoreLocalModifications()).thenReturn(true);

    checker.check();

    verifyZeroInteractions(scmFacade);
  }

  @Test
  public void should_check_there_is_no_local_changes() throws ScmException {
    when(fileSystem.getSourceDirs()).thenReturn(Arrays.asList(new File("src")));
    when(fileSystem.getTestDirs()).thenReturn(Arrays.asList(new File("other")));
    when(scmFacade.localChanges(any(File.class))).thenReturn(statusScmResult);
    when(statusScmResult.isSuccess()).thenReturn(true);

    checker.check();
  }

  @Test
  public void should_fail_when_unable_to_check() throws ScmException {
    when(fileSystem.getSourceDirs()).thenReturn(Arrays.asList(new File("src")));
    when(scmFacade.localChanges(new File("src"))).thenReturn(statusScmResult);
    when(statusScmResult.isSuccess()).thenReturn(false);
    when(statusScmResult.getProviderMessage()).thenReturn("BUG");

    exception.expect(SonarException.class);
    exception.expectMessage("Unable to check for local modifications: BUG");

    checker.check();
  }

  @Test
  public void should_fail_on_error() throws ScmException {
    when(fileSystem.getSourceDirs()).thenReturn(Arrays.asList(new File("src")));
    when(scmFacade.localChanges(new File("src"))).thenThrow(new ScmException("BUG"));

    exception.expect(SonarException.class);
    exception.expectMessage("Unable to check for local modifications");

    checker.check();
  }

  @Test
  public void should_fail_with_local_changes() throws ScmException {
    when(fileSystem.getSourceDirs()).thenReturn(Arrays.asList(new File("src")));
    when(scmFacade.localChanges(new File("src"))).thenReturn(statusScmResult);
    when(statusScmResult.isSuccess()).thenReturn(true);
    when(statusScmResult.getChangedFiles()).thenReturn(Arrays.asList(new ScmFile("source.java", null)));

    exception.expect(SonarException.class);
    exception.expectMessage("Fail to load SCM data as there are local modifications");

    checker.check();
  }
}
