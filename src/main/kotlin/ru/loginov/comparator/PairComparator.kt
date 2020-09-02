package ru.loginov.comparator

class PairComparator : Comparator<Pair<Int, Int>> {

    override fun compare(o1: Pair<Int, Int>?, o2: Pair<Int, Int>?): Int {
        if (o1 == null || o2 == null) {
            return if (o1 == o2) 0 else if (o1 == null) -1 else 1
        }

        val firstCompare = o1.first.compareTo(o2.first)
        return if (firstCompare != 0) firstCompare else o1.second.compareTo(o2.second)
    }
}