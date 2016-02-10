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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.databinding.client.BindableListWrapper;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;
import org.jboss.errai.demo.client.shared.Contact;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.TakesValue;

@Templated("contact-page.html#list")
public class ContactList implements TakesValue<List<Contact>>, BindableListChangeHandler<Contact> {

  @Inject
  private DataBinder<List<Contact>> binder;

  @Inject
  @Named(TableSectionElement.TAG_TBODY)
  @DataField
  private TableSectionElement list;

  @Inject
  private Instance<ContactDisplay> displayFactory;

  private final List<ContactDisplay> displays = new ArrayList<>();

  @PostConstruct
  private void setup() {
    list.removeAllChildren();
    addBindableChangeHandler();
  }

  @Override
  public void setValue(final List<Contact> value) {
    final List<Contact> oldList = binder.getModel();
    binder.setModel(value);
    if (binder.getModel() != oldList) {
      addBindableChangeHandler();
    }
  }

  @Override
  public List<Contact> getValue() {
    return binder.getModel();
  }

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
    }
  }

  @Override
  public void onItemChanged(List<Contact> source, int index, Contact item) {
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

  private BindableListWrapper<Contact> getListAsWrapper() {
    return (BindableListWrapper<Contact>) binder.getModel();
  }

  private void addBindableChangeHandler() {
    final BindableListWrapper<Contact> wrapper = getListAsWrapper();
    wrapper.addChangeHandler(this);
  }
}
