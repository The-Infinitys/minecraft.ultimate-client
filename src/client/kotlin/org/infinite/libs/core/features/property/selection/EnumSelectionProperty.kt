package org.infinite.libs.core.features.property.selection

import org.infinite.libs.core.features.property.SelectionProperty

/**
 * Enumの全要素を選択肢として保持するプロパティ
 *
 * @param T Enumの型
 * @param default デフォルト値
 */
open class EnumSelectionProperty<T : Enum<T>>(
    default: T,
) : SelectionProperty<T>(
    default = default,
    opts = default.declaringJavaClass.enumConstants.toList(),
) {
    // Enumのクラス型を保持しておく（逆引き用）
    private val enumClass: Class<T> = default.declaringJavaClass

    override fun tryApply(anyValue: Any?) {
        if (anyValue == null) return

        // 1. すでに同じEnum型の場合
        if (enumClass.isInstance(anyValue)) {
            @Suppress("UNCHECKED_CAST")
            this.value = anyValue as T
            return
        }

        // 2. 文字列から逆引き (ConfigManager経由はここを通る)
        if (anyValue is String) {
            // name() との一致を確認
            val found = options.find { it.name.equals(anyValue, ignoreCase = true) }
                // もし見つからなければ、propertyString (表示名) との一致を確認
                ?: options.find { propertyString(it).equals(anyValue, ignoreCase = true) }

            if (found != null) {
                this.value = found
                return
            }
        }

        // 3. 数値（ordinal）からの逆引き
        if (anyValue is Number) {
            val ordinal = anyValue.toInt()
            if (ordinal in options.indices) {
                this.value = options[ordinal]
            }
        }
    }
}
