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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Templated(value = "contact-page.html#contact", stylesheet = "contact-page.css")
public class ContactDisplay extends ContactPresenter {

  private final List<ClickHandler> clickHandlers = new ArrayList<ClickHandler>();
  private final List<DoubleClickHandler> doubleClickHandlers = new ArrayList<DoubleClickHandler>();

  @Inject
  @DataField
  private TableRowElement contact;

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
  @Bound(converter = DateConverter.class) @DataField
  private TableCellElement birthday;

  @Inject @Named("td")
  @Bound @DataField
  private TableCellElement notes;

  public HandlerRegistration addClickHandler(final ClickHandler handler) {
    clickHandlers.add(handler);

    return new HandlerRegistration() {
      @Override
      public void removeHandler() {
        clickHandlers.remove(handler);
      }
    };
  }

  public HandlerRegistration addDoubleClickHandler(final DoubleClickHandler handler) {
    doubleClickHandlers.add(handler);

    return new HandlerRegistration() {
      @Override
      public void removeHandler() {
        doubleClickHandlers.remove(handler);
      }
    };
  }

  @EventHandler("contact")
  private void onClick(final ClickEvent event) {
    for (final ClickHandler handler : clickHandlers) {
      handler.onClick(event);
    }
  }

  @EventHandler("contact")
  private void onDoubleClick(final DoubleClickEvent event) {
    for (final DoubleClickHandler handler : doubleClickHandlers) {
      handler.onDoubleClick(event);
    }
  }

  public void setSelected(final boolean selected) {
    if (selected) {
      contact.addClassName("selected");
    } else {
      contact.removeClassName("selected");
    }
  }

}
