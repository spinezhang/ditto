/*
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.services.gateway.endpoints.routes.policies;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.policies.SubjectId;
import org.eclipse.ditto.services.gateway.util.config.security.DefaultOAuthConfig;
import org.junit.Test;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

/**
 * Unit tests for {@link OAuthTokenIntegrationSubjectIdFactory}.
 */
public class OAuthTokenIntegrationSubjectIdFactoryTest {

    @Test
    public void resolveSubjectId() {
        final String subjectPattern = "{{jwt:iss}}:static-part:{{jwt:sub}}:{{header:owner}}";
        final OAuthTokenIntegrationSubjectIdFactory sut = createSut(subjectPattern);
        final DittoHeaders dittoHeaders = DittoHeaders.newBuilder()
                .putHeader("owner", "Ditto")
                .build();
        final Set<SubjectId> subjectIds = sut.getSubjectIds(dittoHeaders, new DummyJwt());
        Assertions.assertThat(subjectIds).hasSize(1);
        final SubjectId subjectId = subjectIds.stream().findFirst().orElseThrow();
        Assertions.assertThat(subjectId.getIssuer()).hasToString("dummy-issuer");
        Assertions.assertThat(subjectId).hasToString("dummy-issuer:static-part:dummy-subject:Ditto");
    }

    @Test
    public void resolveSubjectIdWithUnresolvedPlaceholder() {
        final String subjectPattern = "{{jwt:iss}}:{{policy-entry:label}}:{{jwt:sub}}:{{header:my-custom-header}}";
        final OAuthTokenIntegrationSubjectIdFactory sut = createSut(subjectPattern);
        final DittoHeaders dittoHeaders = DittoHeaders.newBuilder()
                .putHeader("my-custom-header", "foo")
                .build();
        final Set<SubjectId> subjectIds = sut.getSubjectIds(dittoHeaders, new DummyJwt());
        Assertions.assertThat(subjectIds).hasSize(1);
        final SubjectId subjectId = subjectIds.stream().findFirst().orElseThrow();
        Assertions.assertThat(subjectId).hasToString("dummy-issuer:{{policy-entry:label}}:dummy-subject:foo");
    }

    @Test
    public void resolveSubjectIdWithJsonArrayJwtClaim() {
        final String subjectPattern = "integration:{{jwt:aud}}:static";
        final OAuthTokenIntegrationSubjectIdFactory sut = createSut(subjectPattern);
        final DittoHeaders dittoHeaders = DittoHeaders.newBuilder().build();
        final Set<SubjectId> subjectIds = sut.getSubjectIds(dittoHeaders, new DummyJwt());
        Assertions.assertThat(subjectIds).hasSize(2).containsExactly(
                SubjectId.newInstance("integration:aud-1:static"),
                SubjectId.newInstance("integration:aud-2:static")
        );
    }

    @Test
    public void resolveSubjectIdWithMultipleJsonArrayJwtClaims() {
        final String subjectPattern = "{{jwt:iss}}:{{jwt:aud}}:static:{{jwt:foo}}";
        final OAuthTokenIntegrationSubjectIdFactory sut = createSut(subjectPattern);
        final DittoHeaders dittoHeaders = DittoHeaders.newBuilder().build();
        final Set<SubjectId> subjectIds = sut.getSubjectIds(dittoHeaders, new DummyJwt());
        Assertions.assertThat(subjectIds).hasSize(6).containsExactly(
                SubjectId.newInstance("dummy-issuer:aud-1:static:bar1"),
                SubjectId.newInstance("dummy-issuer:aud-2:static:bar1"),
                SubjectId.newInstance("dummy-issuer:aud-1:static:bar2"),
                SubjectId.newInstance("dummy-issuer:aud-2:static:bar2"),
                SubjectId.newInstance("dummy-issuer:aud-1:static:bar3"),
                SubjectId.newInstance("dummy-issuer:aud-2:static:bar3")
        );
    }

    private static OAuthTokenIntegrationSubjectIdFactory createSut(final String subjectPattern) {
        final DefaultOAuthConfig oAuthConfig = DefaultOAuthConfig.of(
                ConfigFactory.empty().withValue("oauth.token-integration-subject",
                        ConfigValueFactory.fromAnyRef(subjectPattern)));
        return OAuthTokenIntegrationSubjectIdFactory.of(oAuthConfig);
    }
}