import processing.core.PApplet;
import java.util.ArrayList;
import processing.core.PImage;

public class Sketch extends PApplet {
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
  boolean boolStartScreen = true;
  int intNewLevelTimer = 0;

  // Projectiles
  ArrayList<Projectile> arrProjectiles;
  int intShootInterval = 10; 
  int intShootTimer = 0;
  boolean boolAllDirectionShooting = false;
  int intPowerUpDuration = 300;
  int intPowerUpTimer = 0;

  // Power-ups
  ArrayList<PowerUp> arrPowerUps;

  // Images
  PImage imgAlien2;
  PImage imgAlien3;
  PImage imgSpaceship;
  PImage imgPowerup;
  PImage imgHealth;

  // Score
  int intScore = 0;

  public void settings() {
    size(400, 400);
  }

  public void setup() {
    imgAlien2 = loadImage("Alien2.png");
    imgAlien3 = loadImage("Alien3.png");
    imgSpaceship = loadImage("Spaceship.png");
    imgPowerup = loadImage("powerup.png");
    imgHealth = loadImage("heart.png");

    initializeAliens();
    arrProjectiles = new ArrayList<Projectile>();
    arrPowerUps = new ArrayList<PowerUp>();

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
    if (boolStartScreen) {
      drawStartScreen();
      return;
    }

    if (boolGameOver) {
      drawGameOverScreen();
      return;
    }

    // Background color change
    background(50 + intLevel * 10 % 256, 50, 50);

    // Player/Spaceship
    image(imgSpaceship, fltPlayerX - fltPlayerSize / 2, fltPlayerY - fltPlayerSize / 2, fltPlayerSize, fltPlayerSize);

    // Lives
    fill(255, 0, 0);
    for (int i = 0; i < intPlayerLives; i++) {
      image(imgHealth, width - 20 * (i + 1) - 10, 10, 20, 20);
    }

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

    // Draw and move projectiles
    fill(0, 255, 0);
    for (int i = arrProjectiles.size() - 1; i >= 0; i--) {
      Projectile p = arrProjectiles.get(i);
      p.move();
      p.display();

      // Check for collision with aliens
      for (int j = 0; j < totalAliens; j++) {
        if (!boolAlienHideStatus[j] && dist(p.x, p.y, fltAlienX[j], fltAlienY[j]) < 10) {
          boolAlienHideStatus[j] = true;
          arrProjectiles.remove(i);
          intScore += 10 * intLevel;
          break;
        }
      }

      // Remove projectiles that are out of bounds
      if (p.y < 0 || p.y > height || p.x < 0 || p.x > width) {
        arrProjectiles.remove(i);
      }
    }

    // Shooting projectiles
    intShootTimer++;
    if (intShootTimer >= intShootInterval) {
      arrProjectiles.add(new Projectile(fltPlayerX, fltPlayerY));
      if (boolAllDirectionShooting) {
        arrProjectiles.add(new Projectile(fltPlayerX, fltPlayerY, 0, -7));
        arrProjectiles.add(new Projectile(fltPlayerX, fltPlayerY, 0, 7));
        arrProjectiles.add(new Projectile(fltPlayerX, fltPlayerY, -7, 0));
        arrProjectiles.add(new Projectile(fltPlayerX, fltPlayerY, 7, 0));
      }
      intShootTimer = 0;
    }

    // Power-up timer
    if (boolAllDirectionShooting) {
      intPowerUpTimer++;
      if (intPowerUpTimer >= intPowerUpDuration) {
        boolAllDirectionShooting = false;
        intPowerUpTimer = 0;
      }
    }

    // Draw and check for power-up collisions
    for (int i = arrPowerUps.size() - 1; i >= 0; i--) {
      PowerUp pu = arrPowerUps.get(i);
      pu.display();
      pu.move();

      if (dist(fltPlayerX, fltPlayerY, pu.x, pu.y) < fltPlayerSize / 2 + 10) {
        if (pu.type.equals("allDirections")) {
          boolAllDirectionShooting = true;
        } else if (pu.type.equals("extraLife")) {
          intPlayerLives++;
        }
        arrPowerUps.remove(i);
      }

      if (pu.y > height) {
        arrPowerUps.remove(i);
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

      // Occasionally drop power-ups
      if (intLevel % 3 == 0) {
        arrPowerUps.add(new PowerUp(random(width), 0, "allDirections"));
      } else if (intLevel % 5 == 0) {
        arrPowerUps.add(new PowerUp(random(width), 0, "extraLife"));
      }
    }

    // Check game over
    if (intPlayerLives <= 0) {
      boolGameOver = true;
    }
  }

  public void drawStartScreen() {
    background(0);
    fill(255);
    textSize(32);
    textAlign(CENTER, CENTER);
    text("Space Shooter", width / 2, height / 3);

    textSize(16);
    text("Press Start to Play", width / 2, height / 2);

    fill(0, 255, 0);
    rect(width / 2 - 50, height / 2 + 40, 100, 30);
    fill(0);
    textSize(16);
    text("START", width / 2, height / 2 + 55);

    image(imgSpaceship, width / 2 - 25, height / 2 + 80, 50, 50);
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
    if (boolStartScreen) {
      return;
    }

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
    if (boolStartScreen) {
      return;
    }

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
    if (boolStartScreen) {
      if (mouseX > width / 2 - 50 && mouseX < width / 2 + 50 && mouseY > height / 2 + 40 && mouseY < height / 2 + 70) {
        boolStartScreen = false;
      }
      return;
    }

    int totalAliens = intAliensPerLevel * intLevel;
    for (int i = 0; i < totalAliens; i++) {
      if (!boolAlienHideStatus[i] && dist(mouseX, mouseY, fltAlienX[i], fltAlienY[i]) < 10) {
        boolAlienHideStatus[i] = true;
      }
    }
  }

  // Projectile class
  public class Projectile {
    float x, y;
    float speedX, speedY;

    Projectile(float startX, float startY) {
      x = startX;
      y = startY;
      speedX = 0;
      speedY = -7;
    }

    Projectile(float startX, float startY, float sX, float sY) {
      x = startX;
      y = startY;
      speedX = sX;
      speedY = sY;
    }

    void move() {
      x += speedX;
      y += speedY;
    }

    void display() {
      ellipse(x, y, 5, 10);
    }
  }

  // Power-up class
  public class PowerUp {
    float x, y;
    String type;
    float speedY = 3;

    PowerUp(float startX, float startY, String powerUpType) {
      x = startX;
      y = startY;
      type = powerUpType;
    }

    void move() {
      y += speedY;
    }

    void display() {
      if (type.equals("allDirections")) {
        image(imgPowerup, x - 10, y - 10, 20, 20);
      } else if (type.equals("extraLife")) {
        image(imgHealth, x - 10, y - 10, 20, 20);
      }
    }
  }

  public static void main(String[] args) {
    PApplet.main("Sketch");
  }
}
