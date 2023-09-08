
import demos.classes.Animation
import kotlinx.coroutines.DelicateCoroutinesApi
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.FontImageMap
import org.openrndr.draw.FontMap
import org.openrndr.draw.loadFont
import org.openrndr.extra.color.presets.WHITE_SMOKE
import org.openrndr.extra.noise.random
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.RoundedRectangle
import org.openrndr.extra.shapes.grid
import org.openrndr.extra.shapes.roundedRectangle
import org.openrndr.math.IntVector2
import org.openrndr.shape.Rectangle
import java.io.File


@OptIn(DelicateCoroutinesApi::class)
fun main() = application {
    configure {
        width = 608
        height = 342
//        hideWindowDecorations = true
        windowAlwaysOnTop = true
        position = IntVector2(1170,110)
        windowTransparent = true
        windowResizable = true
    }

    oliveProgram {
        var mouseClick = false
        var mouseState = "up"
        mouse.dragged.listen {
            mouseState = "drag"
        }
        mouse.exited.listen{
            mouseState = "up"
        }
        mouse.buttonUp.listen {
            mouseState = "up"
            mouseClick = true
        }
        mouse.buttonDown.listen {
            mouseState = "down"
        }
        mouse.moved.listen{
            mouseState = "move"
        }
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
        val scale: DoubleArray = typeScale(3, 10.0, 3)
        val typeFace: Pair<List<FontMap>, List<FontImageMap>> = defaultTypeSetup(scale, listOf("reg", "reg", "bold"))
        val font = loadFont("data/fonts/default.otf", 13.0)
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


        var adjustableWidth = width / 4.0 // mouse.position.x.coerceIn(masterGutter, width.toDouble() - (masterGutter*4)) // Modify this to change the first rectangle's width
        var remainingWidth = drawer.bounds.width - adjustableWidth - 3 * masterGutter
        var canvRect = RoundedRectangle(
            2 * masterGutter + adjustableWidth,
            masterGutter,
            remainingWidth,
            drawer.bounds.height - 2 * masterGutter,
            radius / 2
            )

        var guiRect = RoundedRectangle(
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

        var sliderGrid = guiGrid[0].grid(colCount, rowCount, marginX*2, marginY*2, gutterX, gutterY).flatten()
        var buttonGrid = guiGrid[1].grid(1, 2, marginX*2, marginY*2, gutterX, gutterY).flatten()
        var toggleGrid = guiGrid[2].grid(1, 3, marginX*2, marginY*2, gutterX, gutterY).flatten()

        class CSlider(rectRef: Rectangle){
            val innerMarginX = 5.0
            val innerMarginY = 2.5
            val xPos = rectRef.x
            val yPos = rectRef.y + innerMarginY
            val w = rectRef.width
            val h = rectRef.height - innerMarginY
            private val sliderRad = h/2
            private var sliderX = xPos + (sliderRad * 0.5)

            fun isHovering(mouseX: Double, mouseY: Double): Boolean {
                return mouseX >= xPos && mouseX <= xPos + w &&
                        mouseY >= yPos && mouseY <= yPos + h
            }
            fun updateSlider(mouseX: Double, mousePressed: Boolean) {
//                if (isHovering(mouseX, yPos + h / 2) && mouseDrag) {
                    sliderX = mouseX.coerceIn(xPos + sliderRad, xPos + w - sliderRad)
//                }
            }

            fun display(){
                drawer.fill = ColorRGBa.BLACK
                drawer.stroke = ColorRGBa.WHITE
                drawer.roundedRectangle(xPos, yPos, w, h, sliderRad)
                drawer.fill = ColorRGBa.WHITE
                drawer.stroke = ColorRGBa.BLACK
                drawer.circle(sliderX, yPos + (h / 2), sliderRad)
//                drawer.circle(xPos + (sliderRad*0.5), yPos + (h/2), sliderRad)
            }
        }
        class CButton(rectRef: Rectangle){
            val innerMarginX = 5.0
            val innerMarginY = 2.5
            val bRad = 5.0
            val xPos = rectRef.x
            val yPos = rectRef.y + innerMarginY
            val w = rectRef.width
            val h = rectRef.height - innerMarginY*2
            var isActive: Boolean = false
            var fillCol = ColorRGBa.BLACK
            var toggleOnOff: Boolean = false

            fun isHovering(mouseX: Double, mouseY: Double): Boolean {
                return mouseX >= xPos && mouseX <= xPos + w &&
                        mouseY >= yPos && mouseY <= yPos + h
            }

            fun toggle() {
                isActive = !isActive
            }

            fun checkButton(mouseClick: Boolean) {
                if (this.isHovering(mouse.position.x, mouse.position.y) && mouseClick) {
//                if (this.isHovering(mouse.position.x, mouse.position.y) && mouseState == "down") {
                    toggle()
                }
            }

            fun display(){
                drawer.stroke = ColorRGBa.WHITE
                if(isActive) fillCol = ColorRGBa.GREEN
                else fillCol = ColorRGBa.BLACK
                drawer.fill = fillCol
                drawer.roundedRectangle(xPos, yPos, w, h, bRad)
            }
        }
        class CToggle(rectRef: Rectangle){
            val innerMarginX = 5.0
            val innerMarginY = 2.5
            val bRad = 5.0
            val xPos = rectRef.x
            val yPos = rectRef.y + innerMarginY
            val w = rectRef.width
            val h = rectRef.height - innerMarginY*2
            fun display(){
                drawer.fill = ColorRGBa.BLACK
                drawer.stroke = ColorRGBa.WHITE
                drawer.roundedRectangle(xPos, yPos, w, h, bRad)
                drawer.fontMap = font
                drawer.fill = ColorRGBa.WHITE
//                drawer.text("TOGGLE", xPos + innerMarginX, yPos + innerMarginY + 10.0)
            }
        }

        var sliderArr =  mutableListOf<CSlider>()
        var buttonArr =  mutableListOf<CButton>()
        var toggleArr =  mutableListOf<CToggle>()

        var prevWindowW = 0.0
        var currentWindowW: Double

        fun resizeWindow(){
            adjustableWidth = width / 4.0 // mouse.position.x.coerceIn(masterGutter, width.toDouble() - (masterGutter*4)) // Modify this to change the first rectangle's width
            remainingWidth = drawer.bounds.width - adjustableWidth - 3 * masterGutter

            canvRect = RoundedRectangle(
                2 * masterGutter + adjustableWidth,
                masterGutter,
                remainingWidth,
                drawer.bounds.height - 2 * masterGutter,
                radius / 2
            )
            guiRect = RoundedRectangle(
                masterGutter,
                masterGutter,
                adjustableWidth,
                drawer.bounds.height - 2 * masterGutter,
                radius / 2
            )
            guiGrid = Rectangle(
                guiRect.corner,
                guiRect.width,
                guiRect.height
            ).grid(colCount, rowCount, marginX, marginY, gutterX, gutterY).flatten()
            sliderGrid = guiGrid[0].grid(colCount, rowCount, marginX*2, marginY*2, gutterX, gutterY).flatten()
            buttonGrid = guiGrid[1].grid(1, 2, marginX*2, marginY*2, gutterX, gutterY).flatten()
            toggleGrid = guiGrid[2].grid(1, 3, marginX*2, marginY*2, gutterX, gutterY).flatten()

            sliderArr.clear()
            buttonArr.clear()
            toggleArr.clear()

            sliderGrid.forEachIndexed{ i, e->
                sliderArr.add(CSlider(sliderGrid[i]))
            }
            buttonGrid.forEachIndexed{ i, e->
                buttonArr.add(CButton(buttonGrid[i]))
            }
            toggleGrid.forEachIndexed{ i, e->
                toggleArr.add(CToggle(toggleGrid[i]))
            }
        }
        resizeWindow()
        extend {
            currentWindowW = program.width.toDouble()
            if(prevWindowW != currentWindowW){
//                resizeWindow()
                println("resized")
                prevWindowW = currentWindowW
            }
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

            sliderArr.forEach{ e->
                e.display()
                if (e.isHovering(mouse.position.x, mouse.position.y) && (mouseState == "drag" || mouseClick)) {
                    e.updateSlider(mouse.position.x, mouseClick)
                }
            }
            buttonArr.forEach{ e->
                e.display()
                e.checkButton(mouseClick)
            }
            toggleArr.forEach{ e->
                e.display()
            }

            if (mouseClick) mouseClick = false
        }
    }
}