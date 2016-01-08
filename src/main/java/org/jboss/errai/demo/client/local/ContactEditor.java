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
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Templated(value = "contact-page.html#modal-content", stylesheet = "contact-page.css")
public class ContactEditor extends ContactPresenter {

  private Contact copied;

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

  public void copyModelState(final Contact model) {
    final Contact originalModel = getModel();
    binder.setModel(model, InitialState.FROM_MODEL);
    binder.setModel(originalModel, InitialState.FROM_UI);
    copied = model;
  }

  public boolean isCopied() {
    return copied != null;
  }

  public void overwriteCopiedModelState() {
    if (isCopied()) {
      final Contact workingModel = getModel();
      binder.setModel(copied, InitialState.FROM_UI);
      binder.setModel(workingModel);
    }
  }

  public Contact getCopied() {
    return copied;
  }

}
