//Globals
Population pop;
Fighter genPlayerTemp;
boolean showNothing = false;
int upToGen = 0;
int nextConnectionNo = 1000;
//Noise
int frameCheck = 0;
//Config
int framerate = 600;
int borderThickness = 0;


int Dots = 30;
int Asteroids = 60;
ArrayList<Dot> obstacles = new ArrayList<Dot>();
ArrayList<Asteroid> bullets = new ArrayList<Asteroid>();
void setup() {
  size(500, 500);
  //fullScreen();
  frameRate(framerate);
  int PopulationTop = 20;
  for(int i = 0; i < Asteroids; i++) {
    bullets.add(new Asteroid());
  }
  for (int i = Dots; i >= 0; i--) {
    obstacles.add(new Dot(0, 255, 255));
  }
  //Set Values and et al.
  pop = new Population(PopulationTop);
}

void draw() {
    
    
    if(!showNothing) {
    background(0);
    for(int i = bullets.size() - 1; i >= 0; i--) {
      Asteroid a = bullets.get(i);
      a.draw();
    }
    for (int i = obstacles.size() - 1; i >= 0; i--) {
      //obstacles.get(i).draw();
    }
    
    } else {
    for(int i = bullets.size() - 1; i >= 0; i--) {
      Asteroid a = bullets.get(i);
      a.move();
    }
    

    
    }
    if (!pop.done()) {//if any players are alive then update them
      pop.updateAlive();
    } else {
      frameCount = 0;
      pop.naturalSelection();
      for(int i = bullets.size() - 1; i >= 0; i--) {
        Asteroid a = bullets.get(i);
        a.r();
      }
      //obstacles = new ArrayList<Dot>();
      for (int i = Dots; i >= 0; i--) {
        obstacles.get(i).r();
      }
    }
    if(!showNothing) {
      drawFighters();
    }

  
}

void drawFighters() {
  for (int i = pop.pop.size() - 1; i >= 0; i--) {
    Fighter fighter = pop.pop.get(i);
    stroke(0, 255, 255);
    fill(0, 255, 255);  
    strokeWeight(1);
    if(!fighter.dead)  {
      ellipse(fighter.posX, fighter.posY, fighter.posWidth, fighter.posWidth);
    } else {
      fighter.posX = -1000;
      fighter.posY = -1000;
    }
  }
}

void drawBrain() {  //show the brain of whatever genome is currently showing
  if (!showNothing) {
    fill(255);
    textAlign(LEFT);
    textSize(width/50);
    text("Leader's Score: " + pop.pop.get(0).score, 30, height - 30);
    textAlign(RIGHT);
    text("Frame #: " + frameCount, width, height - 30);
    //Genome Size config
    //Genome drawing config
    for(int i = 0; i < pop.pop.size() - 1; i+=1) {
      //pop.pop.get((int)random(0, pop.pop.size() - 1)).brain.drawGenome((i*250) + 10, 10, 240, 100, 10);
      pop.pop.get(0).brain.drawGenome((i*250) + 10, 10, 240, 100, 10);
    }

  }
}
