/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.internal;

import java.util.Set;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.set.mutable.SetAdapter;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * Set algebra operations.
 * <p>
 * Most operations are non-destructive, i.e. no input sets are modified during execution.
 * The exception is operations ending in "Into." These accept the target collection of
 * the final calculation as the first parameter.
 */
public final class SetIterables
{
    private SetIterables()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <E> MutableSet<E> union(
            SetIterable<? extends E> setA,
            SetIterable<? extends E> setB)
    {
        return SetIterables.unionInto(setA, setB, UnifiedSet.<E>newSet());
    }

    public static <E, R extends Set<E>> R unionInto(
            SetIterable<? extends E> setA,
            SetIterable<? extends E> setB,
            R targetSet)
    {
        Iterate.addAllIterable(setA, targetSet);
        Iterate.addAllIterable(setB, targetSet);
        return targetSet;
    }

    public static <E> MutableSet<E> intersect(
            SetIterable<? extends E> setA,
            SetIterable<? extends E> setB)
    {
        return SetIterables.intersectInto(setA, setB, UnifiedSet.<E>newSet());
    }

    public static <E, R extends Set<E>> R intersectInto(
            SetIterable<? extends E> setA,
            SetIterable<? extends E> setB,
            R targetSet)
    {
        MutableSet<E> adapted = SetAdapter.adapt(targetSet);
        adapted.addAllIterable(setA);
        adapted.retainAllIterable(setB);
        return targetSet;
    }

    public static <E> MutableSet<E> difference(
            SetIterable<? extends E> minuendSet,
            SetIterable<? extends E> subtrahendSet)
    {
        return SetIterables.differenceInto(minuendSet, subtrahendSet, UnifiedSet.<E>newSet());
    }

    public static <E, R extends Set<E>> R differenceInto(
            SetIterable<? extends E> minuendSet,
            SetIterable<? extends E> subtrahendSet,
            R targetSet)
    {
        MutableSet<E> adapted = SetAdapter.adapt(targetSet);
        adapted.addAllIterable(minuendSet);
        adapted.removeAllIterable(subtrahendSet);
        return targetSet;
    }

    public static <E> MutableSet<E> symmetricDifference(
            SetIterable<? extends E> setA,
            SetIterable<? extends E> setB)
    {
        return SetIterables.symmetricDifferenceInto(setA, setB, UnifiedSet.<E>newSet());
    }

    public static <E, R extends Set<E>> R symmetricDifferenceInto(
            SetIterable<? extends E> setA,
            SetIterable<? extends E> setB,
            R targetSet)
    {
        return SetIterables.unionInto(
                SetIterables.difference(setA, setB),
                SetIterables.difference(setB, setA),
                targetSet);
    }

    public static <E> boolean isSubsetOf(
            SetIterable<? extends E> candidateSubset,
            SetIterable<? extends E> candidateSuperset)
    {
        return candidateSubset.size() <= candidateSuperset.size()
                && candidateSuperset.containsAllIterable(candidateSubset);
    }

    public static <E> boolean isProperSubsetOf(
            SetIterable<? extends E> candidateSubset,
            SetIterable<? extends E> candidateSuperset)
    {
        return candidateSubset.size() < candidateSuperset.size()
                && candidateSuperset.containsAllIterable(candidateSubset);
    }

    public static <T> MutableSet<MutableSet<T>> powerSet(Set<T> set)
    {
        MutableSet<MutableSet<T>> seed = UnifiedSet.<MutableSet<T>>newSetWith(UnifiedSet.<T>newSet());
        return powerSetWithSeed(set, seed);
    }

    public static <T> MutableSet<MutableSet<T>> powerSet(UnifiedSetWithHashingStrategy<T> set)
    {
        MutableSet<MutableSet<T>> seed = UnifiedSet.<MutableSet<T>>newSetWith(set.newEmpty());
        return powerSetWithSeed(set, seed);
    }

    private static <T> MutableSet<MutableSet<T>> powerSetWithSeed(Set<T> set, MutableSet<MutableSet<T>> seed)
    {
        return Iterate.injectInto(seed, set, new Function2<MutableSet<MutableSet<T>>, T, MutableSet<MutableSet<T>>>()
        {
            public MutableSet<MutableSet<T>> value(MutableSet<MutableSet<T>> accumulator, final T element)
            {
                return SetIterables.union(accumulator, accumulator.collect(new Function<MutableSet<T>, MutableSet<T>>()
                {
                    public MutableSet<T> valueOf(MutableSet<T> innerSet)
                    {
                        return innerSet.clone().with(element);
                    }
                }));
            }
        });
    }

    /**
     * Returns an Immutable version of powerset where the inner sets are also immutable.
     */
    public static <T> ImmutableSet<ImmutableSet<T>> immutablePowerSet(Set<T> set)
    {
        return powerSet(set).collect(new Function<MutableSet<T>, ImmutableSet<T>>()
        {
            public ImmutableSet<T> valueOf(MutableSet<T> set)
            {
                return set.toImmutable();
            }
        }).toImmutable();
    }

    public static <A, B> LazyIterable<Pair<A, B>> cartesianProduct(SetIterable<A> set1, final SetIterable<B> set2)
    {
        return LazyIterate.flatCollect(set1, new Function<A, LazyIterable<Pair<A, B>>>()
        {
            public LazyIterable<Pair<A, B>> valueOf(final A first)
            {
                return LazyIterate.collect(set2, new Function<B, Pair<A, B>>()
                {
                    public Pair<A, B> valueOf(B second)
                    {
                        return Tuples.pair(first, second);
                    }
                });
            }
        });
    }
}