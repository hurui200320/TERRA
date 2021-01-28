package info.skyblond.kademlia.utils

import org.intellij.lang.annotations.Flow
import org.jetbrains.annotations.NotNull
import java.util.*


/**
 * Data stored as [TreeSet], but do a sequential search.
 *
 * This is due to a usage: K-bucket using TreeSet to sort routes by last seen,
 * however sometimes we want to use node as key rather than route,
 * then we create a [Route] with default last seen.
 * If a node is already in TreeSet, the default last seen would mislead the TreeSet
 * (TreeSet using `compareTo` to do a binary search),
 * thus a node in TreeSet would report not found.
 * Sequential search might solve this issue.
 * */
class SequentialTreeSet<T> : TreeSet<T> {
    /**
     * Same as `TreeSet()`
     * @see TreeSet
     * */
    constructor() : super()

    /**
     * Same as `TreeSet(Comparator<in T>)`
     * @see TreeSet
     * */
    constructor(comparator: Comparator<in T>) : super(comparator)

    /**
     * Same as `TreeSet(Collection<T>)`
     * @see TreeSet
     * */
    constructor(@NotNull @Flow(sourceIsContainer = true, targetIsContainer = true) c: Collection<T>) : super(c)


    /**
     * Same as `TreeSet(SortedSet<T>)`
     * @see TreeSet
     * */
    constructor(s: SortedSet<T>) : super(s)

    /**
     * If given element is in current tree set.
     * Using `Objects.equals(o, e)` and sequential search
     * */
    override fun contains(element: T): Boolean {
        for (e in super.iterator()) {
            if (Objects.equals(element, e))
                return true
        }
        return false
    }

    /**
     * Add if and only if element not found in set
     * */
    override fun add(element: T): Boolean {
        if (this.contains(element))
            return false
        return super.add(element)
    }

    /**
     * Remove a element by sequential search
     * */
    override fun remove(element: T): Boolean {
        for (e in super.iterator()) {
            if (Objects.equals(element, e)) {
                // make sure deleted obj are same as the one in set
                return super.remove(e)
            }
        }
        return false
    }

    /**
     * Adds all of the elements in the specified collection to this set.
     * Duplicate would be drop.
     * */
    override fun addAll(elements: Collection<T>): Boolean {
        return super.addAll(elements.takeWhile { !this.contains(it) })
    }
}