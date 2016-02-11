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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.function.Optional;
import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;
import org.jboss.errai.demo.client.shared.Contact;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * <p>
 * An Errai UI component for displaying a list of {@link Contact Contacts}. The HTML markup for this {@link Templated}
 * component is the HTML element with the CSS class {@code contact-list} in the file {@code contact-page.html} in this
 * package.
 *
 * <p>
 * The {@link DataField} annotation marks fields that replace HTML elements from the template file. As an example, the
 * field {@link ContactDisplay#editor} replaces the {@code <div>} element in the template with the CSS class
 * {@code modal-content}. Because {@link ContactEditor} is an Errai UI component, the markup for {@link ContactEditor}
 * will replace the contents of the {@code modal-content} div in this component.
 *
 * <p>
 * This class implements {@link BindableListChangeHandler} so it can be bound to a list of {@link Contact Contacts}. See
 * {@link ContactListPage} to see a {@link ContactList} bound with declarative data-binding.
 *
 * <p>
 * Instances of this type should be obtained via Errai IoC, either by using {@link Inject} in another container managed
 * bean, or by programmatic lookup through the bean manager.
 */
@Templated("contact-page.html#list")
public class ContactList implements BindableListChangeHandler<Contact> {

  private Optional<ContactDisplay> selected = Optional.empty();

  @Inject
  @Named(TableSectionElement.TAG_TBODY)
  @DataField
  private TableSectionElement list;

  @Inject
  private Instance<ContactDisplay> displayFactory;

  private final List<ContactDisplay> displays = new ArrayList<>();

  @PostConstruct
  private void setup() {
    // Remove the place-holder table row in the template
    list.removeAllChildren();
  }

  /**
   * This method observes CDI events fired locally by {@link ContactDisplay#onClick(ClickEvent)} in order to highlight a
   * {@link ContactDisplay} when it is clicked.
   */
  public void selectComponent(final @Observes @Click ContactDisplay component) {
    selected.filter(s -> s != component).ifPresent(s -> s.setSelected(false));
    component.setSelected(true);
    selected = Optional.ofNullable(component);
  }

  /*
   * The BindableListChangeHandler defines default implementations for most of the handler methods. The following three
   * methods are the only abstract methods that must be implemented.
   */

  @Override
  public void onItemsAddedAt(final List<Contact> source, final int index, final Collection<? extends Contact> items) {
    int i = index;
    for (final Contact model : items) {
      final ContactDisplay display = createComponent(model);
      displays.add(i, display);
      insertAtIndex(i, display);
      i++;
    }
  }

  @Override
  public void onItemsRemovedAt(final List<Contact> source, final List<Integer> indexes) {
    final List<Integer> reverseSorted = new ArrayList<>(indexes);
    Collections.sort(reverseSorted, (n, m) -> m - n);
    for (final int i : indexes) {
      final ContactDisplay removed = displays.remove(i);
      removed.getRoot().removeFromParent();
      // In case we are removing the selected display.
      selected.filter(s -> s.getModel() == removed.getModel())
                  .ifPresent(s -> selected = Optional.empty());
    }
  }

  @Override
  public void onItemChanged(final List<Contact> source, final int index, final Contact item) {
    displays.get(index).setModel(item);
  }

  private ContactDisplay createComponent(final Contact contact) {
    final ContactDisplay display = displayFactory.get();
    display.setModel(contact);

    return display;
  }

  private void insertAtIndex(final int index, final ContactDisplay display) {
    if (index == 0) {
      list.insertFirst(display.getRoot());
    }
    else {
      list.insertAfter(display.getRoot(), displays.get(index-1).getRoot());
    }
  }
}
