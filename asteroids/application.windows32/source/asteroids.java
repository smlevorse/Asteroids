import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class asteroids extends PApplet {

//Sean Levorse
//Asteroids

Ship ship;  
ArrayList<Asteroid> asteroids;
int lastBullet;  //the last frame a bullet was fired
boolean firing;  //if space is being held
ArrayList<Bullet> bulletsToRemove;  //Bullets have have either collided with an asteroid or lasted too long
ArrayList<Asteroid> asteroidsToRemove;  //asteroids that have collided with a bullet
ArrayList<Asteroid> asteroidsToAdd;  //asteroids that are coming off of a broken up asteroid

int state;  //0 for menu, 1 for playing the game, 2 for game over
int score;  //the player's score
int level;  //the player's current level, used to determine how many of each asteroid to spawn

int framesBetweenBullets = 20;  //the number of frames between each bullet, set to 0 for tons of fun

public void setup(){
  
  background(20);
  
  //set the state to main menu
  state = 0;
}

public void draw(){
  //reset lists of things to do
  background(20);
  bulletsToRemove = new ArrayList<Bullet>();
  asteroidsToAdd = new ArrayList<Asteroid>();
  asteroidsToRemove = new ArrayList<Asteroid>();
  
  //main menu
  if(state == 0){
    //Draw the main menu
    fill(255);
    PFont font = createFont("CommodoreAngled.ttf", 150);
    textFont(font);
    textAlign(CENTER, BOTTOM);
    text("Asteroids", width/2, height/2 - 50);
    textSize(24);
    text("Press enter to start.", width/2, height/2 + 50);
    text("Controls", width/2, height/2 + 150);
    text("~", width/2, height/2 + 175);
    textAlign(LEFT, BOTTOM);
    text("Move Forward", width / 2 - 400, height / 2 + 200);
    text("W | Up Arrow", width/2 + 200, height/2 + 200);
    text("Turn Left", width / 2 - 400, height / 2 + 225);
    text("A | Left Arrow", width / 2 + 200, height/2 + 225);
    text("Turn Right", width / 2 - 400, height / 2 + 250);
    text("D | Right Arrow", width/2 + 200, height/2 + 250);
    text("Shoot", width / 2 - 400, height / 2 + 275);
    text("Space", width/2 + 200, height/2 + 275);
  }
  //game state
  else if(state == 1){
    //display and update bullets
    for(Bullet b : ship.bullets){
      b.display();
      b.collide();
    }
    
    //display and update asteroids
    for(Asteroid a : asteroids){
      a.update();
      a.display();
    }
    
    //display and update ship
    ship.update();
    ship.display();
    
    //remove dead bullets
    for(Bullet b : bulletsToRemove){
      ship.bullets.remove(b); 
    }
    
    //remove dead asteroids
    for(Asteroid a : asteroidsToRemove){
      asteroids.remove(a);
    }
    
    //add new asteroids
    for(Asteroid a : asteroidsToAdd){
      asteroids.add(a); 
    }
    
    //if the space bar is being held down, determine if we should fire a bullet
    if(firing){
      if( frameCount - framesBetweenBullets > lastBullet){
        //fire a bullet
        ship.fireBullet();
        lastBullet = frameCount;

      }
    }
    
    //health bar
    stroke(255);
    fill(255);
    PFont font = createFont("CommodoreAngled.ttf", 16);
    textFont(font);
    textAlign(LEFT, TOP);
    text("Health: ", 20, 20);
    fill(0, 0, 0, 0);
    rect(110, 20, 202, 20);
    //set the color of the health bar closer to red as health decreases
    stroke(map(ship.health, 0, 100, 255, 0), map(ship.health, 0, 100, 0, 255), 0 , 255);
    fill(map(ship.health, 0, 100, 255, 0), map(ship.health, 0, 100, 0, 255), 0 , 255);
    rect(111, 21, map(ship.health, 0, 100, 0, 200), 18);
    
    //Level, asteroid count and score
    stroke(255);
    fill(255);
    textAlign(CENTER, TOP);
    text("Level: " + level + "      Asteroids Remaining: " + asteroids.size(), width / 2, 20);
    textAlign(RIGHT, TOP);
    text("Score: " + score, width - 100, 20);
    
    //next level
    if(asteroids.size() == 0){
      level++;
      ship.health += 25;
      if(ship.health > 100){
        ship.health = 100; 
      }
      //every 9 levels, throw in another asteroid of size 200
      for(int i = 0; i < level / 9; i++){
        asteroids.add(new Asteroid(200));
      }
      //every 3 levels, throw in another asteroid of size 150
      for(int i = 0; i < level / 3 + 1; i++){
        asteroids.add(new Asteroid(150)); 
      }
      //on a 3 level cycle have 0, 1, or 2 asteroids of size 100
      for(int i = 0; i < level % 3; i++){
        asteroids.add(new Asteroid(100));
      }
      //each level increases the number of asteroids of size 50
      for(int i = 0; i < level; i++){
        asteroids.add(new Asteroid(50)); 
      }
    }
    
    //if the player dies, set the state to game over
    if (ship.health <= 0){
      state = 2; 
    }
  }
  //Game over state
  else if(state == 2){
    fill(255);
    PFont font = createFont("CommodoreAngled.ttf", 150);
    textFont(font);
    textAlign(CENTER, BOTTOM);
    text("GAME OVER", width/2, height/2 - 50);
    textSize(50);
    //display score
    text("Your score was " + score, width/2, height/2);
    textSize(24);
    text("Press enter to restart.", width/2, height/2 + 50);
    text("Controls", width/2, height/2 + 150);
    text("~", width/2, height/2 + 175);
    textAlign(LEFT, BOTTOM);
    text("Move Forward", width / 2 - 400, height / 2 + 200);
    text("W | Up Arrow", width/2 + 200, height/2 + 200);
    text("Turn Left", width / 2 - 400, height / 2 + 225);
    text("A | Left Arrow", width / 2 + 200, height/2 + 225);
    text("Turn Right", width / 2 - 400, height / 2 + 250);
    text("D | Right Arrow", width/2 + 200, height/2 + 250);
    text("Shoot", width / 2 - 400, height / 2 + 275);
    text("Space", width/2 + 200, height/2 + 275);
  }
}

public void keyPressed(){
  //on the menu and game over screens, if the user presses enter, reset the game and set the game state to the game
  if(state == 0 || state == 2){
    if(keyCode == ENTER){
      state = 1; 
      
      //reset the player and asteroid list
      ship = new Ship(new PVector(width/2, height/2));
      asteroids = new ArrayList<Asteroid>();
      
      //populate asteroids
      asteroids.add(new Asteroid(150)); 
      asteroids.add(new Asteroid(100)); 
      asteroids.add(new Asteroid(50)); 
      
      //Reset bullet firing
      firing = false;
      lastBullet = frameCount;
      
      //reset score and level
      score = 0;
      level = 1;
    }
  }
  
  //when the game is being played, keyboard input controls the ship
  if(state == 1){
    //left and right/a and d rotate the ship
    if(keyCode == LEFT || key == 'A' || key == 'a'){
      ship.angularVelocity = -.1f;
    }
    else if(keyCode == RIGHT || key == 'D' || key == 'd'){
      ship.angularVelocity = .1f;
    }
    //up or w propels the ship
    else if(keyCode == UP || key == 'W' || key == 'w'){
      ship.acceleration = -.3f;
    }
    //holding space autofires
    else if(key == ' '){
      firing = true; 
    }
  }
}

public void keyReleased(){
  //you only have to detect key releases in game
  //undo everything keyPressed() does
  if(state == 1){
    if(keyCode == LEFT || key == 'A' || key == 'a'){
      ship.angularVelocity = 0;
    }
    else if(keyCode == RIGHT || key == 'D' || key == 'd'){
      ship.angularVelocity = 0;
    }
    else if(keyCode == UP || key == 'W' || key == 'w'){
      ship.acceleration = 0;
    }
    else if(key == ' '){
      firing = false; 
    }
  }
}

//detects collision using hit circles. Uses x, y and radius of each object's hit circle
public boolean detectCollision(float x1, float y1, float rad1, float x2, float y2, float rad2){
  return (pow(rad1 + rad2, 2) > (pow(x1 - x2, 2) + pow(y1 - y2, 2)));
}
class Asteroid{
  int radius;                  //the approximate radius for collisions
  PShape asteroid;             //the shape of the asteroid
  PVector position, velocity;  //position and velocity
  float angle, angularVelocity;//angle for rotation and angular velocity
  
  //this constructor is for constructing an asteroid at a specific location given a specific velocity
  Asteroid(int r, PVector pos, PVector vel){
    //initialize values
    radius = r;
    angle = 0;
    angularVelocity = random(-.1f, .1f);
    
    //create the shape
    asteroid = createShape();
    int numVertices = PApplet.parseInt(random(8,20));
    int dist;  //the distance between the center of the asteroid's hit circle and the vertex
    int firstDist = 0;  //keeping track of the first distance so when we get back to the start of the asteroid shape we can close the shape off
    //make the shape
    asteroid.beginShape();
      //set fill and stroke
      asteroid.fill(255,255,255,0);
      asteroid.stroke(255);
      
      //rotate around the hit circle picking random vertices to give the asteroid jagged edges
      for(int i = 0; i < numVertices; i++){
        //get a random distance from the center
        dist = PApplet.parseInt(radius + radius / 8 * randomGaussian());
      
        //save the first distance
        if(i == 0){
          firstDist = dist;
        }
      
        //add the new vertex using polar to cartesian coordinates
        asteroid.vertex( dist * cos(TAU / numVertices * i), dist * sin(TAU / numVertices * i));
      }
    
      //close off the asteroid
      dist = firstDist;
      asteroid.vertex( dist, 0);
    asteroid.endShape();
    
    //set the position and velocity
    position = pos;
    velocity = vel;
    velocity.limit(10);
  }//assigned position vector
  
  //this constructor is for creating an asteroid at a random location off screen at a random veloctiy
  Asteroid(int r){
    //initialize everything
    radius = r;
    angle = 0;
    angularVelocity = random(-.1f, .1f);
    
    //create the shape
    asteroid = createShape();
    int numVertices = PApplet.parseInt(random(8,20));
    int dist;  //the distance between the center of the asteroid's hit circle and the vertex
    int firstDist = 0;  //keeping track of the first distance so when we get back to the start of the asteroid shape we can close the shape off
    //make the shape
    asteroid.beginShape();
      //set fill and stroke
      asteroid.fill(255,255,255,0);
      asteroid.stroke(255);
      
      //rotate around the hit circle picking random vertices to give the asteroid jagged edges
      for(int i = 0; i < numVertices; i++){
        //get a random distance from the center
        dist = PApplet.parseInt(radius + radius / 8 * randomGaussian());
      
        //save the first distance
        if(i == 0){
          firstDist = dist;
        }
      
        //add the new vertex using polar to cartesian coordinates
        asteroid.vertex( dist * cos(TAU / numVertices * i), dist * sin(TAU / numVertices * i));
      }
    
      //close off the asteroid
      dist = firstDist;
      asteroid.vertex( dist, 0);
    asteroid.endShape();
        
    //find a location off screen to spawn an asteroid
    float wall = random(0,4);  //choose a random wall
    if(wall < 1){ //the top of the screen
      position = new PVector( random(4 * radius, width - (4 * radius)), -2 * radius);
      velocity = new PVector( random(-5, 5), random(2.5f, 5));
    }
    else if(wall < 2){ //bottom of the screen
      position = new PVector( random(4 * radius, width - (4 * radius)), height + 2 * radius);
      velocity = new PVector( random(-5, 5), random(-2.5f, -5));
    }
    else if(wall < 3){ //the left side of the screen
      position = new PVector( -2 * radius, random(4 * radius, height - (4 * radius)));
      velocity = new PVector( random(2.5f, 5), random(-5, 5));
    }
    else{              //the right side of the screen
      position = new PVector( -2 * radius, random(4 * radius, height - (4 * radius)));
      velocity = new PVector( random(-2.5f, -5), random(-5, 5));
    }
  }//Random Constructor
  
  //shows the asteroid on the screen
  public void display(){
    pushMatrix();
      translate(position.x, position.y);
      rotate(angle);
      shape(asteroid);
      //stroke(255, 0, 0);                     //uncomment these 3 lines of code to see asteroid hit circles
      //fill(0, 0, 0, 0);
      //ellipse(0, 0, radius * 2, radius * 2);
    popMatrix();
  }
  
  //update the position based on the velocity and angle based on angular velocity
  public void update(){
    position.add(velocity);
    angle += angularVelocity;
    
    //check for collision with the ship
    collide();
    
    //limit asteroids to wrap around the screen
    if(position.x < -radius){
      position.x = width + radius;
    }
    if(position.x > width + radius){
      position.x = -radius;
    }
    if(position.y < -radius){
      position.y = height + radius; 
    }
    if(position.y > height + radius){
      position.y = -radius;
    }
  }//update() method
  
  //handle collisions between this asteroid and the ship
  public void collide(){
    //detect collision with the ship
    if(detectCollision(position.x, position.y, radius, ship.position.x, ship.position.y, 10)){
      //damage the ship
      ship.health -= 10.0f;
      
      //elastic collision
      //assign arbitrary mass based on size of asteroid
      float massShip = ((10.0f/25) * (10.0f/25));
      float massAsteroid = ((radius / 25.0f) * (radius / 25.0f));
      float totalMass = massShip + massAsteroid;
      //get copies of the velocity and position vectors
      PVector v1 = ship.velocity.copy();
      PVector v2 = velocity.copy();
      PVector x1 = ship.position;
      PVector x2 = position.copy();
      
      //forumal for elastic collisions
      ship.velocity.sub((PVector.sub(x1, x2).mult((PVector.sub(v1, v2).dot(PVector.sub(x1, x2)))/(PVector.sub(x1, x2).mag())).mult(2 * massAsteroid / totalMass)));
      ship.velocity.limit(10);
      velocity.sub((PVector.sub(x2, x1).mult((PVector.sub(v2, v1).dot(PVector.sub(x2, x1)))/(PVector.sub(x2, x1).mag())).mult(2 * massShip / totalMass)));
      velocity.limit(10);
    }
  }//collide() method
}//Asteroid class
class Bullet{
  
  PVector position, velocity;  //position and velocity of the bullet
  int life;  //number of frames left before the bullet "dies"
  
  //constructor
  Bullet(PVector pos, PVector vel){
    //set velociy, position and life left
    position = pos;
    velocity = vel;
    life = 100;
  }
  
  public void display(){
    //check to see if it's still alive
    if(life > 0){
      stroke(255);
      fill(255);
      
      //increment position and wrap around the screen
      position.add(velocity);
      position.x += width;
      position.x %= width;
      position.y += height;
      position.y %= height;
      
      //draw the bullet
      ellipse(position.x, position.y, 5, 5);
    }
    else{
      //remove the bullet if it's dead
      bulletsToRemove.add(this);
    }
    
    //keep track of remaining frames in bullets life
    life--;
  }//display() method
  
  //detect collision with asteroids
  public void collide(){
    //detect collision with bullets
    for(Asteroid a : asteroids){
      if(detectCollision(a.position.x, a.position.y, a.radius, position.x, position.y, 2.5f)){
        //if there is a collision with an asteroid, kill the bullet
        bulletsToRemove.add(this);
        //if the asteroid has a radius larger than 25, break it into three child asteroids
        if(a.radius > 25){
          //make two child asteroids with random velocity
          Asteroid child1 = new Asteroid(a.radius / 2, a.position.copy(), new PVector(random(-5, 5), random(-5, 5)));
          asteroidsToAdd.add(child1);
          Asteroid child2 = new Asteroid(a.radius / 2, a.position.copy(), new PVector(random(-5, 5), random(-5, 5)));
          asteroidsToAdd.add(child2);
          
          //keep kinetic energy constant to define the third child's velocity
          a.velocity.sub(child1.velocity);
          a.velocity.sub(child2.velocity);
          Asteroid child3 = new Asteroid(a.radius / 2, a.position.copy(), a.velocity.copy());
          asteroidsToAdd.add(child3);
        }
        
        //kill the asteroid that was hit
        asteroidsToRemove.add(a);
        
        //score based on the size of the asteroid
        addScore(a.radius);
      }
    } 
  }//collide() method
  
  //bigger asteroids are easier to hit and produce more score with children, so larger asteroids should be worth the least
  public void addScore(int radius){
    //the smallest asteroids are hard to hit and are necessary to move on, so they should be the most valuable
    if(radius <= 25){
      score += 300; 
    }
    else if(radius <= 50){
      score += 200;
    }
    else if(radius <= 100){
      score += 150;
    }
    else if(radius <= 150){
      score += 100;
    }
    else if(radius <= 200){
      score += 50;
    }
    else{
      score += 25; 
    }
  }//score(radius) method
}//Bullet class
class Ship{
  PVector position;            //location
  PVector velocity;            //velocity
  float acceleration;          //acceleration
  float angle;                 //angle
  float angularVelocity;       //angluar velocity
  float health;
 
  ArrayList<Bullet> bullets;   //the bullets the ship has fired
  PShape ship = new PShape();  //the shape of the ship
  
  //constructor
  Ship(PVector pos){
    //initialize values
    health = 100;
    position = pos;
    velocity = new PVector(0,0);
    acceleration = 0;
    angle = 0;
    
    //create the ship shape
    ship = createShape();
    ship.beginShape();
      ship.fill(255,255,255,0);
      ship.stroke(255);
      ship.vertex(0,-20);
      ship.vertex(10, 10);
      ship.vertex(0, 5);
      ship.vertex(-10,10);
      ship.vertex(0,-20);
    ship.endShape();
    
    //initialize the list of bullets
    bullets = new ArrayList<Bullet>();
  }//constructor
  
  //displays the ship
  public void display(){
    pushMatrix();
      translate(position.x, position.y);
      rotate(angle);
      shape(ship);
      //stroke(0, 255, 0);              //uncomment these three lines of code to see ship hit circle
      //fill(0, 0, 0, 0);
      //ellipse(0, 0, 20, 20);
    popMatrix();
  }
  
  //updates the ship's position and all of the bullets
  public void update(){
    //rotate the ship
    angle += angularVelocity;
    
    //get the acceleration vector based on if there is an acceleration and rotate it to angle
    PVector accelerationVector = new PVector(0, acceleration);
    accelerationVector.rotate(angle);
    
    //set velocity, limit the velocit and set the position.
    velocity.add(accelerationVector);
    velocity.limit(10);
    position.add(velocity);
    
    //decelerate in space, but still drift a lot
    velocity.mult(.99f);
    
    //wrap around the other side of the screen
    if(position.x < -20){
      position.x = width + 20;
    }
    if(position.x > width + 20){
      position.x = -20;
    }
    if(position.y < -20){
      position.y = height + 20; 
    }
    if(position.y > height + 20){
      position.y = -20;
    }
  }//update() method
  
  //creates a new bullet and shoots it
  public void fireBullet(){
    PVector bulletPos = new PVector(position.x, position.y);
    PVector bulletVel = new PVector(0, -15 - velocity.mag());
    bulletVel.rotate(angle);
    bullets.add(new Bullet(bulletPos, bulletVel));
  }
}
  public void settings() {  size(1600, 900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "asteroids" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
