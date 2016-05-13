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
    angularVelocity = random(-.1, .1);
    
    //create the shape
    asteroid = createShape();
    int numVertices = int(random(8,20));
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
        dist = int(radius + radius / 8 * randomGaussian());
      
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
    angularVelocity = random(-.1, .1);
    
    //create the shape
    asteroid = createShape();
    int numVertices = int(random(8,20));
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
        dist = int(radius + radius / 8 * randomGaussian());
      
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
      velocity = new PVector( random(-5, 5), random(2.5, 5));
    }
    else if(wall < 2){ //bottom of the screen
      position = new PVector( random(4 * radius, width - (4 * radius)), height + 2 * radius);
      velocity = new PVector( random(-5, 5), random(-2.5, -5));
    }
    else if(wall < 3){ //the left side of the screen
      position = new PVector( -2 * radius, random(4 * radius, height - (4 * radius)));
      velocity = new PVector( random(2.5, 5), random(-5, 5));
    }
    else{              //the right side of the screen
      position = new PVector( -2 * radius, random(4 * radius, height - (4 * radius)));
      velocity = new PVector( random(-2.5, -5), random(-5, 5));
    }
  }//Random Constructor
  
  //shows the asteroid on the screen
  void display(){
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
  void update(){
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
  void collide(){
    //detect collision with the ship
    if(detectCollision(position.x, position.y, radius, ship.position.x, ship.position.y, 10)){
      //damage the ship
      ship.health -= 10.0;
      
      //elastic collision
      //assign arbitrary mass based on size of asteroid
      float massShip = ((10.0/25) * (10.0/25));
      float massAsteroid = ((radius / 25.0) * (radius / 25.0));
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