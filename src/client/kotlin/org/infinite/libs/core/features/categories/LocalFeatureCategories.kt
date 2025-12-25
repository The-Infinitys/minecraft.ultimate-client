package org.infinite.libs.core.features.categories

import kotlinx.coroutines.*
import net.minecraft.client.DeltaTracker
import org.infinite.libs.core.features.FeatureCategories
import org.infinite.libs.core.features.categories.category.LocalCategory
import org.infinite.libs.core.features.feature.LocalFeature
import org.infinite.libs.graphics.graphics2d.structs.RenderCommand
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class LocalFeatureCategories(
    categories: List<LocalCategory>,
) : FeatureCategories<KClass<out LocalFeature>, LocalFeature, KClass<out LocalCategory>, LocalCategory>() {
    override val categories: ConcurrentHashMap<KClass<out LocalCategory>, LocalCategory> = ConcurrentHashMap()

    private var connectionScope: CoroutineScope? = null

    init {
        categories.forEach { insert(it) }
    }

    // --- ライフサイクルメソッド (onConnected, onDisconnected等は変更なし) ---

    fun onConnected() {
        connectionScope?.cancel()
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        connectionScope = scope
        scope.launch {
            try {
                categories.values.map { launch { it.onConnected() } }.joinAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onDisconnected() {
        connectionScope?.cancel()
        connectionScope = null
        runBlocking(Dispatchers.Default) {
            categories.values.map { category ->
                launch {
                    try {
                        category.onDisconnected()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.joinAll()
        }
    }

    fun onShutdown() = onDisconnected()

    fun onStartTick() {
        connectionScope?.launch {
            try {
                categories.values.map { launch { it.onStartTick() } }.joinAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onEndTick() {
        connectionScope?.launch {
            try {
                categories.values.map { launch { it.onEndTick() } }.joinAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * UIレンダリング: 全カテゴリーの命令を収集し、優先度順に並べ替えて一つのリストにする
     */
    suspend fun onStartUiRendering(deltaTracker: DeltaTracker): List<RenderCommand> {
        return mergeCategoriesRendering { it.onStartUiRendering(deltaTracker) }
    }

    suspend fun onEndUiRendering(deltaTracker: DeltaTracker): List<RenderCommand> {
        return mergeCategoriesRendering { it.onEndUiRendering(deltaTracker) }
    }

    /**
     * 内部共通ロジック: カテゴリーを跨いで優先度グループを統合する
     */
    private suspend fun mergeCategoriesRendering(
        fetchBlock: suspend (LocalCategory) -> LinkedList<Pair<Int, List<RenderCommand>>>,
    ): List<RenderCommand> = coroutineScope {
        // 1. 各カテゴリーから (Priority -> Commands) のリストを並列取得
        val deferredResults = categories.values.map { category ->
            async(Dispatchers.Default) { fetchBlock(category) }
        }.awaitAll()

        // 2. 全カテゴリーの結果を Priority をキーに統合する
        // TreeMap を使うことで自動的に Priority (Int) 順にソートされる
        val sortedMap = TreeMap<Int, MutableList<RenderCommand>>()

        for (categoryResult in deferredResults) {
            for ((priority, commands) in categoryResult) {
                sortedMap.getOrPut(priority) { mutableListOf() }.addAll(commands)
            }
        }

        // 3. 優先度の低い順にフラットなリストへ変換
        // TreeMap.values はキーの昇順で反復される
        val finalCommands = mutableListOf<RenderCommand>()
        for (commandsInPriority in sortedMap.values) {
            finalCommands.addAll(commandsInPriority)
        }

        finalCommands
    }
}
