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
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.databinding.client.api.AbstractBindableListChangeHandler;
import org.jboss.errai.demo.client.shared.Contact;
import org.jboss.errai.demo.client.shared.ContactOperation;
import org.jboss.errai.demo.client.shared.ContactStorageService;
import org.jboss.errai.demo.client.shared.Created;
import org.jboss.errai.demo.client.shared.Deleted;
import org.jboss.errai.demo.client.shared.Updated;
import org.jboss.errai.enterprise.client.jaxrs.api.ResponseCallback;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.slf4j.Logger;

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
 * <p>
 * An Errai UI component for creating, displaying, updating, and deleting {@link Contact Contacts}. This component is
 * also an Errai Navigation {@link Page}; it will be displayed on the GWT host page whenever the navigation URL fragment
 * is {@code #ContactList}.
 *
 * <p>
 * The HTML markup for this {@link Templated} component is the HTML element with the CSS class {@code contact-list} in
 * the file {@code contact-page.html} in this package. This component uses CSS from the file {@code contact-page.css} in
 * this package.
 *
 * <p>
 * The {@link DataField} annotation marks fields that replace HTML elements from the template file. As an example, the
 * field {@link ContactDisplay#editor} replaces the {@code <div>} element in the template with the CSS class
 * {@code modal-content}. Because {@link ContactEditor} is an Errai UI component, the markup for {@link ContactEditor}
 * will replace the contents of the {@code modal-content} div in this component.
 *
 * <p>
 * This component uses a {@link ListWidget} to display a list of {@link Contact Contacts}. The {@code List<Contact>}
 * returned by {@link ListWidget#getValue()} is a model bound to a table of {@link ContactDisplay ContactDisplays} in an
 * HTML table. Any changes to the model list (such as adding or removing items) will be automatically reflected in the
 * displayed table. Because each individual {@link ContactDisplay} uses Errai Data-Binding internally, changes to an
 * individual {@link Contact} will also be reflected in the UI.
 *
 * <p>
 * Instances of this type should be obtained via Errai IoC, either by using {@link Inject} in another container managed
 * bean, or by programmatic lookup through the bean manager.
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

  /**
   * The {@link Table} qualifier gives us a {@link ListWidget} that will display entires as table rows. The default
   * behaviour displays each entry in a {@code <div>}.
   */
  @Inject @Table(root = "tbody")
  @DataField
  private ListWidget<Contact, ContactDisplay> list;

  /**
   * This is a simple interface for calling a remote HTTP service. Behind this interface, Errai has generates an HTTP
   * request to the service defined by {@link ContactStorageService} (a JaxRS service).
   */
  @Inject
  private Caller<ContactStorageService> contactService;

  @Inject
  private ClientMessageBus bus;

  @Inject
  private Logger logger;

  /**
   * Register handlers and populate the list of {@link Contact Contacts}.
   */
  @PostConstruct
  private void setup() {
    /*
     * The ListWidget creates ContactDisplay instances dynamically as Contacts are added to ListWidget#getValue(). This
     * handler registers event handlers on new components as they are created that allow selecting, editting, and
     * deleting Contacts.
     */
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

    /*
     * Triggers an HTTP request to the ContactStorageService. The call back will be invoked asynchronously to display
     * all retrieved contacts.
     */
    contactService.call(new RemoteCallback<List<Contact>>() {
      @Override
      public void callback(final List<Contact> contacts) {
        list.getValue().addAll(contacts);
      }
    }).getAllContacts();
  }

  private boolean sourceIsNotThisClient(final ContactOperation contactOperation) {
    return contactOperation.getSourceQueueSessionId() == null || !contactOperation.getSourceQueueSessionId().equals(bus.getSessionId());
  }

  /**
   * This is called in response to Errai CDI {@link javax.enterprise.event.Event Events} fired from the server when a
   * new {@link Contact} is created. In this way we can display newly created contacts from other browser sessions.
   */
  public void onRemoteCreated(final @Observes @Created ContactOperation contactOperation) {
    if (sourceIsNotThisClient(contactOperation)) {
      list.getValue().add(contactOperation.getContact());
    }
  }

  /**
   * This is called in response to Errai CDI {@link javax.enterprise.event.Event Events} fired from the server when an
   * existing {@link Contact} is updated. In this way we can display new property values for contacts when they are
   * updated from other browser sessions.
   */
  public void onRemoteUpdated(final @Observes @Updated ContactOperation contactOperation) {
    if (sourceIsNotThisClient(contactOperation)) {
      final int indexOf = list.getValue().indexOf(contactOperation.getContact());
      if (indexOf == -1) {
        logger.warn("Received update before creation for " + contactOperation.getContact() + " from " + contactOperation.getSourceQueueSessionId());
        list.getValue().add(contactOperation.getContact());
      } else {
        list.getComponent(indexOf).setModel(contactOperation.getContact());
      }
    }
  }

  /**
   * This is called in response to Errai CDI {@link javax.enterprise.event.Event Events} fired from the server when an
   * existing {@link Contact} is deleted. In this way we can remove displayed contacts when they are deleted in other
   * browser sessions.
   */
  public void onRemoteDelete(final @Observes @Deleted Long id) {
    final Iterator<Contact> contactIter = list.getValue().iterator();
    while (contactIter.hasNext()) {
      if (id.equals(contactIter.next().getId())) {
        contactIter.remove();
        break;
      }
    }
  }

  /**
   * This is an Errai UI native event handler. The element for which this handler is regsitered is in this class's HTML
   * template file and has the {@code new-content} CSS class.
   * <p>
   * Because there is no {@code new-content} {@link DataField} in this class, this method's parameter is a non-specific
   * {@link Event} (rather than a more specific {@link ClickEvent}). For the same reason, the {@link SinkNative}
   * annotation is required to specify which kinds of DOM events this method should handle.
   * <p>
   * This method displays the hidden modal form so that a user can create a new {@link Contact}.
   */
  @SinkNative(Event.ONCLICK)
  @EventHandler("new-contact")
  public void onNewContactClick(final Event event) {
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
    editor.setModelPaused(model);
    displayModal(true);
  }

  /**
   * This is an Errai UI native event handler. The element for which this handler is regsitered is in this class's HTML
   * template file and has the {@code modal-submit} CSS class.
   * <p>
   * Because there is no {@code modal-submit} {@link DataField} in this class, this method's parameter is a non-specific
   * {@link Event} (rather than a more specific {@link ClickEvent}). For the same reason, the {@link SinkNative}
   * annotation is required to specify which kinds of DOM events this method should handle.
   * <p>
   * This method displays and persists changes made to a {@link Contact} in the {@link ContactEditor}, whether it is a
   * newly created or an previously existing {@link Contact}.
   */
  @SinkNative(Event.ONCLICK)
  @EventHandler("modal-submit")
  public void onModalSubmitClick(final Event event) {
    hideModal();
    if (list.getValue().contains(editor.getModel())) {
      updateContactFromEditor();
    }
    else {
      createNewContactFromEditor();
    }
    editor.setModel(new Contact());
  }

  private void createNewContactFromEditor() {
    // Adding this model to the list will create and display a new, bound ContactDisplay in the table.
    list.getValue().add(editor.getModel());
    final ContactDisplay component = list.getComponent(editor.getModel());
    contactService.call(new ResponseCallback() {
      @Override
      public void callback(final Response response) {
        // Set the id if we successfully create this contact.
        if (response.getStatusCode() == Response.SC_CREATED) {
          final String createdUri = response.getHeader("Location");
          final String idString = createdUri.substring(createdUri.lastIndexOf('/')+1);
          final long id = Long.parseLong(idString);
          component.getModel().setId(id);
        }
      }
    }).create(new ContactOperation(editor.getModel(), bus.getSessionId()));
  }

  private void updateContactFromEditor() {
    editor.syncStateFromUI();
    contactService.call().update(new ContactOperation(editor.getModel(), bus.getSessionId()));
  }

  private void hideModal() {
    modal.getStyle().clearDisplay();
  }

  /**
   * This is an Errai UI native event handler. The element for which this handler is regsitered is in this class's HTML
   * template file and has the {@code modal-cancel} CSS class.
   * <p>
   * Because there is no {@code modal-cancel} {@link DataField} in this class, this method's parameter is a non-specific
   * {@link Event} (rather than a more specific {@link ClickEvent}). For the same reason, the {@link SinkNative}
   * annotation is required to specify which kinds of DOM events this method should handle.
   * <p>
   * This method hides the ContactEditor modal form and resets the bound model.
   */
  @SinkNative(Event.ONCLICK)
  @EventHandler("modal-cancel")
  public void onModalCancelClick(final Event event) {
    hideModal();
    editor.setModel(new Contact());
  }

  /**
   * This is an Errai UI native event handler. The element for which this handler is regsitered is in this class's HTML
   * template file and has the {@code modal-delete} CSS class.
   * <p>
   * Because there is a {@code modal-delete} {@link DataField} in this class, this method's parameter indicates that
   * this handles click events by accepting {@link ClickEvent} as its parameter.
   * <p>
   * This method removes a {@link Contact} from the displayed table and makes an HTTP request to delete the contact from
   * persistent storage on the server.
   */
  @EventHandler("modal-delete")
  public void onModalDeleteClick(final ClickEvent event) {
    if (list.getValue().contains(editor.getModel())) {
      if (lastSelected != null && lastSelected.getModel() == editor.getModel()) {
        lastSelected = null;
      }
      final Contact deleted = editor.getModel();
      contactService.call(new ResponseCallback() {
        @Override
        public void callback(final Response response) {
          if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            list.getValue().remove(deleted);
          }
        }
      }).delete(editor.getModel().getId());
      editor.setModel(new Contact());
      hideModal();
    }
  }

  /**
   * A handler for {@link ContactDisplay} instances. This handler marks a display as selected on single-click, and
   * displays the modal form for editting when double-clicked.
   */
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
