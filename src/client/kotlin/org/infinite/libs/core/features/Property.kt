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
        val className = anyValue?.let { it::class.simpleName } ?: "null"

        if (anyValue == null) {
            LogSystem.warn("[Property:$name] Received null value, skipping.")
            return
        }

        // 1. 直接キャスト可能な場合のチェック
        if (valueType.isInstance(anyValue)) {
            @Suppress("UNCHECKED_CAST")
            this.value = anyValue as T
            return
        }

        // 2. 型変換の試行
        @Suppress("UNCHECKED_CAST")
        val converted: Any? = when (valueType) {
            Int::class.javaObjectType, Int::class.java -> {
                val res = (anyValue as? Number)?.toInt()
                res
            }

            Long::class.javaObjectType, Long::class.java -> {
                val res = (anyValue as? Number)?.toLong()
                res
            }

            Float::class.javaObjectType, Float::class.java -> {
                val res = (anyValue as? Number)?.toFloat()
                res
            }

            Double::class.javaObjectType, Double::class.java -> {
                val res = (anyValue as? Number)?.toDouble()
                res
            }

            Boolean::class.javaObjectType, Boolean::class.java -> {
                val res = when (anyValue) {
                    is Boolean -> anyValue
                    is Number -> anyValue.toLong() != 0L
                    is String -> anyValue.toBoolean()
                    else -> null
                }
                res
            }

            String::class.java -> {
                val res = anyValue.toString()
                res
            }

            else -> {
                LogSystem.warn("[Property:$name] No conversion rule defined for target type ${valueType.simpleName}")
                null
            }
        }

        if (converted != null) {
            try {
                @Suppress("UNCHECKED_CAST")
                this.value = converted as T
            } catch (e: Exception) {
                LogSystem.error("[Property:$name] Failed to cast converted value to T: ${e.message}")
            }
        } else {
            LogSystem.error("[Property:$name] Conversion failed. Value '$anyValue' ($className) is incompatible with ${valueType.simpleName}")
        }
    }

    open fun widget(x: Int, y: Int, width: Int): PropertyWidget<*> =
        PropertyWidget(x, y, width, property = this)
}
