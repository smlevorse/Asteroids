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

void setup(){
  size(1600, 900);
  background(20);
  
  //set the state to main menu
  state = 0;
}

void draw(){
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

void keyPressed(){
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
      ship.angularVelocity = -.1;
    }
    else if(keyCode == RIGHT || key == 'D' || key == 'd'){
      ship.angularVelocity = .1;
    }
    //up or w propels the ship
    else if(keyCode == UP || key == 'W' || key == 'w'){
      ship.acceleration = -.3;
    }
    //holding space autofires
    else if(key == ' '){
      firing = true; 
    }
  }
}

void keyReleased(){
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
boolean detectCollision(float x1, float y1, float rad1, float x2, float y2, float rad2){
  return (pow(rad1 + rad2, 2) > (pow(x1 - x2, 2) + pow(y1 - y2, 2)));
}