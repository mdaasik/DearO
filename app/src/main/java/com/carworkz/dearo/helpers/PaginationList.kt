package com.carworkz.dearo.helpers

class PaginationList<E> : ArrayList<E>() {

    var metaData: MetaData? = null

    companion object {

        fun <E> getPaginationList(list: List<E>, metaData: MetaData): PaginationList<E> {
            val obj = PaginationList<E>()
            obj.addAll(list)
            obj.metaData = metaData
            return obj
        }
    }
}
