import processing.core.PApplet;
import java.util.ArrayList;
import processing.core.PImage;

public class PowerUpGame extends PApplet {

  int playerLives = 3; // Initial number of lives
  int score = 0; // Initial score
  int level = 1; // Initial level

  // Power-ups
  ArrayList<PowerUp> powerUps;
  ArrayList<Projectile> projectiles;
  PImage imgHealth;
  int shootInterval = 10; 
  int shootTimer = 0;
  boolean allDirectionShooting = false;
  int powerUpDuration = 300;
  int powerUpTimer = 0;

  boolean gameOver = false;
  boolean startGame = true;
  boolean levelCleared = false;
  int newLevelTimer = 0;

  public void settings() {
    size(400, 400);
  }

  public void setup() {
    imgHealth = loadImage("heart.png");

    powerUps = new ArrayList<PowerUp>();
    projectiles = new ArrayList<Projectile>();
  }

  public void draw() {
    if (startGame) {
      drawStartScreen();
  
      return;
    }

    if (gameOver) {
      drawGameOverScreen();
      return;
    }

    if (levelCleared) {
      drawLevelClearedScreen();
      return;
    }

    background(255);

    // Draw lives
    fill(255, 0, 0);
    for (int i = 0; i < playerLives; i++) {
      image(imgHealth, width - 20 * (i + 1) - 10, 10, 20, 20);
    }

    // Draw and check for power-up collisions
    for (int i = powerUps.size() - 1; i >= 0; i--) {
      PowerUp pu = powerUps.get(i);
      pu.display();
      pu.move();

      // If the player collects a power-up
      if (dist(mouseX, mouseY, pu.x, pu.y) < 20) { // Assuming player's mouse position for collection
        if (pu.type.equals("extraLife")) {
          playerLives++;
        }
        powerUps.remove(i);
      }
      
      // Remove power-ups that are out of bounds
      if (pu.y > height) {
        powerUps.remove(i);
      }
    }

    // Occasionally drop power-ups
    if (frameCount % 200 == 0) { // Drop a power-up every 200 frames
      powerUps.add(new PowerUp(random(width), 0, "extraLife"));
    }

    // Draw and move projectiles
    fill(0, 255, 0);
    for (int i = projectiles.size() - 1; i >= 0; i--) {
      Projectile p = projectiles.get(i);
      p.move();
      p.display();

      // Remove projectiles that are out of bounds
      if (p.y < 0 || p.y > height || p.x < 0 || p.x > width) {
        projectiles.remove(i);
      }
    }

    // Shooting projectiles
    shootTimer++;
    if (shootTimer >= shootInterval) {
      projectiles.add(new Projectile(mouseX, mouseY));
      if (allDirectionShooting) {
        projectiles.add(new Projectile(mouseX, mouseY, 0, -7));
        projectiles.add(new Projectile(mouseX, mouseY, 0, 7));
        projectiles.add(new Projectile(mouseX, mouseY, -7, 0));
        projectiles.add(new Projectile(mouseX, mouseY, 7, 0));
      }
      shootTimer = 0;
    }

    // Power-up timer
    if (allDirectionShooting) {
      powerUpTimer++;
      if (powerUpTimer >= powerUpDuration) {
        allDirectionShooting = false;
        powerUpTimer = 0;
      }
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
  }

  public void drawGameOverScreen() {
    background(255);
    fill(0);
    textSize(32);
    textAlign(CENTER, CENTER);
    text("Game Over", width / 2, height / 2);
    textSize(16);
    text("Score: " + score, width / 2, height / 2 + 40);
  }

  public void drawLevelClearedScreen() {
    background(255);
    fill(0);
    textSize(32);
    textAlign(CENTER, CENTER);
    text("Level Cleared", width / 2, height / 2);
    textSize(16);
    text("Score: " + score, width / 2, height / 2 + 40);
  }

  public void keyPressed() {
    if (startGame) {
      startGame = false;
    }
  }

  // Power-up class
  class PowerUp {
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
      if (type.equals("extraLife")) {
        image(imgHealth, x - 10, y - 10, 20, 20);
      }
    }
  }

  // Projectile class
  class Projectile {
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

  public static void main(String[] args) {
    PApplet.main("PowerUpGame");
  }
}