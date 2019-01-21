class Fighter {
  float unadjustedFitness;
  float fitness;
  Genome brain;
  int bestScore = 0;
  boolean dead;
  int score;
  int gen = 0;
  int genomeInputs = 4;
  int genomeOutputs = 4;
  int dotsCollected = 0;
  int myNum = (int)random(50, 100);
  float lasted = 0;
  float FighterSpeed = 5;
  long UID = (long)random(4234424);
  float[] vision = new float[genomeInputs];//the input array fed into the neuralNet 
  float[] decision = new float[genomeOutputs]; //the out put of the NN 
  //-------------------------------------
  int hunger =  400;
  float posX = random(width);//random(width);width/2;//
  float posY = random(height);//random(height);height/2;//
  
  float posWidth = 10;
  
  
  Fighter() {
    UID = (long)random(10000000);
    frameCount = 0;
    lasted = 0;
    brain = new Genome(genomeInputs, genomeOutputs);
  }
  
  void calculateFitness() {
    fitness = lasted / 1000;
  }
  
  //----------------------------------------------------------------------------------------------------------------------------------------------------------
  
  
  void moveUp(float amount) {
    float newY = posY + amount;
    if (newY > borderThickness  && newY < height - borderThickness) {
      posY = newY;
    } else {
      if(posY >= height + 9) {
        posY = -8;
      }
      if(posY <= -9) {
        posY = height + 8;
      }
    }
  }
  void moveDown(float amount) {
    float newY = posY - amount;
    if (newY > borderThickness  && newY < height - borderThickness) {
      posY = newY;
    } else {
      if(posY >= height + 9) {
        posY = -8;
      }
      if(posY <= -9) {
        posY = height + 8;
      }
    }
  }
  
  void collectDots() {
   for(int i = obstacles.size() - 1; i >= 1; i--) {
     Dot dot = obstacles.get(i);
     if (distance(dot.posX, dot.posY, this.posX, this.posY) < (posWidth)) {
       dotsCollected += 1;
       hunger += 100;
       dot.posX = random(width);
       dot.posX = random(height);
       break;
     }
    }

  }
  
  
  void moveLeft(float amount) {
    float newX = posX - amount;
    if (newX > 0  && newX < width) {
      posX = newX;
    } else {
      if(posX >= width) {
        posX = 0;
      }
      if(posX <= -9) {
        posX = width;
      }
    }
  }
  void moveRight(float amount) {
    float newX = posX + amount;
    if (newX > 0 && newX < width) {
      posX = newX;
    } else {
      if(posX >= width + 9) {
        posX = -8;
      }
      if(posX <= -9) {
        posX = width + 8;
      }
    }
  }  

  float distance(float Tx, float Ty, float Rx, float Ry) {
    return (float)Math.hypot(Tx - Rx, Ty - Ry);
  }
  
  float[] closestDot() {
    float X = 0;
    float Y = 0;
    Asteroid closestDot = bullets.get(bullets.size() - 1);
    for (int i = bullets.size() - 1; i >= 1; i--) {
      Asteroid obstacle = bullets.get(i);
      if (distance(posX, posY, obstacle.Px, obstacle.Py) <= distance(posX, posY, closestDot.Px, closestDot.Py)) {
        closestDot = obstacle;
        X = closestDot.Px;
        Y = closestDot.Py;
      }
    }
    float[] answer = new float[2];
    answer[0] = X;
    answer[1] = Y;
    return answer; 
  }
  
  
  void populateInputs() {
    //vision[0] = -10;
    vision[0] = 500/(posX*width);
    vision[1] = 500/(posY*height);
    float[] answer = closestDot();
    vision[2] = 500/((answer[1] - posY)*height);
    //vision[4] = answer[1];
    vision[3] = 500/((answer[0] - posX)*width);
    //float[] answers = closestDotEat();
    //vision[4] = 500/((answers[1] - posY)*height);
    //vision[4] = answer[1];
    //vision[5] = 500/((answers[0] - posX)*width);
    //OP ANSWERS
    //if(answer[0] >= posX) {
    //  vision[3]= -100;
    //}else{
    //  vision[3]= 100;;
    //}
    //if(answer[1] >= posY) {
    //  vision[4]= -100;
    //}else{
    //  vision[4]= 100;
    //}

  }
  float[] closestDotEat() {
    float X = 0;
    float Y = 0;
    Dot closestDot = obstacles.get(obstacles.size() - 1);
    for (int i = obstacles.size() - 1; i >= 1; i--) {
      Dot obstacle = obstacles.get(i);
      if (distance(posX, posY, obstacle.posX, obstacle.posY) <= distance(posX, posY, closestDot.posX, closestDot.posY)) {
        closestDot = obstacle;
        X = closestDot.posX;
        Y = closestDot.posY;
      }
    }
    float[] answer = new float[2];
    answer[0] = X;
    answer[1] = Y;
    return answer; 
  }
  void useOutputs() {
    decision = brain.feedForward(vision);
    moveUp(decision[0] * posWidth * 2);
    moveDown(decision[1] * posWidth * 2);
    moveLeft(decision[2] * posWidth * 2);
    moveRight(decision[3] * posWidth * 2);
    
    //Fighter closestEnemy = getClosestEnemy();
    //posD = (float)Math.atan((posY-closestEnemy.posY)/(posX-closestEnemy.posX));
  }
  
  void update() {
    hunger -= 1;
    collectDots();
    lasted = frameCount;

    for(int i = bullets.size() - 1; i >= 0; i--) {
      Asteroid a = bullets.get(i);
        if(distance(posX, posY, a.Px, a.Py) < posWidth) {
          dead = true;
        }
    }
    if(frameCount > 5000 || pop.done()) {
      dead =  true;
      hunger = 200;
      dotsCollected = 1;
    }

  }
  
    
  //---------------------------------------------------------------------------------------------------------------------------------------------------------  
  Fighter clone() {
    Fighter clone = new Fighter();
    clone.brain = brain.clone();
    clone.fitness = fitness;
    clone.brain.generateNetwork(); 
    clone.gen = gen;
    clone.bestScore = score;
    return clone;
  }
  Fighter crossover(Fighter parent2) {
    Fighter child = new Fighter();
    child.brain = brain.crossover(parent2.brain);
    child.brain.generateNetwork();
    return child;
  }
}
  //--------------------------------------------------------------------------------------------------------------------------------------------------------
