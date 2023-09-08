
import demos.classes.Animation
import kotlinx.coroutines.DelicateCoroutinesApi
import org.openrndr.MouseEvent
import org.openrndr.MouseEvents
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.color.presets.FLORAL_WHITE
import org.openrndr.extra.color.presets.GHOST_WHITE
import org.openrndr.extra.color.presets.WHITE_SMOKE
import org.openrndr.extra.noise.random
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.RoundedRectangle
import org.openrndr.extra.shapes.grid
import org.openrndr.extra.shapes.roundedRectangle
import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2
import org.openrndr.math.mix
import org.openrndr.shape.Rectangle
import org.openrndr.writer
import java.awt.SystemColor.window
import java.io.File
import javax.swing.plaf.basic.BasicTreeUI.MouseHandler
import kotlin.system.measureTimeMillis


@OptIn(DelicateCoroutinesApi::class)
fun main() = application {
    configure {
        width = 608
        height = 342
        hideWindowDecorations = true
        windowAlwaysOnTop = true
        position = IntVector2(1170,110)
        windowTransparent = true
    }

    oliveProgram {
        var palette = listOf(
            ColorRGBa.fromHex(0xF1934B),
            ColorRGBa.fromHex(0x0E8847),
            ColorRGBa.fromHex(0xD73E1C),
            ColorRGBa.fromHex(0xF4ECDF),
            ColorRGBa.fromHex(0x552F20)
        )
        val animation = Animation()
        val loopDelay = 3.0
        val message = "hello"
        animation.loadFromJson(File("data/keyframes/keyframes-0.json"))
        val scale: DoubleArray = typeScale(3, 100.0, 3)
        val typeFace: Pair<List<FontMap>, List<FontImageMap>> = defaultTypeSetup(scale, listOf("reg", "reg", "bold"))
        var rad = 10.0
        val animArr = mutableListOf<Animation>()
        val randNums = mutableListOf<Double>()
        val charArr = message.toCharArray()
        charArr.forEach { e ->
            animArr.add(Animation())
            randNums.add(random(0.0, 1.0))
        }
        animArr.forEach { a ->
            a.loadFromJson(File("data/keyframes/keyframes-0.json"))
        }

        val radius = 10.0
        val masterGutter = 10.0
        val lineThickness = 2.0

        val adjustableWidth = width / 4.0 // mouse.position.x.coerceIn(masterGutter, width.toDouble() - (masterGutter*4)) // Modify this to change the first rectangle's width
        val remainingWidth = drawer.bounds.width - adjustableWidth - 3 * masterGutter
        val canvRect = RoundedRectangle(
            2 * masterGutter + adjustableWidth,
            masterGutter,
            remainingWidth,
            drawer.bounds.height - 2 * masterGutter,
            radius / 2
        )
        val guiRect = RoundedRectangle(
            masterGutter,
            masterGutter,
            adjustableWidth,
            drawer.bounds.height - 2 * masterGutter,
            radius / 2
        )

        val colCount = 1
        val rowCount = 4
        val marginX = 2.0
        val marginY = 2.0
        val gutterX = 2.0
        val gutterY = 2.0

        var guiGrid = Rectangle(
            guiRect.corner,
            guiRect.width,
            guiRect.height
        ).grid(colCount, rowCount, marginX, marginY, gutterX, gutterY).flatten()

        val subGrid = guiGrid[0].grid(colCount, rowCount, marginX*2, marginY*2, gutterX, gutterY).flatten()

        extend {
            animArr.forEachIndexed { i, a ->
                a((randNums[i] * 0.3 + frameCount * 0.02) % loopDelay)
            }
            val guiFill = ColorRGBa.WHITE_SMOKE
            drawer.clear(ColorRGBa.TRANSPARENT)
            drawer.fill = ColorRGBa.BLACK
            drawer.stroke = guiFill
            drawer.strokeWeight = 5.0

            drawer.stroke = guiFill
            drawer.strokeWeight = lineThickness
            drawer.roundedRectangle(
                lineThickness / 2,
                lineThickness / 2,
                drawer.bounds.width - lineThickness,
                drawer.bounds.height - lineThickness,
                radius
            )
            drawer.fill = guiFill
            drawer.roundedRectangle(guiRect)
            drawer.roundedRectangle(canvRect)
            drawer.stroke = ColorRGBa.BLACK
            guiGrid.forEach{ r->
                drawer.rectangle(r)
            }

            subGrid.forEach { e->
                drawer.rectangle( e )
            }
        }
    }
}