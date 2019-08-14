/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.services.utils.pubsub.bloomfilter;

import java.util.Collection;
import java.util.concurrent.CompletionStage;

import akka.actor.ActorRef;

/**
 * Reader of distributed Bloom filters of subscribed topics.
 */
public interface TopicBloomFiltersReader {

    /**
     * Get subscribers from a list of topic hashes.
     *
     * @param topicHashes the hash codes of each topic.
     * @return future collection of subscribers whose Bloom filter contains all hashes of 1 or more topics.
     */
    CompletionStage<Collection<ActorRef>> getSubscribers(Collection<? extends Collection<Integer>> topicHashes);

}
