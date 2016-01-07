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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.AbstractBindableListChangeHandler;
import org.jboss.errai.demo.client.shared.Contact;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Event;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Page(role = DefaultPage.class)
@Templated(value = "contact-page.html#contact-list", stylesheet = "contact-page.css")
public class ContactList {

  private ContactDisplay lastSelected;

  @Inject
  @DataField
  private DivElement modal;

  @Inject
  @DataField("modal-content")
  private ContactEditor editor;

  @Inject @Table(root = "tbody")
  @DataField
  private ListWidget<Contact, ContactDisplay> list;

  @PostConstruct
  private void setup() {
    list.addBindableListChangeHandler(new AbstractBindableListChangeHandler<Contact>() {
      @Override
      public void onItemAdded(final List<Contact> source, final Contact item) {
        final ContactDisplay component = list.getComponent(item);
        final SelectionHandler handler = new SelectionHandler(component);
        component.addClickHandler(handler);
        component.addDoubleClickHandler(handler);
      }
    });
  }

  @SinkNative(Event.ONCLICK)
  @EventHandler("new-contact")
  private void onNewContactClick(final Event event) {
    displayModal();
  }

  private void displayModal() {
    modal.getStyle().setDisplay(Display.BLOCK);
  }

  private void editModel(final Contact model) {
    editor.setModel(model);
    displayModal();
  }

  @SinkNative(Event.ONCLICK)
  @EventHandler("modal-button")
  private void onModalSubmitClick(final Event event) {
    modal.getStyle().clearDisplay();
    if (!list.getValue().contains(editor.getModel())) {
      list.getValue().add(editor.getModel());
    }
    editor.setModel(new Contact());
  }

  private class SelectionHandler implements ClickHandler, DoubleClickHandler {

    private final ContactDisplay component;

    private SelectionHandler(final ContactDisplay component) {
      this.component = component;
    }

    @Override
    public void onClick(final ClickEvent event) {
      selectComponent();
    }

    private void selectComponent() {
      if (lastSelected != null) {
        lastSelected.setSelected(false);
      }
      component.setSelected(true);
      lastSelected = component;
    }

    @Override
    public void onDoubleClick(final DoubleClickEvent event) {
      selectComponent();
      editModel(component.getModel());
    }

  }

}
