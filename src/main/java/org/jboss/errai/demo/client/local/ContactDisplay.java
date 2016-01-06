/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.demo.client.local;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.dom.client.TableCellElement;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Templated(value = "contact-page.html#contact", stylesheet = "contact-page.css")
public class ContactDisplay extends ContactPresenter {

  @Inject @Named("td")
  @Bound @DataField
  private TableCellElement fullname;

  @Inject @Named("td")
  @Bound @DataField
  private TableCellElement nickname;

  @Inject @Named("td")
  @Bound @DataField
  private TableCellElement phonenumber;

  @Inject @Named("td")
  @Bound @DataField
  private TableCellElement email;

  @Inject @Named("td")
  @Bound @DataField
  private TableCellElement birthday;

}
