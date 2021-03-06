/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collector;

import java.util.Comparator;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.ImmutableBiMap;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.stack.ImmutableStack;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.BiMaps;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.SortedBags;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.factory.Stacks;

/**
 * A set of Collectors for Eclipse Collections types and algorithms.
 * Includes converters to{Immutable}{Sorted}{List/Set/Bag/Map/BiMap/Multimap}.
 *
 * @since 8.0
 */
public final class Collectors2
{
    private static final Collector.Characteristics[] EMPTY_CHARACTERISTICS = {};
    private static final Collector<?, ?, String> DEFAULT_MAKE_STRING = Collectors2.makeString(", ");

    private Collectors2()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <T> Collector<T, ?, String> makeString()
    {
        return (Collector<T, ?, String>) DEFAULT_MAKE_STRING;
    }

    public static <T> Collector<T, ?, String> makeString(CharSequence separator)
    {
        return Collectors2.makeString("", separator, "");
    }

    public static <T> Collector<T, ?, String> makeString(CharSequence start, CharSequence separator, CharSequence end)
    {
        return Collector.of(
                () -> new StringJoiner(separator, start, end),
                (joiner, each) -> joiner.add(String.valueOf(each)),
                StringJoiner::merge,
                StringJoiner::toString,
                EMPTY_CHARACTERISTICS);
    }

