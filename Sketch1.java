import processing.core.PApplet;
import java.util.ArrayList;
import processing.core.PImage;

public class Sketch1 extends PApplet {
  // Aliens
  float[] fltAlienX;
  float[] fltAlienY;
  float[] fltAlienSpeed;
  boolean[] boolAlienHideStatus;
  int intAliens = 5; 
  float fltSpeed = 1;
  int intLevel = 1;
  int intAliensPerLevel = 3; 

  // Spaceship/player
  float fltPlayerX, fltPlayerY;
  float fltPlayerSize = 20;
  int intPlayerLives = 3;

  // Movement and gameover
  boolean boolMoveLeft, boolMoveRight, boolMoveUp, boolMoveDown;
  boolean boolGameOver = false;
  boolean boolNewLevel = false;
  int intNewLevelTimer = 0;

  // Images
  PImage imgAlien2;
  PImage imgAlien3;
  PImage imgSpaceship;

  // Score
  int intScore = 0;

  public void settings() {
    size(400, 400);
  }

  public void setup() {
    imgAlien2 = loadImage("Alien2.png");
    imgAlien3 = loadImage("Alien3.png");
    imgSpaceship = loadImage("Spaceship.png");

    initializeAliens();

    // Adjust player size
    fltPlayerX = width / 2;
    fltPlayerY = height - 50;
  }

  public void initializeAliens() {
    int totalAliens = intAliensPerLevel * intLevel;
    fltAlienX = new float[totalAliens];
    fltAlienY = new float[totalAliens];
    fltAlienSpeed = new float[totalAliens];
    boolAlienHideStatus = new boolean[totalAliens];

    for (int i = 0; i < totalAliens; i++) {
      fltAlienX[i] = random(width);
      fltAlienY[i] = random(-200, 0); // Spawn from top
      fltAlienSpeed[i] = random(1, 3 + intLevel); // Increase speed with levels
      boolAlienHideStatus[i] = false;
    }
  }

  public void draw() {
    if (boolGameOver) {
      drawGameOverScreen();
      return;
    }

    // Background color change
    background(50 + intLevel * 10 % 256, 50, 50);

    // Player/Spaceship
    image(imgSpaceship, fltPlayerX - fltPlayerSize / 2, fltPlayerY - fltPlayerSize / 2, fltPlayerSize, fltPlayerSize);

    // Score
    fill(255);
    textSize(16);
    textAlign(LEFT, TOP);
    text("Score: " + intScore, 10, 10);

    // Level
    textAlign(CENTER, TOP);
    text("Level: " + intLevel, width / 2, 10);

    if (boolNewLevel) {
      textSize(32);
      textAlign(CENTER, CENTER);
      text("Next Wave!", width / 2, height / 2);
      intNewLevelTimer++;
      if (intNewLevelTimer > 60) {
        boolNewLevel = false;
        intNewLevelTimer = 0;
      }
    }

    // Movement
    if (boolMoveLeft) {
      fltPlayerX -= 5;
    }
    if (boolMoveRight) {
      fltPlayerX += 5;
    }
    if (boolMoveUp) {
      fltPlayerY -= 5;
    }
    if (boolMoveDown) {
      fltPlayerY += 5;
    }

    // Check player position using size
    if (fltPlayerX < fltPlayerSize / 2) {
      fltPlayerX = fltPlayerSize / 2;
    }
    if (fltPlayerX > width - fltPlayerSize / 2) {
      fltPlayerX = width - fltPlayerSize / 2;
    }
    if (fltPlayerY < fltPlayerSize / 2) {
      fltPlayerY = fltPlayerSize / 2;
    }
    if (fltPlayerY > height - fltPlayerSize / 2) {
      fltPlayerY = height - fltPlayerSize / 2;
    }

    // Draw aliens
    int totalAliens = intAliensPerLevel * intLevel;
    for (int i = 0; i < totalAliens; i++) {
      if (!boolAlienHideStatus[i]) {
        if (i % 2 == 0) {
          image(imgAlien2, fltAlienX[i] - 10, fltAlienY[i] - 10, 20, 20);
        } else {
          image(imgAlien3, fltAlienX[i] - 10, fltAlienY[i] - 10, 20, 20);
        }
        fltAlienY[i] += fltAlienSpeed[i] * fltSpeed;

        if (fltAlienY[i] > height) {
          fltAlienY[i] = random(-200, 0);
          fltAlienX[i] = random(width);
          fltAlienSpeed[i] = random(1, 3 + intLevel);
        }

        // Collision with player
        if (dist(fltPlayerX, fltPlayerY, fltAlienX[i], fltAlienY[i]) < fltPlayerSize / 2 + 10) {
          fltAlienY[i] = random(-200, 0);
          fltAlienX[i] = random(width);
          fltAlienSpeed[i] = random(1, 3 + intLevel);
          intPlayerLives--;
        }
      }
    }

    // Check if level is cleared
    boolean boolLevelCleared = true;
    for (int i = 0; i < totalAliens; i++) {
      if (!boolAlienHideStatus[i]) {
        boolLevelCleared = false;
        break;
      }
    }

    if (boolLevelCleared) {
      intLevel++;
      boolNewLevel = true;
      intScore += 100 * intLevel; 
      initializeAliens();
    }

    // Check game over
    if (intPlayerLives <= 0) {
      boolGameOver = true;
    }
  }

  public void drawGameOverScreen() {
    background(255);
    fill(0);
    textSize(32);
    textAlign(CENTER, CENTER);
    text("Game Over", width / 2, height / 2);
    textSize(16);
    text("Score: " + intScore, width / 2, height / 2 + 40);
  }

  public void keyPressed() {
    if (keyCode == UP) {
      fltSpeed = 0.5f;
    } else if (keyCode == DOWN) {
      fltSpeed = 2;
    } else if (key == 'a' || key == 'A') {
      boolMoveLeft = true;
    } else if (key == 'd' || key == 'D') {
      boolMoveRight = true;
    } else if (key == 'w' || key == 'W') {
      boolMoveUp = true;
    } else if (key == 's' || key == 'S') {
      boolMoveDown = true;
    }
  }

  public void keyReleased() {
    if (keyCode == UP || keyCode == DOWN) {
      fltSpeed = 1;
    } else if (key == 'a' || key == 'A') {
      boolMoveLeft = false;
    } else if (key == 'd' || key == 'D') {
      boolMoveRight = false;
    } else if (key == 'w' || key == 'W') {
      boolMoveUp = false;
    } else if (key == 's' || key == 'S') {
      boolMoveDown = false;
    }
  }

  public void mousePressed() {
    int totalAliens = intAliensPerLevel * intLevel;
    for (int i = 0; i < totalAliens; i++) {
      if (!boolAlienHideStatus[i] && dist(mouseX, mouseY, fltAlienX[i], fltAlienY[i]) < 10) {
        boolAlienHideStatus[i] = true;
      }
    }
  }

  public static void main(String[] args) {
    PApplet.main("Sketch1");
  }
}
