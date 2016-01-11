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

import org.jboss.errai.databinding.client.api.InitialState;
import org.jboss.errai.demo.client.shared.Contact;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.TextAreaElement;

/**
 * <p>
 * An Errai UI component for creating and editing a single {@link Contact}. This component can be bound to a
 * {@link Contact} by calling {@link #setModel(Contact)}. It can also copy the state of a {@link Contact} without
 * binding to it (and then later overwrite the state of the copied {@link Contact}).
 *
 * <p>
 * The HTML markup for this {@link Templated} component is the HTML element with the CSS class {@code modal-content} in
 * the file {@code contact-page.html} in this package. This component uses CSS from the file {@code contact-page.css} in
 * this package.
 *
 * <p>
 * The {@link DataField} annotation marks fields that replace HTML elements from the template file. As an example, the
 * field {@link ContactDisplay#root} is the root {@code <div>} element of this component; it can be used to attach this
 * component to the DOM.
 *
 * <p>
 * The {@link Bound} annotations mark UI fields with values that Errai Data-Binding keeps synchronized with properties
 * in the bound {@link Contact} model instance. (See the base class, {@link ContactPresenter}.)
 *
 * <p>
 * Instances of this type should be obtained via Errai IoC, either by using {@link Inject} in another container managed
 * bean, or by programmatic lookup through the bean manager.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Templated(value = "contact-page.html#modal-content", stylesheet = "contact-page.css")
public class ContactEditor extends ContactPresenter {

  private Contact copied;

  /**
   * The {@link DataField} annotation for this field declares that this {@link DivElement} is the element from the
   * template file with the CSS class {@code modal-content}. Because of the fragment {@code #modal-content} in the
   * {@link Templated#value()} on this class, this is the root element of this template.
   */
  @Inject
  @DataField("modal-content")
  private DivElement root;

  @Inject
  @Bound @DataField
  private InputElement fullname;

  @Inject
  @Bound @DataField
  private InputElement nickname;

  @Inject
  @Bound @DataField
  private InputElement phonenumber;

  @Inject
  @Bound @DataField
  private InputElement email;

  @Inject
  @Bound @DataField
  private TextAreaElement notes;

  /*
   * We specify a converter because Errai does not provide built-in conversion from String to Date.
   */
  @Inject
  @Bound(converter = DateConverter.class) @DataField
  private InputElement birthday;

  public DivElement getRootElement() {
    return root;
  }

  @Override
  public void setModel(Contact model) {
    copied = null;
    super.setModel(model);
  }

  /**
   * Copies the state of the given model, but does not remain bound to this model after the method returns. This
   * {@link ContactEditor} retains an instance to the copied model so that it's state can be overwritten with subsequent
   * calls to {@link #overwriteCopiedModelState()}.
   */
  public void copyModelState(final Contact model) {
    final Contact originalModel = getModel();
    binder.setModel(model, InitialState.FROM_MODEL);
    binder.setModel(originalModel, InitialState.FROM_UI);
    copied = model;
  }

  /**
   * True if no calls to {@link #setModel(Contact)} have happened since the last call to
   * {@link #copyModelState(Contact)}.
   */
  public boolean isCopied() {
    return copied != null;
  }

  /**
   * If {@link #isCopied()} is true, overwrite the state of the model returned by {@link #getCopied()} with the state of
   * the model returned by {@link #getModel()}.
   */
  public void overwriteCopiedModelState() {
    if (isCopied()) {
      final Contact workingModel = getModel();
      binder.setModel(copied, InitialState.FROM_UI);
      binder.setModel(workingModel);
    }
  }

  /**
   * @return If no calls to {@link #setModel(Contact)} have happened since the last call to
   *         {@link #copyModelState(Contact)}, return the parameter of the last call to {@link #copyModelState(Contact)}
   *         . Otherwise return <code>null</code>.
   */
  public Contact getCopied() {
    return copied;
  }

}