    public static <T> Collector<T, ?, MutableList<T>> toList()
    {
        return Collector.of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                EMPTY_CHARACTERISTICS);
    }

    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList()
    {
        return Collector.<T, MutableList<T>, ImmutableList<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                MutableList::toImmutable,
                EMPTY_CHARACTERISTICS);
    }

    public static <T> Collector<T, ?, MutableSet<T>> toSet()
    {
        return Collector.of(
                Sets.mutable::empty,
                MutableSet::add,
                MutableSet::withAll,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet()
    {
        return Collector.<T, MutableSet<T>, ImmutableSet<T>>of(
                Sets.mutable::empty,
                MutableSet::add,
                MutableSet::withAll,
                MutableSet::toImmutable,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, MutableSortedSet<T>> toSortedSet()
    {
        return Collector.of(
                SortedSets.mutable::empty,
                MutableSortedSet::add,
                MutableSortedSet::withAll,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, ImmutableSortedSet<T>> toImmutableSortedSet()
    {
        return Collector.<T, MutableSortedSet<T>, ImmutableSortedSet<T>>of(
                SortedSets.mutable::empty,
                MutableSortedSet::add,
                MutableSortedSet::withAll,
                MutableSortedSet::toImmutable,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, MutableSortedSet<T>> toSortedSet(Comparator<? super T> comparator)
    {
        return Collector.of(
                () -> SortedSets.mutable.with(comparator),
                MutableSortedSet::add,
                MutableSortedSet::withAll,
                Collector.Characteristics.UNORDERED);
    }

    public static <T, V extends Comparable<? super V>> Collector<T, ?, MutableSortedSet<T>> toSortedSetBy(Function<? super T, ? extends V> function)
    {
        return Collectors2.toSortedSet(Comparators.byFunction(function));
    }

    public static <T> Collector<T, ?, ImmutableSortedSet<T>> toImmutableSortedSet(Comparator<? super T> comparator)
    {
        return Collector.<T, MutableSortedSet<T>, ImmutableSortedSet<T>>of(
                () -> SortedSets.mutable.with(comparator),
                MutableSortedSet::add,
                MutableSortedSet::withAll,
                MutableSortedSet::toImmutable,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, MutableBag<T>> toBag()
    {
        return Collector.of(
                Bags.mutable::empty,
                MutableBag::add,
                MutableBag::withAll,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, ImmutableBag<T>> toImmutableBag()
    {
        return Collector.<T, MutableBag<T>, ImmutableBag<T>>of(
                Bags.mutable::empty,
                MutableBag::add,
                MutableBag::withAll,
                MutableBag::toImmutable,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, MutableList<T>> toSortedList()
    {
        return Collector.<T, MutableList<T>, MutableList<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                MutableList::sortThis,
                EMPTY_CHARACTERISTICS);
    }

    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableSortedList()
    {
        return Collector.<T, MutableList<T>, ImmutableList<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                list -> list.sortThis().toImmutable(),
                EMPTY_CHARACTERISTICS);
    }

    public static <T> Collector<T, ?, MutableList<T>> toSortedList(Comparator<? super T> comparator)
    {
        return Collector.<T, MutableList<T>, MutableList<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                list -> list.sortThis(comparator),
                EMPTY_CHARACTERISTICS);
    }

    public static <T, V extends Comparable<? super V>> Collector<T, ?, MutableList<T>> toSortedListBy(Function<? super T, ? extends V> function)
    {
        return Collectors2.toSortedList(Comparators.byFunction(function));
    }

    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableSortedList(Comparator<? super T> comparator)
    {
        return Collector.<T, MutableList<T>, ImmutableList<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                list -> list.sortThis(comparator).toImmutable(),
                EMPTY_CHARACTERISTICS);
    }

    public static <T> Collector<T, ?, MutableSortedBag<T>> toSortedBag()
    {
        return Collector.of(
                SortedBags.mutable::empty,
                MutableSortedBag::add,
                MutableSortedBag::withAll,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, ImmutableSortedBag<T>> toImmutableSortedBag()
    {
        return Collector.<T, MutableSortedBag<T>, ImmutableSortedBag<T>>of(
                SortedBags.mutable::empty,
                MutableSortedBag::add,
                MutableSortedBag::withAll,
                MutableSortedBag::toImmutable,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, MutableSortedBag<T>> toSortedBag(Comparator<? super T> comparator)
    {
        return Collector.of(
                () -> SortedBags.mutable.with(comparator),
                MutableSortedBag::add,
                MutableSortedBag::withAll,
                Collector.Characteristics.UNORDERED);
    }

    public static <T, V extends Comparable<? super V>> Collector<T, ?, MutableSortedBag<T>> toSortedBagBy(Function<? super T, ? extends V> function)
    {
        return Collectors2.toSortedBag(Comparators.byFunction(function));
    }

    public static <T> Collector<T, ?, ImmutableSortedBag<T>> toImmutableSortedBag(Comparator<? super T> comparator)
    {
        return Collector.<T, MutableSortedBag<T>, ImmutableSortedBag<T>>of(
                () -> SortedBags.mutable.with(comparator),
                MutableSortedBag::add,
                MutableSortedBag::withAll,
                MutableSortedBag::toImmutable,
                Collector.Characteristics.UNORDERED);
    }

    public static <T> Collector<T, ?, MutableStack<T>> toStack()
    {
        return Collector.<T, MutableList<T>, MutableStack<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                Stacks.mutable::ofAll,
                EMPTY_CHARACTERISTICS);
    }

    public static <T> Collector<T, ?, ImmutableStack<T>> toImmutableStack()
    {
        return Collector.<T, MutableList<T>, ImmutableStack<T>>of(
                Lists.mutable::empty,
                MutableList::add,
                MutableList::withAll,
                Stacks.immutable::ofAll,
                EMPTY_CHARACTERISTICS);
    }

    public static <T, K, V> Collector<T, ?, MutableBiMap<K, V>> toBiMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.of(
                BiMaps.mutable::empty,
                (mbm, each) -> mbm.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    public static <T, K, V> Collector<T, ?, ImmutableBiMap<K, V>> toImmutableBiMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.<T, MutableBiMap<K, V>, ImmutableBiMap<K, V>>of(
                BiMaps.mutable::empty,
                (mbm, each) -> mbm.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                MutableBiMap::toImmutable,
                EMPTY_CHARACTERISTICS);
    }

    public static <T, K, V> Collector<T, ?, MutableMap<K, V>> toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.of(
                Maps.mutable::empty,
                (map, each) -> map.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collector.<T, MutableMap<K, V>, ImmutableMap<K, V>>of(
                Maps.mutable::empty,
                (map, each) -> map.put(keyFunction.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                MutableMap::toImmutable,
                EMPTY_CHARACTERISTICS);
    }

    private static <T, K, R extends MutableMultimap<K, T>> Collector<T, ?, R> groupBy(
            Function<? super T, ? extends K> groupBy,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (map, each) -> map.put(groupBy.valueOf(each), each),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    private static <T, K, A extends MutableMultimap<K, T>, R extends ImmutableMultimap<K, T>> Collector<T, ?, R> groupByImmutable(
            Function<? super T, ? extends K> groupBy,
            Supplier<A> supplier,
            java.util.function.Function<A, R> finisher)
    {
        return Collector.of(
                supplier,
                (map, each) -> map.put(groupBy.valueOf(each), each),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                finisher,
                EMPTY_CHARACTERISTICS);
    }

    private static <T, K, V, R extends MutableMultimap<K, V>> Collector<T, ?, R> groupBy(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction,
            Supplier<R> supplier)
    {
        return Collector.of(
                supplier,
                (map, each) -> map.put(groupBy.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                EMPTY_CHARACTERISTICS);
    }

    private static <T, K, V, A extends MutableMultimap<K, V>, R extends ImmutableMultimap<K, V>> Collector<T, ?, R> groupByImmutable(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction,
            Supplier<A> supplier,
            java.util.function.Function<A, R> finisher)
    {
        return Collector.of(
                supplier,
                (map, each) -> map.put(groupBy.valueOf(each), valueFunction.valueOf(each)),
                (r1, r2) ->
                {
                    r1.putAll(r2);
                    return r1;
                },
                finisher,
                EMPTY_CHARACTERISTICS);
    }

    public static <T, K> Collector<T, ?, MutableListMultimap<K, T>> toListMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupBy(groupBy, Multimaps.mutable.list::empty);
    }

    public static <T, K, V> Collector<T, ?, MutableListMultimap<K, V>> toListMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupBy(groupBy, valueFunction, Multimaps.mutable.list::empty);
    }

    public static <T, K> Collector<T, ?, MutableSetMultimap<K, T>> toSetMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupBy(groupBy, Multimaps.mutable.set::empty);
    }

    public static <T, K, V> Collector<T, ?, MutableSetMultimap<K, V>> toSetMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupBy(groupBy, valueFunction, Multimaps.mutable.set::empty);
    }

    public static <T, K> Collector<T, ?, MutableBagMultimap<K, T>> toBagMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupBy(groupBy, Multimaps.mutable.bag::empty);
    }

    public static <T, K, V> Collector<T, ?, MutableBagMultimap<K, V>> toBagMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupBy(groupBy, valueFunction, Multimaps.mutable.bag::empty);
    }

    public static <T, K> Collector<T, ?, ImmutableListMultimap<K, T>> toImmutableListMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupByImmutable(groupBy, Multimaps.mutable.list::empty, MutableListMultimap::toImmutable);
    }

    public static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> toImmutableListMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupByImmutable(groupBy, valueFunction, Multimaps.mutable.list::empty, MutableListMultimap::toImmutable);
    }

    public static <T, K> Collector<T, ?, ImmutableSetMultimap<K, T>> toImmutableSetMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupByImmutable(groupBy, Multimaps.mutable.set::empty, MutableSetMultimap::toImmutable);
    }

    public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupByImmutable(groupBy, valueFunction, Multimaps.mutable.set::empty, MutableSetMultimap::toImmutable);
    }

    public static <T, K> Collector<T, ?, ImmutableBagMultimap<K, T>> toImmutableBagMultimap(
            Function<? super T, ? extends K> groupBy)
    {
        return Collectors2.groupByImmutable(groupBy, Multimaps.mutable.bag::empty, MutableBagMultimap::toImmutable);
    }

    public static <T, K, V> Collector<T, ?, ImmutableBagMultimap<K, V>> toImmutableBagMultimap(
            Function<? super T, ? extends K> groupBy,
            Function<? super T, ? extends V> valueFunction)
    {
        return Collectors2.groupByImmutable(groupBy, valueFunction, Multimaps.mutable.bag::empty, MutableBagMultimap::toImmutable);
    }
}

