/*
 * Copyright 2015 Palantir Technologies, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.remoting.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.palantir.remoting.http.errors.SerializableErrorErrorDecoder;
import feign.OptionalAwareDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;

/**
 * Static factory methods for producing common configurations of {@link FeignClientFactory}, which in turn may be used
 * to create HTTP proxies for HTTP remoting clients. The returned instances serialize server-side exceptions as JSON and
 * decode any 204 response as an {@link Optional#absent} in case the proxied interface is of type {@link Optional}.
 */
public final class FeignClients {

    private FeignClients() {}

    /**
     * Provides a {@link FeignClientFactory} with an {@link ObjectMapper} configured with {@link
     * com.fasterxml.jackson.datatype.guava.GuavaModule} and {@link com.fasterxml.jackson.datatype.jdk7.Jdk7Module}.
     */
    public static FeignClientFactory standard() {
        return FeignClientFactory.of(
                new JAXRSContract(),
                new JacksonEncoder(ObjectMappers.guavaJdk7()),
                new OptionalAwareDecoder(new JacksonDecoder(ObjectMappers.guavaJdk7()),
                        SerializableErrorErrorDecoder.INSTANCE),
                SerializableErrorErrorDecoder.INSTANCE,
                FeignClientFactory.okHttpClient());
    }

    /**
     * Provides a {@link FeignClientFactory} with an unmodified {@link ObjectMapper}.
     */
    public static FeignClientFactory vanilla() {
        return FeignClientFactory.of(
                new JAXRSContract(),
                new JacksonEncoder(ObjectMappers.vanilla()),
                new OptionalAwareDecoder(new JacksonDecoder(ObjectMappers.vanilla()),
                        SerializableErrorErrorDecoder.INSTANCE),
                SerializableErrorErrorDecoder.INSTANCE,
                FeignClientFactory.okHttpClient());
    }

    /**
     * Provides a {@link FeignClientFactory} with the specified {@link ObjectMapper}.
     */
    public static FeignClientFactory withMapper(ObjectMapper mapper) {
        return FeignClientFactory.of(
                new JAXRSContract(),
                new JacksonEncoder(mapper),
                new OptionalAwareDecoder(new JacksonDecoder(mapper), SerializableErrorErrorDecoder.INSTANCE),
                SerializableErrorErrorDecoder.INSTANCE,
                FeignClientFactory.okHttpClient());
    }

}
