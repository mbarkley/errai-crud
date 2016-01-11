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

package org.jboss.errai.demo.client.shared;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Bindable
@Portable
@Entity
@NamedQueries({
  @NamedQuery(name = Contact.ALL_CONTACTS_QUERY, query = "SELECT c FROM Contact c ORDER BY c.id")
})
public class Contact {

  public static final String ALL_CONTACTS_QUERY = "allContacts";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  private String fullname;

  private String nickname;

  private String phonenumber;

  private String email;

  private Date birthday;

  private String notes;

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getPhonenumber() {
    return phonenumber;
  }

  public void setPhonenumber(String phonenumber) {
    this.phonenumber = phonenumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("[Contact: id=")
           .append(id)
           .append(", nickname=")
           .append(nickname)
           .append(", fullname=")
           .append(fullname)
           .append(", phonenumber=")
           .append(phonenumber)
           .append(", email=")
           .append(email)
           .append(", birthday=")
           .append(DateTimeFormat.getFormat(PredefinedFormat.ISO_8601).format(birthday).substring(0, 10))
           .append(", notes=\"")
           .append(notes.substring(0, 20) + (notes.length() > 20 ? "..." : ""));

    return builder.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    return (obj instanceof Contact) && ((Contact) obj).getId() != 0 && ((Contact) obj).getId() == getId();
  }

  @Override
  public int hashCode() {
    return (int) getId();
  }

}
