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

import org.sonar.api.BatchExtension;
import org.sonar.api.resources.ProjectFileSystem;

import java.io.File;

public class ScmUrlGuess implements BatchExtension {
  private final ProjectFileSystem projectFileSystem;

  public ScmUrlGuess(ProjectFileSystem projectFileSystem) {
    this.projectFileSystem = projectFileSystem;
  }

  public String guess() {
    File basedir = projectFileSystem.getBasedir();

    for (File dir = basedir; dir != null; dir = dir.getParentFile()) {
      for (SupportedScm scm : SupportedScm.values()) {
        String folderName = scm.getScmSpecificFilename();
        if (folderName != null) {
          if (new File(basedir, folderName).isDirectory()) {
            return scm.getUrlRoot();
          }
        }
      }
    }

    return null;
  }
}
