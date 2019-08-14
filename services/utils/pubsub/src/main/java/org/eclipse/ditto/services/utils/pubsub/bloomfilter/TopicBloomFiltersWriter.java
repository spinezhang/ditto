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

import java.util.concurrent.CompletionStage;

import akka.actor.ActorRef;
import akka.cluster.ddata.Replicator;
import akka.util.ByteString;

/**
 * Writer of distributed Bloom filters of subscribed topics.
 */
public interface TopicBloomFiltersWriter {

    /**
     * Update the topics this cluster member subscribes to.
     *
     * @param ownSubscriber actor that manages local subscriptions for this cluster member.
     * @param ownBloomFilter Bloom filter of local subscriptions.
     * @param writeConsistency write consistency for the operation.
     * @return future that completes or fails according to the result of the operation.
     */
    CompletionStage<Void> updateOwnTopics(ActorRef ownSubscriber, ByteString ownBloomFilter,
            Replicator.WriteConsistency writeConsistency);

    /**
     * Remove a subscriber outright.
     *
     * @param subscriber the subscriber to remove.
     * @param writeConsistency write consistency for the operation.
     * @return future that completes or fails according to the result of the operation.
     */
    CompletionStage<Void> removeSubscriber(final ActorRef subscriber,
            final Replicator.WriteConsistency writeConsistency);
}
