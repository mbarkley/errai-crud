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

package org.jboss.errai.demo.server;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.jboss.errai.demo.client.shared.Contact;
import org.jboss.errai.demo.client.shared.ContactStorageService;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Stateless
public class ContactStorageServiceImpl implements ContactStorageService {

  @Inject
  private ContactEntityService entityService;

  @Override
  public List<Contact> getAllContacts() {
    return entityService.getAllContacts();
  }

  @Override
  public Response create(final Contact contact) {
    entityService.create(contact);

    return Response
            .created(UriBuilder.fromResource(ContactStorageService.class).path(String.valueOf(contact.getId())).build())
            .build();
  }

  @Override
  public Response update(final Contact contact) {
    entityService.update(contact);

    return Response.noContent().build();
  }

  @Override
  public Response delete(Long id) {
    entityService.delete(id);

    return Response.noContent().build();
  }

}