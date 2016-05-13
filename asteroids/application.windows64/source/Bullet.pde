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
  
  void display(){
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
  void collide(){
    //detect collision with bullets
    for(Asteroid a : asteroids){
      if(detectCollision(a.position.x, a.position.y, a.radius, position.x, position.y, 2.5)){
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
  void addScore(int radius){
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