package com.dorkyhead.flappybird

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Color.BLUE
import com.badlogic.gdx.graphics.Color.toFloatBits
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import java.util.*
import javax.xml.soap.Text
import kotlin.math.min

class FlappyBird : ApplicationAdapter() {

    var batch: SpriteBatch? = null

    var backGround = mutableListOf<Texture>()
    //randomly generate the background (0,1)
    var backGroundState = (0..2).random()

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

    //maximum upward motion of the pipes
    var maxPipeOffset = 0f
    var randomGenerator:Random? = null
    var distanceBetweenHorizontalPipes = 0

    val pipeVelocity = 7
    val numberOfPipes = 4
    //making pipes move to the left
    var pipeInX = FloatArray(numberOfPipes)
    var distanceBetweenVerticalPipes = FloatArray(numberOfPipes)

    //creating shapes using shapeRender for collision detection
    var topPipeRectangle = mutableListOf<Rectangle>()
    var bottomPipeRectangle = mutableListOf<Rectangle>()
    var birdCircle: Circle? = null

    var message: Texture? = null

    //playButton
    var playButton: Texture? = null


    //scoring integer variable
    var score = 0
    var scorePipe = 0
    var bitmapFont: BitmapFont? = null
    var scoreTextures = mutableListOf<Texture>()

    //gameOver
    var gameOver: Texture? = null



    override fun create() {
        batch = SpriteBatch()

        backGround.add(Texture("background-day.png"))
        backGround.add(Texture("background-night.png"))

        bottomBackGround = Texture("base.png")
        birdState.add(Texture("bluebird-downflap.png"))
        birdState.add(Texture("bluebird-midflap.png"))
        birdState.add(Texture("bluebird-upflap.png"))
        birdState.add(Texture("bluebird-midflap.png"))

        topPipe = Texture("topPipe.png")
        bottomPipe = Texture("bottomPipe.png")

        message = Texture("message.png")
        playButton = Texture("start-button.png")

        //bird size
        birdSize = min(
                (Gdx.graphics.width * 0.1).toFloat(),
                (Gdx.graphics.height * 0.1).toFloat()
        )
        gapBetweenPipes = ((birdSize*4.2).toFloat())


        maxPipeOffset = (Gdx.graphics.height - gapBetweenPipes*1.64).toFloat()
        //.toFloat converts the values to Float type

        randomGenerator = Random()
        distanceBetweenHorizontalPipes = (Gdx.graphics.width*3/4)

        //shapeRenderer = ShapeRenderer()
        birdCircle = Circle()

        //creating score
        bitmapFont = BitmapFont()
        bitmapFont!!.setColor(Color.WHITE)
        bitmapFont!!.data.scale(birdSize/10)

        //gameOver
        gameOver = Texture("gameover.png")

        for (i in 0..9){
            scoreTextures.add(Texture("${i}.png"))
        }

        //bringing in the pipes from right to left:
        for (i in 0 until numberOfPipes){

            topPipeRectangle.add(Rectangle())
            bottomPipeRectangle.add(Rectangle())
        }

        startGame()
    }

    private fun startGame(){
        birdVelocity = 0f
        score = 0
        scorePipe = 0

        //gravity system
        birdYCordinate = (Gdx.graphics.height/2 - birdSize/2)
        for (i in 0 until numberOfPipes) {
            pipeInX[i] = (Gdx.graphics.width + i * distanceBetweenHorizontalPipes).toFloat()
            distanceBetweenVerticalPipes[i] = (randomGenerator!!.nextFloat() - 0.5f) * maxPipeOffset
        }
    }

