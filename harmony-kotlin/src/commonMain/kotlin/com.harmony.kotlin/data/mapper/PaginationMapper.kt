package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.data.entity.PaginationOffsetLimit

class PaginationOffsetLimitMapper<From, To>(private val modelMapper: Mapper<From, To>) : Mapper<PaginationOffsetLimit<From>,
    PaginationOffsetLimit<To>> {

  override fun map(from: PaginationOffsetLimit<From>): PaginationOffsetLimit<To> = PaginationOffsetLimit(
    from.values.map { modelMapper.map(it) }, from.offset,
    from.limit, from.size
  )
}
