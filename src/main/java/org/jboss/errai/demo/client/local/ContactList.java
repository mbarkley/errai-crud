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

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.databinding.client.api.AbstractBindableListChangeHandler;
import org.jboss.errai.demo.client.shared.Contact;
import org.jboss.errai.demo.client.shared.ContactStorageService;
import org.jboss.errai.enterprise.client.jaxrs.api.ResponseCallback;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.http.client.Response;
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

  @Inject
  @DataField("modal-delete")
  private ButtonElement delete;

  @Inject @Table(root = "tbody")
  @DataField
  private ListWidget<Contact, ContactDisplay> list;

  @Inject
  private Caller<ContactStorageService> contactService;

  @PostConstruct
  private void setup() {
    list.addBindableListChangeHandler(new AbstractBindableListChangeHandler<Contact>() {
      @Override
      public void onItemAdded(final List<Contact> source, final Contact item) {
        final ContactDisplay component = list.getComponent(item);
        final ListComponentHandler handler = new ListComponentHandler(component);
        component.addClickHandler(handler);
        component.addDoubleClickHandler(handler);
      }

      @Override
      public void onItemsAdded(final List<Contact> source, final Collection<? extends Contact> items) {
        for (final Contact item : items) {
          onItemAdded(source, item);
        }
      }
    });

    contactService.call(new RemoteCallback<List<Contact>>() {
      @Override
      public void callback(final List<Contact> contacts) {
        list.getValue().addAll(contacts);
      }
    }).getAllContacts();
  }

  @SinkNative(Event.ONCLICK)
  @EventHandler("new-contact")
  private void onNewContactClick(final Event event) {
    displayModal(false);
  }

  private void displayModal(final boolean withDelete) {
    if (withDelete) {
      delete.getStyle().clearDisplay();
    } else {
      delete.getStyle().setDisplay(Display.NONE);
    }
    modal.getStyle().setDisplay(Display.BLOCK);
  }

  private void editModel(final Contact model) {
    editor.copyModelState(model);
    displayModal(true);
  }

  @SinkNative(Event.ONCLICK)
  @EventHandler("modal-submit")
  private void onModalSubmitClick(final Event event) {
    hideModal();
    if (editor.isCopied()) {
      editor.overwriteCopiedModelState();
      contactService.call().update(editor.getCopied());
    }
    else {
      list.getValue().add(editor.getModel());
      final ContactDisplay component = list.getComponent(editor.getModel());
      contactService.call(new ResponseCallback() {
        @Override
        public void callback(final Response response) {
          if (response.getStatusCode() == Response.SC_CREATED) {
            final String createdUri = response.getHeader("Location");
            final String idString = createdUri.substring(createdUri.lastIndexOf('/')+1);
            final long id = Long.parseLong(idString);
            component.getModel().setId(id);
          }
        }
      }).create(editor.getModel());
    }
    editor.setModel(new Contact());
  }

  private void hideModal() {
    modal.getStyle().clearDisplay();
  }

  @SinkNative(Event.ONCLICK)
  @EventHandler("modal-cancel")
  private void onModalCancelClick(final Event event) {
    hideModal();
    editor.setModel(new Contact());
  }

  @EventHandler("modal-delete")
  private void onModalDeleteClick(final ClickEvent event) {
    if (editor.isCopied()) {
      if (lastSelected != null && lastSelected.getModel() == editor.getCopied()) {
        lastSelected = null;
      }
      final Contact deleted = editor.getCopied();
      contactService.call(new ResponseCallback() {
        @Override
        public void callback(final Response response) {
          if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            list.getValue().remove(deleted);
          }
        }
      }).delete(editor.getCopied().getId());
      editor.setModel(new Contact());
      hideModal();
    }
  }

  private class ListComponentHandler implements ClickHandler, DoubleClickHandler {

    private final ContactDisplay component;

    private ListComponentHandler(final ContactDisplay component) {
      this.component = component;
    }

    @Override
    public void onClick(final ClickEvent event) {
      selectComponent();
    }

    private void selectComponent() {
      if (lastSelected != null && lastSelected != component) {
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