    override fun render() {

        Gdx.app.log("birdYCordinate", "${birdYCordinate}")

        //backGround
        batch!!.begin()
        batch!!.draw(
                backGround[backGroundState],
                0f,
                0f,
                Gdx.graphics.width.toFloat(),
                Gdx.graphics.height.toFloat()
        )

        if (gameState == 1) {
                if (pipeInX[scorePipe] < (Gdx.graphics.width/2)){
                    score++
                    Gdx.app.log("Score", "${score}")
                    if (scorePipe < numberOfPipes -1){
                        scorePipe++
                    } else {
                        scorePipe = 0
                    }
                }
            //bird keeps falling down after the first touch, next simultaneous touches powers-the-bird UP
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
                if (birdYCordinate > (Gdx.graphics.height/4.5) || birdVelocity < 0) {
                    birdVelocity += gravity
                    birdYCordinate -= birdVelocity
                } else {
                    gameState = 2
                }

                when (flapState) {
                    0 -> flapState = 1
                    1 -> flapState = 2
                    2 -> flapState = 3
                    3 -> flapState = 0
                }
            }
        else if (gameState == 0){

            //gameplay message
            batch!!.draw(
                    message,
                    0f + (Gdx.graphics.width/4),
                    (Gdx.graphics.height/2.8).toFloat(),
                    (Gdx.graphics.width/2).toFloat(),
                    (Gdx.graphics.height/2).toFloat()
            )

          if (Gdx.input.justTouched()){
              gameState = 1
          }
        }
        else if (gameState == 2){
            batch!!.draw(
                    gameOver,
                    (Gdx.graphics.width / 2 - birdSize*3),
                    Gdx.graphics.height /2 - birdSize/2,
                    birdSize*6,
                    birdSize
            )

            if (Gdx.input.justTouched()){
                startGame()
                gameState = 1
            }
        }

        //bird
        batch!!.draw(birdState[flapState],
                (Gdx.graphics.width/3 - birdSize/2),
                birdYCordinate,
                birdSize,
                birdSize
        )

        //bird shapeRenderer
        birdCircle!!.set(
                (Gdx.graphics.width/3).toFloat(),
                birdYCordinate + birdSize/2,
                birdSize/2
        )

        //scoring build
        val scoreDigits = mutableListOf<Int>()
        var scoreNew = score
        if (scoreNew == 0)
        {

                //0-score
                batch!!.draw(
                        scoreTextures[0],
                        birdSize,
                        Gdx.graphics.height - birdSize * 2,
                        birdSize,
                        birdSize
                )


        }
        else
        {
            while (scoreNew > 0)
            {
                scoreDigits.add(scoreNew % 10)
                scoreNew /= 10
            }
            scoreDigits.reverse()
            Gdx.app.log("scoreDigits", "$scoreDigits")
            for (i in 0 until scoreDigits.count())
            {
                //after-0 score
                batch!!.draw(
                        scoreTextures[scoreDigits[i]],
                        birdSize*(i+1),
                        Gdx.graphics.height - birdSize*2,
                        birdSize,
                        birdSize)
            }
        }

        //bottomBackground again so the pipes appear they are behind the bottomBackGround
        batch!!.draw(bottomBackGround, 0f, 0f,
                (Gdx.graphics.width).toFloat(),
                (Gdx.graphics.height/4.5).toFloat()
        )


        //collision detection
        for (i in 0 until numberOfPipes){

            //bottomPipe shapeRenderer
            bottomPipeRectangle[i].set(
                    pipeInX[i],
                    0f - ((Gdx.graphics.height - gapBetweenPipes/3.7)*0.5f).toFloat() + distanceBetweenVerticalPipes[i] +
                            maxPipeOffset*0.47f,
                    birdSize*2,
                    (Gdx.graphics.height/2 - gapBetweenPipes/2) +
                            maxPipeOffset*0.5f
            )

            //topPipe shapeRenderer
            topPipeRectangle[i].set(
                    pipeInX[i],
                    ((Gdx.graphics.height - gapBetweenPipes/3.7)*0.5f).toFloat() + distanceBetweenVerticalPipes[i] +
                            maxPipeOffset*0.47f + gapBetweenPipes/10,
                    birdSize * 2,
                    (Gdx.graphics.height*2/3).toFloat()
            )

            if ((Intersector.overlaps(birdCircle, topPipeRectangle[i])) || (Intersector.overlaps(birdCircle, bottomPipeRectangle[i]))){
                Gdx.app.log("Collision", "Detected!")
                gameState = 2
            }
        }
        batch!!.end()
        //shapeRenderer ends
    }

}

