package com.dorkyhead.flappybird

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Rectangle
import java.util.*
import kotlin.math.min

class FlappyBird : ApplicationAdapter() {

    var batch: SpriteBatch? = null
    var backGround: Texture? = null
    var bottomBackGround: Texture? = null
    var birdState = mutableListOf<Texture>()
    var birdSize: Float = 0f
    var flapState = 0
    var birdYCordinate: Float = 0f
    var birdVelocity: Float = 0f
    var gameState = 0
    var gravity = 2f
    var gapBetweenPipes = 0f

    var topPipe: Texture? = null
    var bottomPipe: Texture? = null

    //maximum motion of the pipes
    var maxPipeOffset = 0f
    var randomGenerator:Random? = null
    var distanceBetweenHorizontalPipes = 0

    val pipeVelocity = 7
    val numberOfPipes = 4
    //making pipes move to the left
    var pipeInX = FloatArray(numberOfPipes)
    var distanceBetweenVerticalPipes = FloatArray(numberOfPipes)

    //creating shapes using shapeRender
//    var shapeRenderer: ShapeRenderer? = null
//    var topPipeRectangle = mutableListOf<Rectangle>()
//    var bottomPipeRectangle = mutableListOf<Rectangle>()
//    var birdCircle: Circle? = null


    override fun create() {
        batch = SpriteBatch()
        backGround = Texture("background-day.png")
        bottomBackGround = Texture("base.png")
        birdState.add(Texture("bluebird-downflap.png"))
        birdState.add(Texture("bluebird-midflap.png"))
        birdState.add(Texture("bluebird-upflap.png"))
        birdState.add(Texture("bluebird-midflap.png"))

        topPipe = Texture("topPipe.png")
        bottomPipe = Texture("bottomPipe.png")

        //bird size
        birdSize = min(
                (Gdx.graphics.width * 0.1).toFloat(),
                (Gdx.graphics.height * 0.1).toFloat()
        )
        gapBetweenPipes = ((birdSize*4.2).toFloat())

        //bird falls due to gravity
        birdYCordinate = (Gdx.graphics.height/2 - birdSize/2)
        maxPipeOffset = (Gdx.graphics.height - gapBetweenPipes*1.64).toFloat()
        //.toFloat converts the values to Float type

        randomGenerator = Random()
        distanceBetweenHorizontalPipes = (Gdx.graphics.width*3/4)
        //bringing in the pipes from right to left:
        for (i in 0 until numberOfPipes){
            pipeInX[i] = (Gdx.graphics.width + i*distanceBetweenHorizontalPipes).toFloat()
            distanceBetweenVerticalPipes[i] = (randomGenerator!!.nextFloat() - 0.5f)* maxPipeOffset
        }

      //shapeRenderer = ShapeRenderer()

    }

    override fun render() {

        //backGround
        batch!!.begin()
        batch!!.draw(backGround, 0f, 0f,
                Gdx.graphics.width.toFloat(),
                Gdx.graphics.height.toFloat())
        //bird keeps falling down after the first touch, next simultaneous touches powers-the-bird UP

            if (gameState != 0) {
                if (Gdx.input.justTouched()) {
                    birdVelocity = (-gapBetweenPipes * 0.071).toFloat()
                }
                // this for loop will create the pipes until the bird does not hit a pipe
                for (i in 0 until numberOfPipes) {
                    if (pipeInX[i] < -birdSize * 2) {
                        pipeInX[i] = pipeInX[i] + numberOfPipes * this.distanceBetweenHorizontalPipes
                        distanceBetweenVerticalPipes[i] = (randomGenerator!!.nextFloat() - 0.5f) * maxPipeOffset
                    } else {
                        pipeInX[i] = pipeInX[i] - pipeVelocity
                    }

                    //bottomPipes
                batch!!.draw(
                        bottomPipe,
                        pipeInX[i],
                        0f - ((Gdx.graphics.height - gapBetweenPipes/3.7)*0.5f).toFloat() + distanceBetweenVerticalPipes[i] +
                                maxPipeOffset*0.47f,
                        birdSize*2,
                        (Gdx.graphics.height/2 - gapBetweenPipes/2) +
                                maxPipeOffset*0.5f

                )

                    //topPipes
                    batch!!.draw(
                            topPipe,
                            pipeInX[i],
                            ((Gdx.graphics.height - gapBetweenPipes/3.7)*0.5f).toFloat() + distanceBetweenVerticalPipes[i] +
                                    maxPipeOffset*0.47f + gapBetweenPipes/10,
                            birdSize * 2,
                            (Gdx.graphics.height*2/3).toFloat()
                    )
                }
                if (birdYCordinate > (Gdx.graphics.height / 4) || birdVelocity < 0) {
                    birdVelocity += gravity
                    birdYCordinate -= birdVelocity
                }

                when (flapState) {
                    0 -> flapState = 1
                    1 -> flapState = 2
                    2 -> flapState = 3
                    3 -> flapState = 0
                }
            } else {
                if (Gdx.input.justTouched()) {
                    gameState = 1
                }
            }
        //bottomBackground
        batch!!.draw(bottomBackGround, 0f, 0f,
                (Gdx.graphics.width).toFloat(),
                (Gdx.graphics.height/4).toFloat()
        )

        //bird
        batch!!.draw(birdState[flapState],
                (Gdx.graphics.width/3 - birdSize/2),
                birdYCordinate,
                birdSize,
                birdSize
        )
        batch!!.end()
    }

}


