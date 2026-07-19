package com.github.caracal.jarvis.shopping.data

/**
 * Publishes locally-originated Shopping List state changes to a cloud sync channel.
 *
 * Implemented by the sync layer so [ShoppingRepository] implementations stay decoupled from
 * any specific transport (e.g. MQTT).
 */
fun interface ShoppingSyncPublisher {

    /** Publishes the given snapshot JSON, as produced by [ShoppingRepository.exportSnapshot]. */
    fun publish(snapshotJson: String)
}
