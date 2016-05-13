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
  void display(){
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
  void update(){
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
    velocity.mult(.99);
    
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
  void fireBullet(){
    PVector bulletPos = new PVector(position.x, position.y);
    PVector bulletVel = new PVector(0, -15 - velocity.mag());
    bulletVel.rotate(angle);
    bullets.add(new Bullet(bulletPos, bulletVel));
  }
}