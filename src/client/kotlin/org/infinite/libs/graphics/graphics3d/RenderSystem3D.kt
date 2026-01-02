package org.infinite.libs.graphics.graphics3d

import com.mojang.blaze3d.buffers.GpuBufferSlice
import com.mojang.blaze3d.resource.GraphicsResourceAllocator
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.world.phys.Vec3
import org.infinite.libs.graphics.graphics3d.structs.RenderCommand3D
import org.infinite.libs.interfaces.MinecraftInterface
import org.joml.Matrix4f
import org.joml.Vector4f

class RenderSystem3D(
    private val graphicsResourceAllocator: GraphicsResourceAllocator,
    private val deltaTracker: DeltaTracker,
    private val renderBlockOutline: Boolean,
    private val camera: Camera,
    private val positionMatrix: Matrix4f,
    private val projectionMatrix: Matrix4f,
    private val frustumMatrix: Matrix4f,
    private val gpuBufferSlice: GpuBufferSlice,
    private val vector4f: Vector4f,
    private val bl2: Boolean,
) : MinecraftInterface() {
    /**
     * レンダリングスレッドから計算スレッドへ渡すための安全なスナップショット
     */
    data class RenderSnapshot(
        val posMatrix: Matrix4f, // Matrix4f(positionMatrix) でコピー済み
        val projMatrix: Matrix4f, // Matrix4f(projectionMatrix) でコピー済み
        val cameraPos: Vec3, // camera.position
        val partialTicks: Float, // deltaTracker.gameTimeDeltaTicks
        val scaledWidth: Int,
        val scaledHeight: Int,
        val isOutlineEnabled: Boolean, // renderBlockOutline
    )

    fun snapShot(): RenderSnapshot {
        val window = minecraft.window
        return RenderSnapshot(
            posMatrix = Matrix4f(positionMatrix),
            projMatrix = Matrix4f(projectionMatrix),
            cameraPos = Vec3(camera.position().x, camera.position().y, camera.position().z),
            partialTicks = deltaTracker.gameTimeDeltaTicks,
            scaledWidth = window.guiScaledWidth,
            scaledHeight = window.guiScaledHeight,
            isOutlineEnabled = renderBlockOutline,
        )
    }

    fun test() {
        val player = minecraft.player ?: return
        val start = player.position()
        val end = start.add(10.0, 10.0, 10.0)
        drawLine(start, end, 0xFFFF0000.toInt(), 2f, false)
    }

//    private val cameraPos: Vec3
//        get() = camera.position()

    fun drawLine(start: Vec3, end: Vec3, color: Int, lineWidth: Float, depthTest: Boolean = true) {
    }

    fun render(commands: List<RenderCommand3D>) {
        for (i in 0 until commands.size) {
            when (val c = commands[i]) {
                is RenderCommand3D.Line -> drawLine(c.from, c.to, c.color, c.size, false)
                RenderCommand3D.PopMatrix -> TODO()
                RenderCommand3D.PushMatrix -> TODO()
                is RenderCommand3D.Quad -> TODO()
                is RenderCommand3D.SetMatrix -> TODO()
                is RenderCommand3D.Triangle -> TODO()
            }
        }
    }
}
