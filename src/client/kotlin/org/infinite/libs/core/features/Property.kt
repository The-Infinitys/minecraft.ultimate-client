package org.infinite.libs.core.features

import org.infinite.libs.log.LogSystem
import org.infinite.libs.ui.widgets.PropertyWidget
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

open class Property<T : Any>(
    val default: T,
) {
    // 追加: 自身の名称と親Featureの参照
    var name: String = "unknown"
        internal set
    var parent: Feature? = null
        internal set
    private val valueType = default::class.java
    private val _value = AtomicReference<T>(default)
    private val listeners = CopyOnWriteArrayList<(oldValue: T, newValue: T) -> Unit>()

    /**
     * プロパティの翻訳キーを生成する。
     * 例: infinite.features.cheat.movement.flight.speed
     */
    fun translationKey(): String? {
        val parentKey = parent?.translation() ?: return null
        return "$parentKey.$name"
    }

    var value: T
        get() = _value.get()
        set(newValue) {
            val filtered = filterValue(newValue)
            val oldValue = _value.getAndSet(filtered)
            if (oldValue != filtered) {
                notifyListeners(oldValue, filtered)
            }
        }

    protected open fun filterValue(newValue: T): T = newValue
    fun reset() {
        value = default
    }

    fun addListener(listener: (oldValue: T, newValue: T) -> Unit) = listeners.add(listener)
    fun removeListener(listener: (oldValue: T, newValue: T) -> Unit) = listeners.remove(listener)
    protected fun notifyListeners(oldValue: T, newValue: T) {
        listeners.forEach { it(oldValue, newValue) }
    }

    fun tryApply(anyValue: Any?) {
        if (anyValue == null) return

        // 1. 実行時の型チェック (T の情報を活用)
        if (valueType.isInstance(anyValue)) {
            @Suppress("UNCHECKED_CAST")
            this.value = anyValue as T
            return
        }

        // 2. 型が合わない場合の「賢い」変換
        // T (type) が Boolean か Int か等で分岐
        @Suppress("UNCHECKED_CAST")
        val converted: T? = when (default) {
            is Boolean -> when (anyValue) {
                is Number -> (anyValue.toLong() != 0L) as? T
                is String -> anyValue.toBoolean() as? T
                else -> null
            }

            is Int -> (anyValue as? Number)?.toInt() as? T
            is Long -> (anyValue as? Number)?.toLong() as? T
            is Float -> (anyValue as? Number)?.toFloat() as? T
            is Double -> (anyValue as? Number)?.toDouble() as? T
            is String -> anyValue.toString() as? T
            else -> null
        }

        if (converted != null) {
            this.value = converted
        } else {
            LogSystem.warn("Property '$name': Type mismatch. Expected ${valueType.simpleName}, got ${anyValue::class.simpleName}")
        }
    }

    open fun widget(x: Int, y: Int, width: Int): PropertyWidget<*> =
        PropertyWidget(x, y, width, property = this)
}
