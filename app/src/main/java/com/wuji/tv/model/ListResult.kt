package com.wuji.tv.model


data class ListResult(
    val list_items: List<Item>
)

data class Item(
    val bt_ticket: String = "",
    val dl_ticket: String = "",
    val name: String = "",
    val status: Int = 0,
    val user: String = "",
    val seeding: Boolean = false,
    val dl_length: Long = 0,
    val is_main_seed: Boolean = false
)
