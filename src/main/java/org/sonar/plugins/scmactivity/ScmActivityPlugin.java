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

import com.google.common.collect.ImmutableList;
import org.sonar.api.BatchExtension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.List;

@Properties({
  @Property(
    key = ScmActivityPlugin.ENABLED,
    defaultValue = "" + ScmActivityPlugin.ENABLED_DEFAULT,
    name = "Activation of this SCM Activity plugin",
    description = "This property can be set to false in order to deactivate the SCM Activity plugin.",
    module = true,
    project = true,
    global = true
  ),
  @Property(
    key = ScmActivityPlugin.URL,
    defaultValue = "",
    name = "SCM URL",
    description = "The format is described in <a target=\"_blank\" href=\"http://maven.apache.org/scm/scm-url-format.html\">this page</a>. "
      + "Example: <i>scm:svn:https://svn.codehaus.org/sonar-plugins/trunk/scm-activity</i>. "
      + "This setting is not used with Git, Svn, and Hg since the url is discovered automatically.",
    module = true,
    project = true,
    global = false
  ),
  @Property(
    key = ScmActivityPlugin.USER,
    defaultValue = "",
    name = "User",
    description = "Optional user to be used to retrieve blame information from the SCM engine.",
    module = false,
    project = true,
    global = true
  ),
  @Property(
    key = ScmActivityPlugin.PASSWORD,
    defaultValue = "",
    name = "Password",
    description = "Optional password to be used to retrieve blame information from the SCM engine.",
    module = false,
    project = true,
    global = true
  ),
  @Property(
    key = ScmActivityPlugin.THREAD_COUNT,
    defaultValue = "" + ScmActivityPlugin.THREAD_COUNT_DEFAULT,
    name = "Thread count",
    description = "Number of threads used to speed-up the retrieval of authors by line (aka blame information).",
    module = true,
    project = true,
    global = true
  )})
public final class ScmActivityPlugin implements Plugin {
  public static final String ENABLED = "sonar.scm.enabled";
  public static final String URL = "sonar.scm.url";
  public static final String USER = "sonar.scm.user.secured";
  public static final String PASSWORD = "sonar.scm.password.secured";
  public static final String THREAD_COUNT = "sonar.scm.threadCount";
  public static final boolean ENABLED_DEFAULT = true;
  public static final int THREAD_COUNT_DEFAULT = 4;

  public String getKey() {
    return "scm-activity";
  }

  public String getName() {
    return "SCM Activity";
  }

  public String getDescription() {
    return "Collects information from SCM.";
  }

  @SuppressWarnings("unchecked")
  public List<Class<? extends BatchExtension>> getExtensions() {
    return ImmutableList.of(
        Blame.class,
        BlameVersionSelector.class,
        FileToResource.class,
        MavenScmConfiguration.class,
        PreviousSha1Finder.class,
        ScmActivityMetrics.class,
        ScmActivitySensor.class,
        ScmConfiguration.class,
        Sha1Generator.class,
        SonarScmManager.class,
        ScmFacade.class,
        ScmUrlGuess.class,
        UrlChecker.class);
  }
}
